package com.jobma.employer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.model.account.EOForgetPassword;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class SubAccountsPermissionDialog extends Dialog {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;

    private RecyclerView recpermission;
    private List<String> permissionList = new ArrayList<>();
    private Context context;
    private ImageView btnCross;
    private String subUserId;
    private ArrayList<Integer> updatePermissionList = new ArrayList<>();

    public SubAccountsPermissionDialog(@NonNull Context context, String subUserId) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        this.context = context;
        this.subUserId = subUserId;

        this.progress = new GlobalProgressDialog(context);
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permissions);

        this.setCanceledOnTouchOutside(false);

        this.initView();
        this.addPermissionList();
        this.getPermissionsDialog(this.subUserId);
    }

    private void initView() {
        recpermission = findViewById(R.id.recpermission);
        btnCross = findViewById(R.id.imageView52);

        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                updatePermissionsDialog(subUserId, TextUtils.join(",", updatePermissionList));
            }
        });
    }

    private void addPermissionList() {
        permissionList.add("Evaluate Candidates");
        permissionList.add("Manage Sub Account");
        permissionList.add("Invite Candidates");
        permissionList.add("Integration Interviews");
        permissionList.add("Jobma Interviews");
        permissionList.add("Company Profile");
        permissionList.add("Company Video");
        permissionList.add("Interview Template");
    }

    private void updatePermissionsDialog(String subUserId, String permissions) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.updatePermissionSubAccounts(apiKey, subUserId, permissions).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword updatePermissions = response.body();
                        if (!ObjectUtil.isEmpty(updatePermissions)) {
                            if (updatePermissions.getError() == RESPONSE_SUCCESS) {
                                Toast.makeText(context, "" + updatePermissions.getMessage(), Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "" + updatePermissions.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void getPermissionsDialog(String subUserId) {
        if (!ObjectUtil.isEmpty(this.apiKey)) {
            progress.showProgressBar();
            apiInterface.getPermissionSubAccounts(apiKey, subUserId).enqueue(new Callback<EOForgetPassword>() {
                @Override
                public void onResponse(Call<EOForgetPassword> call, Response<EOForgetPassword> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {
                        EOForgetPassword permissions = response.body();
                        if (!ObjectUtil.isEmpty(permissions)) {
                            if (permissions.getError() == RESPONSE_SUCCESS) {
                                recpermission.setAdapter(new DialogPermissionAdapter(context, permissionList, permissions.getData()));
                            } else {
                                Toast.makeText(context, "" + permissions.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOForgetPassword> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(context, "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }


    @Override
    public void show() {
        super.show();
    }


    //************************************ ADAPTER CLASS *******************************************
    public class DialogPermissionAdapter extends RecyclerView.Adapter<DialogPermissionAdapter.PermissionAdapterViewholder> {

        private Context context;
        private List<String> permissionList;
        private String data;


        public DialogPermissionAdapter(Context context, List<String> permissionList, String data) {
            this.context = context;
            this.permissionList = permissionList;
            this.data = data;
        }

        @NonNull
        @Override
        public PermissionAdapterViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_permission_dialog, viewGroup, false);
            return new PermissionAdapterViewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PermissionAdapterViewholder viewholder, int position) {
            String[] permission = data.split(",");

            viewholder.tvPermission.setText(permissionList.get(position));

            for (String str : permission) {
                if (str.equalsIgnoreCase(String.valueOf(position + 1))) {
                    viewholder.btnselect.setChecked(true);
                    updatePermissionList.add(position + 1);
                }
            }

            viewholder.btnselect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewholder.btnselect.isChecked()) {
                        viewholder.btnselect.setChecked(true);
                        updatePermissionList.add(position + 1);
                    } else {
                        viewholder.btnselect.setChecked(false);
                        //TODO this is used to remove position from a particular array list, when uncheck dialog box
                        for (Iterator<Integer> itr = updatePermissionList.iterator(); itr.hasNext(); ) {
                            Integer integer = itr.next();
                            if (integer == (position + 1)) {
                                itr.remove();
                            }
                        }
                    }

                }
            });
        }

        @Override
        public int getItemCount() {
            return ObjectUtil.isEmpty(permissionList.size()) ? 0 : permissionList.size();
        }

        class PermissionAdapterViewholder extends RecyclerView.ViewHolder {
            private CheckBox btnselect;
            private TextView tvPermission;

            public PermissionAdapterViewholder(@NonNull View itemView) {
                super(itemView);
                tvPermission = itemView.findViewById(R.id.textView121);
                btnselect = itemView.findViewById(R.id.imageView53);
            }
        }
    }
}
