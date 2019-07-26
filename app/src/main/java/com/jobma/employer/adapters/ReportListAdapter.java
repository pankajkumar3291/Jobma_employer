package com.jobma.employer.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityInteraction;
import com.jobma.employer.model.reportIssue.EOReportList;
import com.jobma.employer.util.ObjectUtil;

import java.util.List;

public class ReportListAdapter extends RecyclerView.Adapter<ReportListAdapter.ReportListViewHolder> {

    private List<EOReportList> reportList;
    private Context context;

    public ReportListAdapter(Context context, List<EOReportList> reportList) {
        this.context = context;
        this.reportList = reportList;
        setHasStableIds(true);
    }

    @NonNull
    @Override
    public ReportListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_reported_list, viewGroup, false);
        return new ReportListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReportListViewHolder reportListViewHolder, int position) {
        EOReportList eoReportList = reportList.get(position);

        reportListViewHolder.tvname.setText(eoReportList.getSubject());
        reportListViewHolder.tvTicket.setText(eoReportList.getJobmaTicketId());
        reportListViewHolder.tvReportDate.setText(eoReportList.getCreatedAt());

        if (eoReportList.getIssueStatus().equalsIgnoreCase("Pending")) {
            reportListViewHolder.tvStatus.setText(eoReportList.getIssueStatus());
            reportListViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.rejected_color));
            reportListViewHolder.view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circular_view));
        } else {
            reportListViewHolder.tvStatus.setText(eoReportList.getIssueStatus());
            reportListViewHolder.tvStatus.setTextColor(context.getResources().getColor(R.color.selected_color));
            reportListViewHolder.view.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.circular_view_green));
        }

        reportListViewHolder.mainlayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityInteraction.class).putExtra("contactId", eoReportList.getJobmaContactId()));
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.reportList.size()) ? 0 : this.reportList.size();
    }

    class ReportListViewHolder extends RecyclerView.ViewHolder {
        private TextView tvname, tvTicket, tvReportDate, tvStatus;
        private View view;
        private ConstraintLayout mainlayout;

        private ReportListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvname = itemView.findViewById(R.id.textView61);
            tvTicket = itemView.findViewById(R.id.textView64);
            tvReportDate = itemView.findViewById(R.id.textView66);
            tvStatus = itemView.findViewById(R.id.textView68);
            view = itemView.findViewById(R.id.view12);
            mainlayout = itemView.findViewById(R.id.mainlayout);
        }
    }

}
