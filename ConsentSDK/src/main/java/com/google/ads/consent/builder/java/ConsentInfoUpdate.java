package com.google.ads.consent.builder.java;

import android.content.Context;
import android.util.Log;

import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.listener.ConsentInfoUpdateListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.android.gms.ads.AdRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class ConsentInfoUpdate {
    ConsentForm consentForm;
    Context context;

    public ConsentInfoUpdate(Context context) {
        this.context = context;
    }

    public void requestForm(){
        final ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        consentInformation.addTestDevice(AdRequest.DEVICE_ID_EMULATOR);
        consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        String[] publisherIds = {"pub-0000000000000000"};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if(consentInformation.isRequestLocationInEeaOrUnknown()) {
                    switch (consentStatus){
                        case PERSONALIZED:
                        case NON_PERSONALIZED:
                            Log.e("consentStatus", consentStatus.toString());
                            break;
                        case UNKNOWN:
                        default:
                            consentForm = makeConsentForm(context);
                            consentForm.load();
                            break;
                    }
                }
            }
            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {
                Log.e("Log", errorDescription);
            }
        });
    }

    private ConsentForm makeConsentForm(final Context context){
        URL privacyUrl = null;
        try {
            privacyUrl = new URL("https://you_url.com/privacy");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ConsentForm.Builder(context, privacyUrl).withListener(new ConsentFormListener() {
                    @Override
                    public void onConsentFormLoaded() {
                        consentForm.show();
                        Log.e("onConsentFormLoaded", "consentForm.show()");
                    }
                    @Override
                    public void onConsentFormOpened() {
                        Log.e("onConsentFormOpened", "onConsentFormOpened()");
                    }
                    @Override
                    public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                        Log.e("onConsentFormClosed", consentStatus.toString());
                    }
                    @Override
                    public void onConsentFormError(String errorDescription) {
                        Log.e("onConsentFormError", errorDescription);
                    }
                })
                .withPersonalizedAdsOption()
                .withNonPersonalizedAdsOption()
                .withAdFreeOption()
                .build();
    }

}
