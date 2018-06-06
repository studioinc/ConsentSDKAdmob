package com.consentsdkgdpr.consentsdkforgdpr

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.ads.consent.ConsentGDPR
import com.google.android.gms.ads.AdRequest

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("DEVICE_ID_EMULATOR", AdRequest.DEVICE_ID_EMULATOR)
        ConsentGDPR.Builder()
                .with(this)
                .setPrivacyUrl("https://your_privacy_url.com")
                .setPublisherId("pub-0000000000000000")
                .debugGeographyEea(true)
                .setTestDeviceId("TEST_DEVICE_ID") //B3EEABB8EE11C2BE770B684D95219ECB
                .withListener(ConsentGDPR.GDPRListener())
                .build().show()
    }
}
