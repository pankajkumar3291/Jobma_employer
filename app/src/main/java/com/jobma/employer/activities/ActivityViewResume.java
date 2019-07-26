package com.jobma.employer.activities;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.util.ObjectUtil;

public class ActivityViewResume extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;
    private ImageView ivBackBtn;
    private WebView webView;
    private String viewResume;
    private GlobalProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_resume);

        if (!ObjectUtil.isEmpty(this.getIntent().getStringExtra("viewResume"))) {
            this.viewResume = this.getIntent().getStringExtra("viewResume");
        }

        this.initView();
        this.dataToView();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());
        this.progressDialog = new GlobalProgressDialog(this);

        this.ivBackBtn = this.findViewById(R.id.ivBackBtn);
        this.webView = this.findViewById(R.id.webView);
        this.ivBackBtn.setOnClickListener(this);
    }

    private void dataToView() {
        progressDialog.showProgressBar();
        webView.setWebViewClient(new WebViewClient());
        webView.getSettings().setSupportZoom(true);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=" + viewResume);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                progressDialog.hideProgressBar();
            }
        }, 3000);
    }

    @Override
    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.ivBackBtn) {
            this.finish();
        }
    }

}
