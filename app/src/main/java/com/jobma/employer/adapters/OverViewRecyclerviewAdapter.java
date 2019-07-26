package com.jobma.employer.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jobma.employer.R;
import com.jobma.employer.model.dashboard.EOOverviewData;
import com.jobma.employer.util.ObjectUtil;
import com.jobma.employer.util.UIUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class OverViewRecyclerviewAdapter extends RecyclerView.Adapter<OverViewRecyclerviewAdapter.OverviewViewHolder> {

    private ArrayList<EOOverviewData> overviewDataList;
    private Context context;
    private boolean isvisible;

    public OverViewRecyclerviewAdapter(Context context, ArrayList<EOOverviewData> overviewDataList) {
        this.context = context;
        this.overviewDataList = overviewDataList;
    }

    @NonNull
    @Override
    public OverviewViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_overview, viewGroup, false);
        return new OverviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final OverviewViewHolder holder, final int position) {
        float dialogRadius = UIUtil.getDimension(R.dimen._3sdp);
        UIUtil.setBackgroundRound(holder.dialogCorner, R.color.dialog_rounded_color, new float[]{dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius, dialogRadius});

        EOOverviewData overviewData = this.overviewDataList.get(position);

        holder.tvname.setText(overviewData.getPitcherName());
        if (!ObjectUtil.isEmpty(overviewData.getJobmaInvitationDate())) {
            String[] splitStr = overviewData.getJobmaInvitationDate().split("\\s+");
            holder.tvtime.setText(splitStr[1].concat(" ").concat(splitStr[2]));
        }

        holder.tvinterview.setText(overviewData.getInterviewMode());
        holder.tvlocation.setText(overviewData.getInvitationTimezone());
        holder.tvAppliedFor.setText(overviewData.getJobName());
        loadImages(overviewData.getPitcherPhoto(), holder.profileImage);
        holder.tvInterviewToken.setText(ObjectUtil.isEmpty(overviewData.getJobmaInterviewToken()) ? "Interview token" : overviewData.getJobmaInterviewToken());

        holder.dotImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO uncomment the code for live interview
                if (isvisible) {
                    holder.dialoglayout.setVisibility(View.GONE);
                    isvisible = false;
                } else {
                    holder.dialoglayout.setVisibility(View.VISIBLE);
                    isvisible = true;
                }
            }
        });

        holder.tvTokenCopy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                copyTokenToClipboard(overviewData.getJobmaInterviewToken());
            }
        });

        holder.tvGoToLiveInterview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Go to live interview page", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void copyTokenToClipboard(String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Copied text", Toast.LENGTH_SHORT).show();
    }

    private void loadImages(String imagePath, ImageView imageView) {
        Picasso.get()
                .load(imagePath)
                .fit()
                .error(R.drawable.ic_profile)
                .into(imageView);
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.overviewDataList) ? 0 : overviewDataList.size();
    }

    public class OverviewViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        ImageView dotImage;
        ConstraintLayout dialoglayout, dialogCorner;
        TextView tvname, tvtime, tvinterview, tvlocation, tvAppliedFor, tvInterviewToken, tvTokenCopy, tvGoToLiveInterview;

        private OverviewViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage = itemView.findViewById(R.id.circleImageView3);
            tvname = itemView.findViewById(R.id.textView97);
            tvtime = itemView.findViewById(R.id.textView98);
            tvinterview = itemView.findViewById(R.id.textView99);
            tvlocation = itemView.findViewById(R.id.textView100);
            tvAppliedFor = itemView.findViewById(R.id.textView102);
            dotImage = itemView.findViewById(R.id.imageView44);
            dialoglayout = itemView.findViewById(R.id.constraintLayout13);
            dialogCorner = itemView.findViewById(R.id.constraintLayout14);
            tvInterviewToken = itemView.findViewById(R.id.textView113);
            tvTokenCopy = itemView.findViewById(R.id.textView114);
            tvGoToLiveInterview = itemView.findViewById(R.id.textView112);
        }
    }
}
