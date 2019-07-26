package com.jobma.employer.exoPlayer;

import android.content.Context;
import android.content.Intent;

public abstract class Sample {

    private final String name;
    private final DrmInfo drmInfo;

    public Sample(String name, DrmInfo drmInfo) {
        this.name = name;
        this.drmInfo = drmInfo;
    }

    public Intent buildIntent(Context context, boolean preferExtensionDecoders, String abrAlgorithm) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(PlayerActivity.PREFER_EXTENSION_DECODERS_EXTRA, preferExtensionDecoders);
        intent.putExtra(PlayerActivity.ABR_ALGORITHM_EXTRA, abrAlgorithm);
        if (drmInfo != null) {
            drmInfo.updateIntent(intent);
        }
        return intent;
    }

}