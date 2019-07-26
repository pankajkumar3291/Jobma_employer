package com.jobma.employer.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.fivemin.chief.nonetworklibrary.networkBroadcast.NoNet;
import com.jobma.employer.R;
import com.jobma.employer.fragments.FragmentInvite;

public class ActivityInvite extends AppCompatActivity implements View.OnClickListener {

    private NoNet noNet;

    private ImageView btnback;
    private Button btnCreateJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite);

        this.initView();
        getSupportFragmentManager().beginTransaction().add(R.id.invite_container, new FragmentInvite()).commit();
    }

    private void initView() {
        this.noNet = new NoNet();
        this.noNet.initNoNet(this, getSupportFragmentManager());

        btnback = findViewById(R.id.btnback);
        btnback.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnback:
                this.finish();
                break;
        }
    }

    protected void onResume() {
        this.noNet.RegisterNoNet();
        super.onResume();
    }

    @Override
    protected void onPause() {
        this.noNet.unRegisterNoNet();
        super.onPause();
    }


}
