package com.example.branch;

import android.app.Application;

import io.branch.referral.Branch;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Branch.enableLogging();
        Branch.getAutoInstance(this);
    }
}
