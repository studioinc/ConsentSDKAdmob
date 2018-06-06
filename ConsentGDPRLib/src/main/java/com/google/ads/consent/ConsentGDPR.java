package com.google.ads.consent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.gms.ads.AdRequest;

import java.net.MalformedURLException;
import java.net.URL;

public class ConsentGDPR {
    private static final String TAG = ConsentGDPR.class.getName();
    private static ConsentGDPR consentGDPR = null;
    private static Context context = null;
    private static String testDeviceId = "";
    private static String privacyUrl = "";
    private static String publisherId = "";
    private static boolean isDebugGeographyEea = false;
    private static boolean isAdsPersonalize = false;
    private static boolean isAdsNonPersonalize = false;
    public static AdRequest adRequest;
    private boolean isAdRequestLoaded = false;
    private static ConsentGDPRListener consentGDPRListener;
    private static ConsentForm form;

    public interface ConsentGDPRListener{
        void isAdsPersonalize();
        void isAdsNonPersonalize();
        void isUserNotFromEeaPersonalize();
    }
    public ConsentGDPR(Context context) {
        this.context = context;
    }

    public static synchronized ConsentGDPR getInstance(Context context){
        if (consentGDPR == null){
            consentGDPR = new ConsentGDPR(context);
        }
        return consentGDPR;
    }

    public static void checkConsentGDPR() {
        ConsentInformation consentInformation = ConsentInformation.getInstance(context);
        consentInformation.addTestDevice(Builder.getTestDeviceId()); // B3EEABB8EE11C2BE770B684D95219ECB
        // Geography appears as in EEA for test devices.
        if (isDebugGeographyEea) {
            consentInformation.getInstance(context).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA);
        }
        // Geography appears as not in EEA for debug devices.
        //ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA);
        String[] publisherIds = {Builder.getPublisherId()}; //pub-6249501545322879
        consentInformation.requestConsentInfoUpdate(publisherIds, new ConsentInfoUpdateListener() {
            @Override
            public void onConsentInfoUpdated(ConsentStatus consentStatus) {
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        form = showForm();
                        form.load();
                        if (form != null){
                            showForm();
                        }
                    } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.PERSONALIZED);
                        consentGDPRListener.isAdsPersonalize();
                    } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        consentGDPRListener.isAdsNonPersonalize();
                    }
                } else {
                    consentGDPRListener.isAdsPersonalize();
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                }
            }

            @Override
            public void onFailedToUpdateConsentInfo(String errorDescription) {

            }
        });

    }

    public static ConsentForm showForm(){
        URL privacyUrl = null;
        try {
            privacyUrl = new URL(Builder.getPrivacyUrl()); //https://medmobilelab.github.io/pr0dev/privacy/ar/
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return new ConsentForm.Builder(context, privacyUrl).withListener(new ConsentFormListener() {
            @Override
            public void onConsentFormLoaded() {
                form.show();
                Log.e("onConsentFormLoaded", "consentForm.show()");
            }
            @Override
            public void onConsentFormOpened() {
                Log.e("onConsentFormOpened", "onConsentFormOpened()");
            }
            @Override
            public void onConsentFormClosed(ConsentStatus consentStatus, Boolean userPrefersAdFree) {
                Log.e("onConsentFormClosed", consentStatus.toString());
                if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown()) {
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                    if (consentStatus == ConsentStatus.UNKNOWN) {
                        form = showForm();
                        form.load();
                        if (form != null){
                            showForm();
                        }
                    } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.PERSONALIZED);
                        consentGDPRListener.isAdsPersonalize();
                    } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                        ConsentInformation.getInstance(context).setConsentStatus(ConsentStatus.NON_PERSONALIZED);
                        consentGDPRListener.isAdsNonPersonalize();
                    }
                } else {
                    consentGDPRListener.isUserNotFromEeaPersonalize();
                    Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown() + "");
                }
            }
            @Override
            public void onConsentFormError(String errorDescription) {
                Log.e("onConsentFormError", errorDescription);
            }
        }).withPersonalizedAdsOption()
          .withNonPersonalizedAdsOption()
          .build();
    }

    public void show(){
        checkConsentGDPR();
    }

    public boolean isAdRequestLoaded() {
        return this.isAdRequestLoaded;
    }

    public void adRequestLoaded(boolean isAdRequestLoaded) {
        this.isAdRequestLoaded = isAdRequestLoaded;
    }

    public boolean isAdsPersonalize() {
        return this.isAdsPersonalize;
    }

    public void adsPersonalize(boolean isAdsPersonalize) {
        this.isAdsPersonalize = isAdsPersonalize;
    }

    public boolean isAdsNonPersonalize() {
        return this.isAdsNonPersonalize;
    }

    public void adsNonPersonalize(boolean isAdsNonPersonalize) {
        this.isAdsNonPersonalize = isAdsNonPersonalize;
    }

    public AdRequest getAdRequest() {
        return this.adRequest;
    }

    public static void setAdRequest(AdRequest adRequest) {
        ConsentGDPR.adRequest = adRequest;
    }

    public static class GDPRListener implements ConsentGDPR.ConsentGDPRListener{
        @Override
        public void isAdsPersonalize() {
            adRequest = new AdRequest.Builder().build();
            setAdRequest(adRequest);
            addSharedPrefUserFromEEA(false);
        }

        @Override
        public void isAdsNonPersonalize() {
            Bundle extras = new Bundle();
            extras.putString("npa", "1");
            adRequest = new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).build();
            setAdRequest(adRequest);
            addSharedPrefUserFromEEA(true);
        }

        @Override
        public void isUserNotFromEeaPersonalize() {
            adRequest = new AdRequest.Builder().build();
            setAdRequest(adRequest);
            addSharedPrefUserFromEEA(false);
        }
    }

    public static void addSharedPrefUserFromEEA(boolean bool){
        SharedPreferences preferences = context.getSharedPreferences("gdpr", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("is_ads_personalize", bool);
        editor.apply();
        editor.commit();
    }

    public static boolean isUserFromEeaOrUnknown(){
        SharedPreferences preferences = context.getSharedPreferences("gdpr", Context.MODE_PRIVATE);
        return preferences.getBoolean("is_ads_personalize", false);
    }


    public static class Builder {
        private ConsentGDPR consentGDPR;

        public Builder with(Context context) {
            ConsentGDPR.context = context;
            consentGDPR = new ConsentGDPR(context);
            return this;
        }

        public static String getTestDeviceId() {
            return testDeviceId;
        }

        public Builder setTestDeviceId(String testDeviceId) {
            ConsentGDPR.testDeviceId = testDeviceId;
            return this;
        }

        public static String getPrivacyUrl() {
            return privacyUrl;
        }

        public Builder setPrivacyUrl(String privacyUrl) {
            ConsentGDPR.privacyUrl = privacyUrl;
            return this;
        }

        public static String getPublisherId() {
            return publisherId;
        }

        public Builder setPublisherId(String publisherId) {
            ConsentGDPR.publisherId = publisherId;
            return this;
        }

        public Builder debugGeographyEea(boolean isDebugGeographyEea) {
            ConsentGDPR.isDebugGeographyEea = isDebugGeographyEea;
            return this;
        }



        public Builder withListener(ConsentGDPRListener listener){
            consentGDPRListener = listener;
            return this;
        }

        public ConsentGDPR build(){
            return new ConsentGDPR(context);
        }
    }



}
