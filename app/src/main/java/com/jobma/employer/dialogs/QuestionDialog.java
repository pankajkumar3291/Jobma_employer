package com.jobma.employer.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;

import com.jobma.employer.R;


public class QuestionDialog extends Dialog {

    private Context context;
    private int layout;
    private String title;

    public QuestionDialog(@NonNull Context context, String title, int layout) {
        super(context, R.style.Theme_AppCompat_Light_Dialog_Alert);
        this.context = context;
        this.layout = layout;
        this.title = title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout);
    }

    @Override
    public void show() {
        super.show();
    }
}
