package com.jobma.employer.exoPlayer;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public final class UriSample extends Sample {

        private final Uri uri;
        private final String extension;
        private final String adTagUri;
        private final String sphericalStereoMode;

        public UriSample(
                String name,
                DrmInfo drmInfo,
                Uri uri,
                String extension,
                String adTagUri,
                String sphericalStereoMode) {
            super(name, drmInfo);
            this.uri = uri;
            this.extension = extension;
            this.adTagUri = adTagUri;
            this.sphericalStereoMode = sphericalStereoMode;
        }

        @Override
        public Intent buildIntent(
                Context context, boolean preferExtensionDecoders, String abrAlgorithm) {
            return super.buildIntent(context, preferExtensionDecoders, abrAlgorithm)
                    .setData(uri)
                    .putExtra(PlayerActivity.EXTENSION_EXTRA, extension)
                    .putExtra(PlayerActivity.AD_TAG_URI_EXTRA, adTagUri)
                    .putExtra(PlayerActivity.SPHERICAL_STEREO_MODE_EXTRA, sphericalStereoMode)
                    .setAction(PlayerActivity.ACTION_VIEW);
        }

    }