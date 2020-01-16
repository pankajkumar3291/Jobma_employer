package com.jobma.employer.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.jobma.employer.R;
import com.jobma.employer.model.applicants.Catcher;

import java.util.List;

public class EvaluationFilterAccountAdapter extends RecyclerView.Adapter<EvaluationFilterAccountAdapter.EvaluationFilterAccountViewHolder> {

    private List<Catcher> accountList;
    private Context context;

    public EvaluationFilterAccountAdapter(List<Catcher> accountList, Context context) {
        this.accountList = accountList;
        this.context = context;
    }

    @NonNull
    @Override
    public EvaluationFilterAccountViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_account, viewGroup, false);
        return new EvaluationFilterAccountViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final EvaluationFilterAccountViewHolder evaluationFilterAccountViewHolder, int i) {
        final Catcher catcher = accountList.get(i);

        if (catcher.isIschecked()) {
            evaluationFilterAccountViewHolder.checkBox.setChecked(true);
        } else {
            evaluationFilterAccountViewHolder.checkBox.setChecked(false);
        }

        evaluationFilterAccountViewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (evaluationFilterAccountViewHolder.checkBox.isChecked()) {
                    catcher.setIschecked(true);
                } else {
                    catcher.setIschecked(false);
                }
            }
        });

        evaluationFilterAccountViewHolder.tvName.setText(catcher.getFname() + " " + catcher.getLname());
    }

    @Override
    public int getItemCount() {
        return accountList.size();
    }

    class EvaluationFilterAccountViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private CheckBox checkBox;

        private EvaluationFilterAccountViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textView203);
            checkBox = itemView.findViewById(R.id.checkBox2);
        }
    }
}