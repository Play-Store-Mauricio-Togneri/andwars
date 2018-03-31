package com.mauriciotogneri.andwars.activities;

import android.app.Application;

import com.crashlytics.android.Crashlytics;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mauriciotogneri.andwars.R;

import io.fabric.sdk.android.Fabric;

public class AndWars extends Application
{
    private Tracker tracker;

    @Override
    public void onCreate()
    {
        super.onCreate();

        Fabric.with(this, new Crashlytics());

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        this.tracker = analytics.newTracker(R.xml.app_tracker);
    }

    public Tracker getTracker()
    {
        return this.tracker;
    }
}