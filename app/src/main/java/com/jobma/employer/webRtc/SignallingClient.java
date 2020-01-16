package com.jobma.employer.webRtc;

import android.annotation.SuppressLint;
import android.content.Context;

import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.liveInterview.EOEncodedResponseData;
import com.jobma.employer.util.ObjectUtil;

import org.json.JSONException;
import org.json.JSONObject;
import org.webrtc.IceCandidate;
import org.webrtc.SessionDescription;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.Arrays;
import java.util.UUID;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.SUB_USER;
import static com.jobma.employer.util.Constants.USER_TYPE;

public class SignallingClient {

    private static SignallingClient instance;
    private String roomName = null;
    private Socket socket;
    boolean isChannelReady = false;
    boolean isInitiator = false;
    boolean isStarted = false;
    private SignalingInterface callback;
    private EOEncodedResponseData encodedData;
    private String uniqueID;
    private SessionSecuredPreferences loginPreferences;
    private String userType;
    private GlobalProgressDialog progress;
    private Context context;

    private JSONObject peerLoggedInObject, payload, userDetails;
    private String toSocketId, roomId, userEmail, peerUserType;

    private boolean isVideoOfferFromPeer = false;
    private JSONObject videoOfferObject, videoOfferPayload, videoOfferSessionData;
    private String videoOfferFromSocketId, videoOfferToSocketId, videoOfferUUID;


    //This piece of code should not go into production!!
    //This will help in cases where the node server is running in non-https server and you want to ignore the warnings
    @SuppressLint("TrustAllX509TrustManager")
    private final TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[]{};
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType) {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType) {
        }
    }};

    public static SignallingClient getInstance() {
        if (instance == null) {
            instance = new SignallingClient();
        }
        return instance;
    }

    public void sendDataThroughSignal(SignalingInterface signalingInterface, EOEncodedResponseData eoEncodedData, Context context) {

        this.callback = signalingInterface;
        this.encodedData = eoEncodedData;
        this.context = context;
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.userType = loginPreferences.getString(USER_TYPE, "");
        this.progress = new GlobalProgressDialog(this.context);

        try {
            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, trustAllCerts, null);
            IO.setDefaultHostnameVerifier((hostname, session) -> true);
            IO.setDefaultSSLContext(sslcontext);
            this.socket = IO.socket("https://dev.jobma.com:9999");   //set the socket.io url here
            this.socket.connect();
            this.uniqueID = UUID.randomUUID().toString();

            if (!ObjectUtil.isEmpty(this.encodedData) && !ObjectUtil.isEmpty(socket) && ObjectUtil.isNonEmptyStr(userType)) {

                if (this.userType.equals(SUB_USER)) {
                    try {
                        JSONObject mainObject = new JSONObject();
                        mainObject.put("fromsocketid", socket.id());
                        mainObject.put("tosocketid", "");
                        mainObject.put("roomid", eoEncodedData.getToken());
                        JSONObject payloadObject = new JSONObject();
                        payloadObject.put("usermail", eoEncodedData.getCatcherEmail());
                        payloadObject.put("usertype", 5);
                        payloadObject.put("reinvite", "false");
                        mainObject.put("payload", payloadObject);

                        this.createOrJoinRoom(mainObject); //TODO form here create or join room if user is SUBUSER

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        JSONObject mainObject = new JSONObject();
                        mainObject.put("fromsocketid", socket.id());
                        mainObject.put("tosocketid", "");
                        mainObject.put("roomid", eoEncodedData.getToken());
                        JSONObject payloadObject = new JSONObject();
                        payloadObject.put("usermail", eoEncodedData.getCatcherEmail());
                        payloadObject.put("usertype", 3);
                        payloadObject.put("reinvite", "false");
                        mainObject.put("payload", payloadObject);

                        this.createOrJoinRoom(mainObject); //TODO form here create or join room if user is MAINUSER

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (URISyntaxException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

    }

    //1. CREATE_OR_JOIN_ROOM
    private void createOrJoinRoom(JSONObject message) {
        //emit CREATE_OR_JOIN_ROOM
        this.socket.emit("CREATE_OR_JOIN_ROOM", message);


        //room created event
        this.socket.on("ROOM_CREATED", args -> {
            System.out.println("room created : " + Arrays.toString(args));

            isInitiator = true;
            callback.onCreatedRoom();

        });

        this.socket.on("ROOM_NOT_FOUND", args -> {
            System.out.println("room not found : " + Arrays.toString(args));
        });

        this.socket.on("USER_INSIDE_ROOM", args -> {
            System.out.println("user inside room : " + Arrays.toString(args));

        });

        this.socket.on("ROOM_EXISTS_USER_JOINED", args -> {
            System.out.println("room exist user joined : " + Arrays.toString(args));

            //this.socket.emit("LOGGED_IN_ROOM", message);

            isInitiator = true;
            isChannelReady = true;
            callback.onJoinedRoom();
            callback.onTryToStart();

        });

        //emit LOGGED_IN_ROOM
        this.socket.emit("LOGGED_IN_ROOM", message);
        this.loggedInRoomCall();
    }

    //2. LOGGED_IN_ROOM
    private void loggedInRoomCall() {
        this.socket.on("ROOM_NOT_FOUND", args -> {
            System.out.println("logged in room not found : " + Arrays.toString(args));
        });

        this.socket.on("PEER_LOGGED_IN", args -> {
            System.out.println("peer logged in : " + Arrays.toString(args));

            this.isInitiator = true;
            this.isChannelReady = true;
            this.callback.onNewPeerJoined();
            this.callback.onTryToStart();

            isVideoOfferFromPeer = true; //this flag is used to check, when i created room first and waiting for others, then new peer joined
            //This callback is called when you create room and waiting for others then same room seeker joined,again emit data OFFER_SDP_FROM_PEER

            try {
                this.peerLoggedInObject = (JSONObject) args[0];
                this.toSocketId = peerLoggedInObject.getString("fromsocketid");
                this.roomId = peerLoggedInObject.getString("roomid");
                this.payload = peerLoggedInObject.getJSONObject("payload");
                this.userDetails = payload.getJSONObject("userDetails");
                this.userEmail = userDetails.getString("usermail");
                this.peerUserType = userDetails.getString("userType");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        });
    }

    //3 OFFER_SDP_FROM_PEER
    public void emitSessionData(SessionDescription message) {

        if (!ObjectUtil.isEmpty(this.encodedData) && !ObjectUtil.isEmpty(socket)) {
            if (this.userType.equals(SUB_USER)) {
                try {
                    JSONObject mainObject = new JSONObject();
                    mainObject.put("fromsocketid", this.socket.id());
                    mainObject.put("tosocketid", this.toSocketId);
                    mainObject.put("roomid", this.roomId);
                    JSONObject payloadObject = new JSONObject();
                    payloadObject.put("usermail", this.encodedData.getCatcherEmail());
                    payloadObject.put("usertype", 5);
                    payloadObject.put("uuid", this.uniqueID);
                    JSONObject sessionObj = new JSONObject();
                    sessionObj.put("type", "offer");
                    sessionObj.put("sdp", message.description);
                    payloadObject.put("sessionData", sessionObj);
                    mainObject.put("payload", payloadObject);

                    if (isVideoOfferFromPeer) {
                        this.socket.emit("OFFER_SDP_FROM_PEER", mainObject);
                        System.out.println("Offer sdp from peer :" + mainObject);
                    }

                    this.socket.on("VIDEO_ANS_FROM_PEER", args -> {
                        System.out.println("VIDEO_ANS_FROM_PEER offered by Peer: " + Arrays.toString(args));

                        try {
                            JSONObject videoAnswerObject = (JSONObject) args[0];
                            JSONObject payloadAnswerObject = videoAnswerObject.getJSONObject("payload");
                            JSONObject answerSessionDescriptionObject = payloadAnswerObject.getJSONObject("answerSessionDescription");
                            String type = answerSessionDescriptionObject.getString("type");
                            if (type.equalsIgnoreCase("answer")) {
                                this.callback.onAnswerReceived(videoAnswerObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    this.socket.on("VIDEO_OFFER_FROM_PEER", args -> {
                        System.out.println("VIDEO_OFFER_FROM_PEER111 : " + Arrays.toString(args));

                        //messages - SDP and ICE candidates are transferred through this
                        try {
                            this.videoOfferObject = (JSONObject) args[0];
                            this.videoOfferFromSocketId = this.videoOfferObject.getString("tosocketid");
                            this.videoOfferToSocketId = this.videoOfferObject.getString("fromsocketid");
                            this.roomId = this.videoOfferObject.getString("roomid");
                            this.videoOfferPayload = this.videoOfferObject.getJSONObject("payload");
                            this.userEmail = this.videoOfferPayload.getString("usermail");
                            this.videoOfferUUID = this.videoOfferPayload.getString("uuid");
                            this.videoOfferSessionData = this.videoOfferPayload.getJSONObject("sessionDescription");
                            String type = this.videoOfferSessionData.getString("type");
                            if (type.equalsIgnoreCase("offer")) {
                                callback.onOfferReceived(this.videoOfferObject);
                            } /*else if (type.equalsIgnoreCase("answer") && isStarted) {
                                callback.onAnswerReceived(data);
                            } else if (type.equalsIgnoreCase("candidate") && isStarted) {
                                callback.onIceCandidateReceived(data);
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });


                    if (WebRtcActivity.isOfferReceived && ObjectUtil.isNonEmptyStr(this.videoOfferFromSocketId) && ObjectUtil.isNonEmptyStr(this.videoOfferToSocketId)
                            && ObjectUtil.isNonEmptyStr(this.roomId) && ObjectUtil.isNonEmptyStr(this.userEmail) && ObjectUtil.isNonEmptyStr(this.videoOfferUUID)) {
                        JSONObject videoAnsObject = new JSONObject();
                        videoAnsObject.put("fromsocketid", this.videoOfferFromSocketId);
                        videoAnsObject.put("tosocketid", this.videoOfferToSocketId);
                        videoAnsObject.put("roomid", this.roomId);
                        JSONObject videoAnsPayloadObject = new JSONObject();
                        videoAnsPayloadObject.put("usermail", this.encodedData.getCatcherEmail());
                        videoAnsPayloadObject.put("usertype", 5);
                        videoAnsPayloadObject.put("uuid", this.videoOfferUUID);
                        JSONObject answerObject = new JSONObject();
                        answerObject.put("type", "answer");
                        answerObject.put("sdp", message.description);
                        videoAnsPayloadObject.put("answerData", answerObject);
                        videoAnsObject.put("payload", videoAnsPayloadObject);

                        this.socket.emit("VIDEO_ANS_FROM_PEER", videoAnsObject);
                        System.out.println("VIDEO_ANS_FROM_PEER112 :" + videoAnsObject);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {
                try {
                    JSONObject mainObject = new JSONObject();
                    mainObject.put("fromsocketid", this.socket.id());
                    mainObject.put("tosocketid", this.toSocketId);
                    mainObject.put("roomid", this.roomId);
                    JSONObject payloadObject = new JSONObject();
                    payloadObject.put("usermail", this.encodedData.getCatcherEmail());
                    payloadObject.put("usertype", 3);
                    payloadObject.put("uuid", this.toSocketId + "§" + this.socket.id());
                    JSONObject sessionObj = new JSONObject();
                    sessionObj.put("type", "offer");
                    sessionObj.put("sdp", message.description);
                    payloadObject.put("sessionData", sessionObj);
                    mainObject.put("payload", payloadObject);

                    if (isVideoOfferFromPeer) {
                        this.socket.emit("OFFER_SDP_FROM_PEER", mainObject);
                        System.out.println("Offer sdp from peer emit:" + mainObject);
                    }

                    this.socket.on("VIDEO_ANS_FROM_PEER", args -> {
                        System.out.println("VIDEO_ANS_FROM_PEER offered by Peer : " + Arrays.toString(args));

                        try {
                            JSONObject videoAnswerObject = (JSONObject) args[0];
                            this.videoOfferFromSocketId = this.socket.id();
                            this.videoOfferToSocketId = videoAnswerObject.getString("fromsocketid");
                            JSONObject payloadAnswerObject = videoAnswerObject.getJSONObject("payload");
                            this.videoOfferUUID = payloadAnswerObject.getString("uuid");
                            JSONObject answerSessionDescriptionObject = payloadAnswerObject.getJSONObject("answerSessionDescription");
                            String type = answerSessionDescriptionObject.getString("type");
                            if (type.equalsIgnoreCase("answer")) {
                                this.callback.onAnswerReceived(videoAnswerObject);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    });

                    this.socket.on("VIDEO_OFFER_FROM_PEER", args -> {
                        System.out.println("VIDEO_OFFER_FROM_PEER111 : " + Arrays.toString(args));

                        //messages - SDP and ICE candidates are transferred through this
                        try {
                            this.videoOfferObject = (JSONObject) args[0];
                            this.videoOfferFromSocketId = this.videoOfferObject.getString("tosocketid");
                            this.videoOfferToSocketId = this.videoOfferObject.getString("fromsocketid");
                            this.roomId = this.videoOfferObject.getString("roomid");
                            this.videoOfferPayload = this.videoOfferObject.getJSONObject("payload");
                            this.userEmail = this.videoOfferPayload.getString("usermail");
                            this.videoOfferUUID = this.videoOfferPayload.getString("uuid");
                            this.videoOfferSessionData = this.videoOfferPayload.getJSONObject("sessionDescription");
                            String type = this.videoOfferSessionData.getString("type");
                            if (type.equalsIgnoreCase("offer")) {
                                callback.onOfferReceived(this.videoOfferObject);
                            }
                            /*else if (type.equalsIgnoreCase("answer") && isStarted) {
                                callback.onAnswerReceived(data);
                            } else if (type.equalsIgnoreCase("candidate") && isStarted) {
                                callback.onIceCandidateReceived(data);
                            }*/

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    });

                    if (WebRtcActivity.isOfferReceived && ObjectUtil.isNonEmptyStr(this.videoOfferFromSocketId) && ObjectUtil.isNonEmptyStr(this.videoOfferToSocketId)
                            && ObjectUtil.isNonEmptyStr(this.roomId) && ObjectUtil.isNonEmptyStr(this.userEmail) && ObjectUtil.isNonEmptyStr(this.videoOfferUUID)) {
                        JSONObject videoAnsObject = new JSONObject();
                        videoAnsObject.put("fromsocketid", this.videoOfferFromSocketId);
                        videoAnsObject.put("tosocketid", this.videoOfferToSocketId);
                        videoAnsObject.put("roomid", this.roomId);
                        JSONObject videoAnsPayloadObject = new JSONObject();
                        videoAnsPayloadObject.put("usermail", this.encodedData.getCatcherEmail());
                        videoAnsPayloadObject.put("usertype", 3);
                        videoAnsPayloadObject.put("uuid", this.videoOfferUUID);
                        JSONObject answerObject = new JSONObject();
                        answerObject.put("type", "answer");
                        answerObject.put("sdp", message.description);
                        videoAnsPayloadObject.put("answerData", answerObject);
                        videoAnsObject.put("payload", videoAnsPayloadObject);

                        this.socket.emit("VIDEO_ANS_FROM_PEER", videoAnsObject);
                        System.out.println("VIDEO_ANS_FROM_PEER112 :" + videoAnsObject);
                    }

                    this.socket.on("ICE_RECVD", args -> {
                        System.out.println("ICE_RECVD : " + Arrays.toString(args));

                    });

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

    public void emitIceCandidate(IceCandidate iceCandidate) {
        try {
            JSONObject mainObject = new JSONObject();
            mainObject.put("fromsocketid", this.videoOfferFromSocketId);
            mainObject.put("tosocketid", this.videoOfferToSocketId);

            mainObject.put("roomid", this.roomId);
            JSONObject icePayload = new JSONObject();
            icePayload.put("uuid", this.videoOfferUUID);
            JSONObject iceObject = new JSONObject();
            iceObject.put("candidate", iceCandidate.sdp);
            iceObject.put("sdpMid", iceCandidate.sdpMid);
            iceObject.put("sdpMLineIndex", iceCandidate.sdpMLineIndex);
            icePayload.put("iceData", iceObject);
            mainObject.put("payload", icePayload);
            this.socket.emit("ICE_CANDIDATE", mainObject);
            System.out.println("ICE_CANDIDATE is emited : " + mainObject);

        } catch (Exception e) {
            e.printStackTrace();
        }

        this.socket.on("ICE_RECVD", args -> {
            System.out.println("ICE_RECVD :" + Arrays.toString(args));

        });
    }

    public void close() {
        this.socket.emit("DISCONNECT");
        this.socket.disconnect();
        this.socket.close();
    }

    interface SignalingInterface {

        void onRemoteHangUp(String msg);

        void onOfferReceived(JSONObject data);

        void onAnswerReceived(JSONObject data);

        void onIceCandidateReceived(JSONObject data);

        void onTryToStart();

        void onCreatedRoom();

        void onJoinedRoom();

        void onNewPeerJoined();
    }

}
