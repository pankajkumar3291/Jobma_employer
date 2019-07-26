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
import com.jobma.employer.activities.ActivityInterviewKit;
import com.jobma.employer.model.interviewKit.EOKitList;
import com.jobma.employer.util.ObjectUtil;

import java.util.List;

public class InterviewKitAdapter extends RecyclerView.Adapter<InterviewKitAdapter.InterViewKitViewHolder> {

    private Context context;
    private List<EOKitList> interviewList;

    public InterviewKitAdapter(Context context, List<EOKitList> interviewList) {
        this.context = context;
        this.interviewList = interviewList;
    }

    @NonNull
    @Override
    public InterviewKitAdapter.InterViewKitViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_inerview_kit, viewGroup, false);
        return new InterViewKitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterViewKitViewHolder viewHolder, int position) {
        EOKitList kitListData = this.interviewList.get(position);

        if (!ObjectUtil.isEmpty(kitListData.getTitle()))
            viewHolder.tvTitle.setText(kitListData.getTitle());

        viewHolder.tvPreRecorded.setText(String.valueOf(kitListData.getVideo()).concat(" - "));
        viewHolder.tvMCQ.setText(String.valueOf(kitListData.getMcq()).concat(" - "));
        viewHolder.tvEssay.setText(String.valueOf(kitListData.getEssay()).concat(" - "));

        viewHolder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, ActivityInterviewKit.class).putExtra("kitId",kitListData.getId()));
            }
        });

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.interviewList) ? 0 : this.interviewList.size();
    }

    class InterViewKitViewHolder extends RecyclerView.ViewHolder {

        private TextView tvTitle, tvPreRecorded, tvMCQ, tvEssay;
        private ConstraintLayout layout;

        private InterViewKitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.textView230);
            layout = itemView.findViewById(R.id.constraintLayout42);
            tvPreRecorded = itemView.findViewById(R.id.textView231);
            tvMCQ = itemView.findViewById(R.id.textView235);
            tvEssay = itemView.findViewById(R.id.textView237);
        }
    }


}
