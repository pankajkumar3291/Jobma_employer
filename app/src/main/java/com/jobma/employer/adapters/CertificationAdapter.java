package com.jobma.employer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.model.profile.EOCertificationData;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;

public class CertificationAdapter extends RecyclerView.Adapter<CertificationAdapter.CertificateViewHolder> {

    private Context context;
    private ArrayList<EOCertificationData> certificateList;

    public CertificationAdapter(Context context, ArrayList<EOCertificationData> certificateList) {
        this.context = context;
        this.certificateList = certificateList;
    }

    @NonNull
    @Override
    public CertificationAdapter.CertificateViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_profile_details, viewGroup, false);
        return new CertificationAdapter.CertificateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CertificationAdapter.CertificateViewHolder holder, int position) {
        EOCertificationData certificationData = this.certificateList.get(position);

        if (!ObjectUtil.isEmpty(certificationData.getCertification())) {
            holder.certificationLayout.setVisibility(View.VISIBLE);
            holder.tvCertificateName.setText(certificationData.getCertification());
        }
        if (ObjectUtil.isEmpty(certificationData.getStartDate()) || ObjectUtil.isEmpty(certificationData.getEndDate())) {
            holder.tvTimePeriod.setText("N/A");
        } else {
            holder.tvTimePeriod.setText(certificationData.getStartDate().concat(" To ").concat(certificationData.getEndDate()));
        }
        holder.tvDescription.setText(ObjectUtil.isEmpty(certificationData.getBenefits()) ? "N/A" : certificationData.getBenefits());

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.certificateList.size()) ? 0 : this.certificateList.size();
    }

    class CertificateViewHolder extends RecyclerView.ViewHolder {
        private ConstraintLayout certificationLayout;
        private TextView tvCertificateName, tvTimePeriod, tvDescription;

        private CertificateViewHolder(@NonNull View itemView) {
            super(itemView);
            certificationLayout = itemView.findViewById(R.id.constraintLayout60);
            tvCertificateName = itemView.findViewById(R.id.textView43);
            tvTimePeriod = itemView.findViewById(R.id.textView13);
            tvDescription = itemView.findViewById(R.id.textView11);
        }
    }
}
