package com.jobma.employer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.model.reportIssue.GetUserList;
import com.jobma.employer.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverviewSubAccountAdapter extends RecyclerView.Adapter<OverviewSubAccountAdapter.SubAccountViewHolder> {

    private Context context;
    private ArrayList<GetUserList> subAccountList;

    public OverviewSubAccountAdapter(Context context, ArrayList<GetUserList> subAccountList) {
        this.context = context;
        this.subAccountList = subAccountList;
    }

    @NonNull
    @Override
    public SubAccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_dashboard_sub_account, viewGroup, false);
        return new SubAccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubAccountViewHolder viewHolder, int position) {
        GetUserList getUserList = subAccountList.get(position);

        viewHolder.tvName.setText(getUserList.getFullName());
        viewHolder.tvcredit.setText(getUserList.getApproval());
        viewHolder.tvpreRecorded.setText(String.valueOf(getUserList.getPrerecord()));
        viewHolder.tvLiveInterview.setText(String.valueOf(getUserList.getLiveinterivew()));
        if (!ObjectUtil.isEmpty(getUserList.getSubuerLastLogin())) {
            viewHolder.tvLastLogin.setText("(".concat("last login : ").concat(getUserList.getSubuerLastLogin()).concat(")"));
        } else {
            viewHolder.tvLastLogin.setText("( Not login yet )");
        }

        if (!getUserList.getCatcherPhoto().isEmpty())
            Picasso.get().load(getUserList.getCatcherPhoto()).resize(100, 100).error(R.drawable.ic_profile).into(viewHolder.userImg);
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(subAccountList) ? 0 : subAccountList.size();
    }

    class SubAccountViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView userImg;
        private TextView tvcredit, tvpreRecorded, tvLiveInterview, tvName, tvLastLogin;

        private SubAccountViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.circleImageView4);
            tvcredit = itemView.findViewById(R.id.textView106);
            tvpreRecorded = itemView.findViewById(R.id.textView107);
            tvLiveInterview = itemView.findViewById(R.id.textView109);
            tvName = itemView.findViewById(R.id.textView110);
            tvLastLogin = itemView.findViewById(R.id.textView219);
        }
    }

}
