package com.jobma.employer.exoPlayer;

import android.content.Intent;

import com.google.android.exoplayer2.util.Assertions;

public final class DrmInfo {

    private final String drmScheme;
    private final String drmLicenseUrl;
    private final String[] drmKeyRequestProperties;
    private final boolean drmMultiSession;

    public DrmInfo(
            String drmScheme,
            String drmLicenseUrl,
            String[] drmKeyRequestProperties,
            boolean drmMultiSession) {
        this.drmScheme = drmScheme;
        this.drmLicenseUrl = drmLicenseUrl;
        this.drmKeyRequestProperties = drmKeyRequestProperties;
        this.drmMultiSession = drmMultiSession;
    }

    public void updateIntent(Intent intent) {
        Assertions.checkNotNull(intent);
        intent.putExtra(PlayerActivity.DRM_SCHEME_EXTRA, drmScheme);
        intent.putExtra(PlayerActivity.DRM_LICENSE_URL_EXTRA, drmLicenseUrl);
        intent.putExtra(PlayerActivity.DRM_KEY_REQUEST_PROPERTIES_EXTRA, drmKeyRequestProperties);
        intent.putExtra(PlayerActivity.DRM_MULTI_SESSION_EXTRA, drmMultiSession);
    }
}