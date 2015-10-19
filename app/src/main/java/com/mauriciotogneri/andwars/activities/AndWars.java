package com.mauriciotogneri.andwars.activities;

import android.app.Application;
import android.os.StrictMode;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.Tracker;
import com.mauriciotogneri.andwars.R;

import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender.Type;

@ReportsCrashes(formUri = "http://zeronest.com/acra/report.php", reportType = Type.FORM)
public class AndWars extends Application
{
    private Tracker tracker;

    @Override
    public void onCreate()
    {
        super.onCreate();

        ACRA.init(this);
        ACRA.getErrorReporter().putCustomData("PACKAGE_NAME", getPackageName());

        StrictMode.ThreadPolicy.Builder threadBuilder = new StrictMode.ThreadPolicy.Builder();
        threadBuilder.detectAll();
        threadBuilder.penaltyLog();
        StrictMode.setThreadPolicy(threadBuilder.build());

        StrictMode.VmPolicy.Builder vmBuilder = new StrictMode.VmPolicy.Builder();
        vmBuilder.detectAll();
        vmBuilder.penaltyLog();
        StrictMode.setVmPolicy(vmBuilder.build());

        GoogleAnalytics analytics = GoogleAnalytics.getInstance(this);
        this.tracker = analytics.newTracker(R.xml.app_tracker);
    }

    public Tracker getTracker()
    {
        return this.tracker;
    }
}