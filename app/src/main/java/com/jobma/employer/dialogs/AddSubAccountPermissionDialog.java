package com.jobma.employer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityAddSubAccount;
import com.jobma.employer.model.subAccounts.AddPermissionDialog;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

public class AddSubAccountPermissionDialog extends Dialog {

    private RecyclerView recpermission;
    private Context context;
    private ImageView btnCross;
    private ArrayList<AddPermissionDialog> permissionDialogs;

    public AddSubAccountPermissionDialog(@NonNull Context context, ArrayList<AddPermissionDialog> permissionDialogs) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        this.context = context;
        this.permissionDialogs = permissionDialogs;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_permissions);

        this.setCanceledOnTouchOutside(false);

        this.initView();
        this.recpermission.setAdapter(new DialogPermissionAdapter(context, permissionDialogs));
    }

    private void initView() {
        recpermission = findViewById(R.id.recpermission);
        btnCross = findViewById(R.id.imageView52);

        btnCross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                ((ActivityAddSubAccount) context).addSelectedPositions();
            }
        });
    }

    @Override
    public void show() {
        super.show();
    }


    //************************************ ADAPTER CLASS *******************************************
    public class DialogPermissionAdapter extends RecyclerView.Adapter<DialogPermissionAdapter.PermissionAdapterViewholder> {

        private Context context;
        private ArrayList<AddPermissionDialog> permissionList;


        public DialogPermissionAdapter(Context context, ArrayList<AddPermissionDialog> permissionList) {
            this.context = context;
            this.permissionList = permissionList;
        }

        @NonNull
        @Override
        public PermissionAdapterViewholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(context).inflate(R.layout.row_permission_dialog, viewGroup, false);
            return new PermissionAdapterViewholder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final PermissionAdapterViewholder viewholder, int position) {

            AddPermissionDialog addPermissionDialog = this.permissionList.get(position);
            viewholder.tvPermission.setText(addPermissionDialog.getPermission());

            if (addPermissionDialog.isChecked()) {
                viewholder.btnselect.setChecked(true);
            }

            viewholder.btnselect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    addPermissionDialog.setChecked(isChecked);
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
