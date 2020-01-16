package com.jobma.employer.application;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.widget.Toast;

import com.google.android.exoplayer2.offline.DownloadManager;
import com.google.android.exoplayer2.offline.DownloaderConstructorHelper;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.upstream.FileDataSourceFactory;
import com.google.android.exoplayer2.upstream.HttpDataSource;
import com.google.android.exoplayer2.upstream.cache.Cache;
import com.google.android.exoplayer2.upstream.cache.CacheDataSource;
import com.google.android.exoplayer2.upstream.cache.CacheDataSourceFactory;
import com.google.android.exoplayer2.upstream.cache.NoOpCacheEvictor;
import com.google.android.exoplayer2.upstream.cache.SimpleCache;
import com.google.android.exoplayer2.util.Util;
import com.jobma.employer.BuildConfig;
import com.jobma.employer.R;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.exoPlayer.DownloadTracker;
import com.jobma.employer.util.Constants;
import com.jobma.employer.util.ObjectUtil;

import java.io.File;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class GlobalApplication extends Application {

    private String packageName;
    private Typeface fontAwesomeTypeface;
    private SessionSecuredPreferences loginPref;

    private static final String DOWNLOAD_ACTION_FILE = "actions";
    private static final String DOWNLOAD_TRACKER_ACTION_FILE = "tracked_actions";
    private static final String DOWNLOAD_CONTENT_DIRECTORY = "downloads";
    private static final int MAX_SIMULTANEOUS_DOWNLOADS = 2;
    protected String userAgent;
    private File downloadDirectory;
    private Cache downloadCache;
    private DownloadManager downloadManager;
    private DownloadTracker downloadTracker;

    @Override
    public void onCreate() {
        super.onCreate();

        this.setAppInstance();

        userAgent = Util.getUserAgent(this, "ExoPlayerDemo");

        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("opensans_regular.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build()
        );

    }

    protected void setAppInstance() {
        ApplicationHelper.setApplicationObj(this);
    }

    public Typeface getTypeface() {
        if (this.fontAwesomeTypeface == null) {
            this.initIcons();
        }
        return this.fontAwesomeTypeface;
    }

    public void initIcons() {
        this.fontAwesomeTypeface = Typeface.createFromAsset(this.getAssets(), "fontawesome-webfont.ttf");
    }

    public SessionSecuredPreferences loginPreferences(String preferenceName) {
        if (this.loginPref == null) {
            this.loginPref = new SessionSecuredPreferences(this.getBaseContext(), this.getBaseContext().getSharedPreferences(preferenceName, Context.MODE_PRIVATE));
        }
        return this.loginPref;
    }

    public Context getContext() {
        return this.getApplicationContext();
    }

    public String phoneFormat() {
        return Constants.phoneFormat;
    }

    public int getResourceId(String resourceName, String defType) {
        return getResourceId(resourceName, defType, this.packageName());
    }

    public int getResourceId(String resourceName, String defType, String packageName) {
        return (!ObjectUtil.isNumber(resourceName) && ObjectUtil.isNonEmptyStr(resourceName)) ? this.getResources().getIdentifier(resourceName, defType, packageName) : 0;
    }

    public PackageInfo packageInfo() {
        PackageManager manager = this.getPackageManager();
        PackageInfo info = null;
        try {
            info = manager.getPackageInfo(this.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e2) {
            Toast.makeText(this.getBaseContext(), "Package Not Found : " + e2, Toast.LENGTH_SHORT).show();
        }
        return info;
    }

    public String packageName() {
        if (this.packageName == null) {
            PackageInfo info = this.packageInfo();
            this.packageName = info != null ? info.packageName : "com.jobma.employer";
        }
        return this.packageName;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {

        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    public DataSource.Factory buildDataSourceFactory() {
        DefaultDataSourceFactory upstreamFactory = new DefaultDataSourceFactory(this, buildHttpDataSourceFactory());
        return buildReadOnlyCacheDataSource(upstreamFactory, getDownloadCache());
    }

    public HttpDataSource.Factory buildHttpDataSourceFactory() {
        return new DefaultHttpDataSourceFactory(userAgent);
    }

    public boolean useExtensionRenderers() {
        return "withExtensions".equals(BuildConfig.FLAVOR);
    }

    public DownloadManager getDownloadManager() {
        initDownloadManager();
        return downloadManager;
    }

    private synchronized void initDownloadManager() {
        if (downloadManager == null) {
            DownloaderConstructorHelper downloaderConstructorHelper = new DownloaderConstructorHelper(getDownloadCache(), buildHttpDataSourceFactory());
            downloadManager = new DownloadManager(downloaderConstructorHelper, MAX_SIMULTANEOUS_DOWNLOADS,
                    DownloadManager.DEFAULT_MIN_RETRY_COUNT,
                    new File(getDownloadDirectory(), DOWNLOAD_ACTION_FILE));
            downloadTracker = new DownloadTracker(this, buildDataSourceFactory(), new File(getDownloadDirectory(), DOWNLOAD_TRACKER_ACTION_FILE));
            downloadManager.addListener(downloadTracker);
        }
    }

    private synchronized Cache getDownloadCache() {
        if (downloadCache == null) {
            File downloadContentDirectory = new File(getDownloadDirectory(), DOWNLOAD_CONTENT_DIRECTORY);
            downloadCache = new SimpleCache(downloadContentDirectory, new NoOpCacheEvictor());
        }
        return downloadCache;
    }

    private File getDownloadDirectory() {
        if (downloadDirectory == null) {
            downloadDirectory = getExternalFilesDir(null);
            if (downloadDirectory == null) {
                downloadDirectory = getFilesDir();
            }
        }
        return downloadDirectory;
    }

    private static CacheDataSourceFactory buildReadOnlyCacheDataSource(DefaultDataSourceFactory upstreamFactory, Cache cache) {
        return new CacheDataSourceFactory(cache, upstreamFactory, new FileDataSourceFactory(), null,
                CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR, null);
    }

    public DownloadTracker getDownloadTracker() {
        initDownloadManager();
        return downloadTracker;
    }


}
