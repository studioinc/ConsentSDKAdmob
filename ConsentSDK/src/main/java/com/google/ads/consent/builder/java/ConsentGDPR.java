package com.google.ads.consent.builder.java;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.google.ads.consent.ConsentForm;
import com.google.ads.consent.ConsentFormListener;
import com.google.ads.consent.ConsentInformation;
import com.google.ads.consent.ConsentStatus;
import com.google.ads.consent.DebugGeography;
import com.google.ads.consent.listener.AdRequestListener;
import com.google.ads.consent.listener.ConsentInfoUpdateListener;
import com.google.ads.consent.util.LogDebug;

import java.net.MalformedURLException;
import java.net.URL;

public class ConsentGDPR {
    private final String TAG = ConsentGDPR.class.getName();
    private static ConsentGDPR consentGDPR = null;
    private Context context = null;
    private String testDeviceId = "";
    private String privacyUrl = "https://policies.google.com/privacy";
    private String publisherId = "";
    private boolean isDebugGeographyEea = false;
    private AdRequestListener adRequestListener;
    private ConsentForm form;

    // Initialize constructor with context
    public ConsentGDPR(Context context) {
        this.context = context;
    }

    // Initialize constructor with methods
    public ConsentGDPR(Context context, String privacyUrl, String publisherId, boolean isDebugGeographyEea, AdRequestListener adRequestListener) {
        this.context = context;
        this.privacyUrl = privacyUrl;
        this.publisherId = publisherId;
        this.isDebugGeographyEea = isDebugGeographyEea;
        this.adRequestListener = adRequestListener;
    }
    /**
    * provided to the {@link ConsentGDPR} class for getInstance(context)
    */
    public static synchronized ConsentGDPR getInstance(Context context){
        if (consentGDPR == null){
            consentGDPR = new ConsentGDPR(context);
        }
        return consentGDPR;
    }

    // check in Eea or unknown
    public void checkInEeaOrUnknown() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        consentInformation.addTestDevice(getTestDeviceId());
        // Geography appears as in EEA for test devices.
        if (isDebugGeographyEea) {
            consentInformation.getInstance(context).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        }
        // Geography appears as not in EEA for debug devices.
        //ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA);
        String[] publisherIds = {getPublisherId()};
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        form = LoadForm();
                        form.load();
                        if (form != null){
                            LoadForm();
                        }
                    } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.PERSONALIZED);
                        adRequestListener.isAdsPersonalize();
                    } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        adRequestListener.isAdsNonPersonalize();
                    }
                } else {
                    adRequestListener.isUserNotFromEeaPersonalize();
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {

            }
        });

    }

    // Methoud load form
    public ConsentForm LoadForm(){
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(getPrivacyUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ConsentForm.Builder(context, privacyUrl).withListener(new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded() {
                form.show();
                LogDebug.e("onConsentFormLoaded", "consentForm.show()");
            }
            @Override
            public void onConsentFormOpened() {
                LogDebug.e("onConsentFormOpened", "onConsentFormOpened()");
            }
            @Override
            public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                LogDebug.e("onConsentFormClosed", consentStatus.toString());
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                    LogDebug.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        form = LoadForm();
                        form.load();
                        if (form != null){
                            LoadForm();
                        }
                    } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.PERSONALIZED);
                        adRequestListener.isAdsPersonalize();
                    } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        adRequestListener.isAdsNonPersonalize();
                    }
                } else {
                    adRequestListener.isUserNotFromEeaPersonalize();
                    LogDebug.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                }
            }
            @Override
            public void onConsentFormError(String errorDescription) {
                LogDebug.e("onConsentFormError", errorDescription);
            }
        }).withPersonalizedAdsOption()
          .withNonPersonalizedAdsOption()
          .build();
    }

    // Methoud show form
    public void show(){
        checkInEeaOrUnknown();
    }

    // Get Test Device Id
    public String getTestDeviceId() {
        return testDeviceId;
    }

    // Set Test Device Id
    public void setTestDeviceId(String testDeviceId) {
        this.testDeviceId = testDeviceId;
    }

    // Get Privacy Url
    public String getPrivacyUrl() {
        return privacyUrl;
    }

    // Set Privacy Url
    public void setPrivacyUrl(String privacyUrl) {
        this.privacyUrl = privacyUrl;
    }

    // Get Publisher Id
    public String getPublisherId() {
        return publisherId;
    }

    // Set Publisher Id
    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public boolean isDebugGeographyEea() {
        return isDebugGeographyEea;
    }

    // is Debug Geography Eea
    public void setDebugGeographyEea(boolean debugGeographyEea) {
        isDebugGeographyEea = debugGeographyEea;
    }

    // add AdRequest callback
    public void setAdRequestListener(AdRequestListener adRequestListener) {
        this.adRequestListener = adRequestListener;
    }


    // Builder Class
    public static class Builder {
        private Context context;
        private String testDeviceId;
        private String privacyUrl;
        private String publisherId;
        private boolean isDebugGeography;
        private AdRequestListener adRequestListener;

        public Builder context(Context context) {
            this.context = context;
            return this;
        }

        // Test Device Id
        public Builder testDeviceId(String testDeviceId) {
            this.testDeviceId = testDeviceId;
            return this;
        }

        // Test Device Id Resource String
        public Builder testDeviceId(int testDeviceId) {
            this.testDeviceId = context.getResources().getString(testDeviceId);
            return this;
        }

        // Privacy Url String
        public Builder privacyUrl(String privacyUrl) {
            this.privacyUrl = privacyUrl;
            return this;
        }

        // Publisher Id String
        public Builder publisherId(String publisherId) {
            this.publisherId = publisherId;
            return this;
        }

        // Publisher Id Resource String
        public Builder publisherId(int publisherId) {
            this.publisherId = context.getResources().getString(publisherId);
            return this;
        }

        // Debug Geography
        public Builder debugGeography(boolean isDebugGeographyEea) {
            this.isDebugGeography = isDebugGeographyEea;
            return this;
        }

        // add AdRequest callback
        public Builder listener(AdRequestListener listener){
            this.adRequestListener = listener;
            return this;
        }

        // set Build Class
        public ConsentGDPR build(){
            ConsentGDPR consentGDPR = new ConsentGDPR(context);
            consentGDPR.setPrivacyUrl(privacyUrl);
            consentGDPR.setPublisherId(publisherId);
            consentGDPR.setDebugGeographyEea(isDebugGeography);
            consentGDPR.setAdRequestListener(adRequestListener);
            return consentGDPR;
        }
    }

    // add SharedPreferences if User From EEA or Unknown
    public void addSharedPrefUserFromEeaOrUnknown(boolean bool){
        SharedPreferences preferences = context.getSharedPreferences("gdpr", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_ads_personalize", bool);
        editor.apply();
        editor.commit();
    }

    // get SharedPreferences if User From EEA or Unknown
    public boolean isUserFromEeaOrUnknown(){
        SharedPreferences preferences = context.getSharedPreferences("gdpr", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_ads_personalize", false);
    }
}
