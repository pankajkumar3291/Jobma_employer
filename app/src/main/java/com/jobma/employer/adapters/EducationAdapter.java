package com.jobma.employer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.jobma.employer.R;
import com.jobma.employer.model.profile.EOEducationData;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

public class EducationAdapter extends RecyclerView.Adapter<EducationAdapter.EducationViewHolder> {

    private Context context;
    private ArrayList<EOEducationData> educationList;

    public EducationAdapter(Context context, ArrayList<EOEducationData> educationList) {
        this.educationList = educationList;
        this.context = context;
    }

    @NonNull
    @Override
    public EducationAdapter.EducationViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_details, viewGroup, false);
        return new EducationAdapter.EducationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EducationViewHolder holder, int position) {
        EOEducationData eoEducationData = this.educationList.get(position);

        if (!ObjectUtil.isEmpty(eoEducationData.getDegree())) {
            holder.educationLayout.setVisibility(View.VISIBLE);
            holder.tvDegree.setText(eoEducationData.getDegree());
            holder.tvInstitution.setText(eoEducationData.getEduInstitution());
        }
        if (ObjectUtil.isEmpty(eoEducationData.getStartDate()) || ObjectUtil.isEmpty(eoEducationData.getEndDate())) {
            holder.tvTimePeriod.setText("N/A");
        } else {
            holder.tvTimePeriod.setText(eoEducationData.getStartDate().concat(" To ").concat(eoEducationData.getEndDate()));
        }
        holder.tvDescription.setText(ObjectUtil.isEmpty(eoEducationData.getEduSummary()) ? "N/A" : eoEducationData.getEduSummary());
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(educationList.size()) ? 0 : educationList.size();
    }

    class EducationViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout educationLayout;
        private TextView tvDegree, tvInstitution, tvTimePeriod, tvDescription;

        private EducationViewHolder(@NonNull View itemView) {
            super(itemView);
            educationLayout = itemView.findViewById(R.id.constraintLayout59);
            tvDegree = itemView.findViewById(R.id.textView39);
            tvInstitution = itemView.findViewById(R.id.textView41);
            tvTimePeriod = itemView.findViewById(R.id.textView13);
            tvDescription = itemView.findViewById(R.id.textView11);
        }
    }
}
