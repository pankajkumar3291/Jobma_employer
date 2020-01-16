package com.jobma.employer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.model.profile.EOProfessionalData;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

public class EmploymentAdapter extends RecyclerView.Adapter<EmploymentAdapter.EmploymentViewHolder> {

    private ArrayList<EOProfessionalData> employmentList;
    private Context context;

    public EmploymentAdapter(Context context, ArrayList<EOProfessionalData> employmentList) {
        this.context = context;
        this.employmentList = employmentList;
    }

    @NonNull
    @Override
    public EmploymentAdapter.EmploymentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_details, viewGroup, false);
        return new EmploymentAdapter.EmploymentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EmploymentAdapter.EmploymentViewHolder holder, int position) {
        final EOProfessionalData professionalData = employmentList.get(position);
        if (!ObjectUtil.isEmpty(professionalData.getTitle())) {
            holder.empLayout.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(professionalData.getTitle());
            holder.tvCompanyName.setText(professionalData.getCompany());
        }
        if (ObjectUtil.isEmpty(professionalData.getStartDate()) || ObjectUtil.isEmpty(professionalData.getEndDate())) {
            holder.tvTimePeriod.setText("N/A");
        } else {
            holder.tvTimePeriod.setText(professionalData.getStartDate().concat(" To ").concat(professionalData.getEndDate()));
        }
        holder.tvDescription.setText(ObjectUtil.isEmpty(professionalData.getDetail()) ? "N/A" : professionalData.getDetail());
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.employmentList.size()) ? 0 : this.employmentList.size();
    }

    class EmploymentViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout empLayout;
        private TextView tvTitle, tvCompanyName, tvTimePeriod, tvDescription;

        private EmploymentViewHolder(@NonNull View itemView) {
            super(itemView);
            empLayout = itemView.findViewById(R.id.constraintLayout58);
            tvTitle = itemView.findViewById(R.id.textView61);
            tvCompanyName = itemView.findViewById(R.id.textView29);
            tvTimePeriod = itemView.findViewById(R.id.textView13);
            tvDescription = itemView.findViewById(R.id.textView11);
        }
    }
}
