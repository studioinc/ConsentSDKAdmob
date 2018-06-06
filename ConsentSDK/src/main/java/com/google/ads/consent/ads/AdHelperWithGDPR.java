package com.google.ads.consent.ads;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.ads.consent.builder.java.ConsentGDPR;
import com.google.ads.consent.listener.AdCallListener;
import com.google.ads.consent.listener.AdRequestListener;
import com.google.ads.consent.listener.ConsentResultCallback;
import com.google.ads.consent.util.CheckNetwork;
import com.google.ads.consent.util.LogDebug;
import com.google.ads.mediation.admob.AdMobAdapter;
import com.google.android.ads.consent.R;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;



public class AdHelperWithGDPR extends AppCompatActivity{
    private String TAG = AdHelperWithGDPR.class.getName();
    private Context context;
    private AdRequest adRequest = null;
    private InterstitialAd interstitialAd;
    private AdView adView;
    private RewardedVideoAd rewardedVideoAd;
    private AdCallListener admobCallListener;
    private ConsentResultCallback consentCallBack;
    private static AdHelperWithGDPR adhelper = null;
    private String privacyUrl;
    private String publisherId;

    private int adAppUnitId;
    private int adBannerId;
    private int adIntetstitiaId;
    private int adRewardedVideoId;

    private boolean testDeviceDebugGeography;


    public static boolean isAdsPersonalize;
    public static boolean isAdsNonPersonalize;
    public static boolean isUserNotFromEeaPersonalize;

    public final int AdsUserNotFromEeaPersonalize = 1;
    public final int AdsNonPersonalize = 2;
    public final int AdsPersonalize = 3;

    public void setConsentResultCallBack(ConsentResultCallback callBack){
        this.consentCallBack = callBack;
    }

    public void addAdmobCallListener(AdCallListener listener){
        this.admobCallListener = listener;
    }

    public static synchronized AdHelperWithGDPR getInstance(){
        if (adhelper == null){
            return adhelper = new AdHelperWithGDPR();
        }
        return adhelper;
    }

    public class ReQuestCallBack implements AdRequestListener {
        @Override
        public void isUserNotFromEeaPersonalize() {
            isUserNotFromEeaPersonalize = true;
            consentCallBack.onResult(AdsUserNotFromEeaPersonalize);
        }

        @Override
        public void isAdsNonPersonalize() {
            isAdsNonPersonalize = true;
            consentCallBack.onResult(AdsNonPersonalize);
        }

        @Override
        public void isAdsPersonalize() {
            isAdsPersonalize = true;
            consentCallBack.onResult(AdsPersonalize);
        }
    }

    public boolean checkLinkExt(String url){
        if (url.startsWith("http://")){
            return true;
        } else if (url.startsWith("https://")){
            return true;
        } else {
            return false;
        }
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
    }
    public void applyBuilder(){

        if (CheckNetwork.isOnline(getApplicationContext())){
            // Builder Class method
            if (stripNonDigits(getPublisherId()).length() == 16 && checkLinkExt(getPrivacyUrl())){
                new ConsentGDPR.Builder()
                        .context(this)
                        .privacyUrl(getPrivacyUrl())
                        .publisherId(getPublisherId())
                        .debugGeography(isTestDeviceDebugGeography())
                        .testDeviceId(AdRequest.DEVICE_ID_EMULATOR)
                        .listener(new ReQuestCallBack())
                        .build().show();
            } else {
                LogDebug.d("PublisherId", "please entre you publisher id 'pub-' a unique number 16");
                String tastMsg = null;
                String publisherIdMsg = "- 1: Please entre you publisher id 'pub-' a unique number 16";
                String ptivacyMsg = "- 2: Privacy Policy Link is not found request http or https";
                boolean[] booleans = new boolean[]{stripNonDigits(getPublisherId()).length() == 16, checkLinkExt(getPrivacyUrl())};

                if (!booleans[0] && !booleans[1]){
                    tastMsg = publisherIdMsg + "\n\n"+ ptivacyMsg;
                } else if (!booleans[0]){
                    tastMsg = publisherIdMsg;
                }else if (!booleans[1]){
                    tastMsg = ptivacyMsg;
                }
                Toast.makeText(context, tastMsg+"", Toast.LENGTH_LONG).show();
            }

            /**
            // GetInstance method
            ConsentGDPR instance = ConsentGDPR.getInstance(this);
            instance.setPrivacyUrl(getPrivacyUrl());
            instance.setPublisherId(getPublisherId());
            instance.setTestDeviceId(AdRequest.DEVICE_ID_EMULATOR);
            instance.setDebugGeographyEea(true);
            instance.setAdRequestListener(new ReQuestCallBack());
            instance.show();
            */


            /**
            // constructor parameters method
            ConsentGDPR consent = new ConsentGDPR(this, getPrivacyUrl(), getPublisherId(), true, new ReQuestCallBack());
            consent.show();
            */

            /**
            // constructor direct parameters method
            new ConsentGDPR(this, getPrivacyUrl(), getPublisherId(), true, new ReQuestCallBack()).show();
            */

        }

        setConsentResultCallBack(new ConsentResultCallback(){
            @Override
            public void onResult(int i) {
                switch (i){
                    case AdsUserNotFromEeaPersonalize:
                        LogDebug.e(TAG, "isUserNotFromEeaPersonalize");
                        adRequest = getAdRequestAdsPersonalize();
                        checkResourceIdsSetListener();
                        break;
                    case AdsNonPersonalize:
                        LogDebug.e(TAG, "isAdsNonPersonalize");
                        adRequest = getAdRequestAdsNonPersonalize();
                        checkResourceIdsSetListener();
                        break;
                    case AdsPersonalize:
                        LogDebug.e(TAG, "isAdsPersonalize");
                        adRequest = getAdRequestAdsPersonalize();
                        checkResourceIdsSetListener();
                        break;
                    default:
                        AdsUserNotFromEeaPersonalize:
                        break;
                }
            }
        });
    }

    public void checkResourceIdsSetListener(){
        if (!(adAppUnitId <= 0 && adBannerId <= 0 && adIntetstitiaId <= 0 && adRewardedVideoId <= 0)){
            admobCallListener.onAdReady();
        } else {
            LogDebug.d("AdInitialize", "ads not initialized");
            Toast.makeText(context, "ads is not initialized", Toast.LENGTH_LONG).show();
            //showDialogTips();
        }
    }


    public void showDialogTips(){
        Dialog dialog = new Dialog(this);
        dialog.setCancelable(true);
        dialog.setTitle("Dialog");
        //dialog.show();

        final AlertDialog alertDialog;
        View viewLayout = LayoutInflater.from(this).inflate(R.layout.tips_dialog, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //builder.setTitle("");
        //builder.setMessage("");
        builder.setView(viewLayout);
        builder.setCancelable(false);
       /* builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        dialog.dismiss();
                    }
                });*/
        alertDialog = builder.show();
        Button howToUse = viewLayout.findViewById(R.id.how_to_use);
        Button cancel = viewLayout.findViewById(R.id.cancel);
        howToUse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getResources().getString(R.string.github_lib_url))).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
                alertDialog.dismiss();
            }
        });
    }

    public void initAdAppId(int id){
        this.adAppUnitId = id;
        if (!(adAppUnitId <= 0)){
            MobileAds.initialize(context, context.getResources().getString(adAppUnitId));
        } else {
            LogDebug.d("initAdAppId", "admob_app_unit_id not found");
            Toast.makeText(context, "admob_app_unit_id not found", Toast.LENGTH_LONG).show();
        }

    }


    public void initAdFullScreen(int id){
        this.adIntetstitiaId = id;
        if (CheckNetwork.isOnline(context)){
            this.interstitialAd = new InterstitialAd(context);
            if (!(adIntetstitiaId <= 0)) {
                this.interstitialAd.setAdUnitId(context.getResources().getString(adIntetstitiaId)); //context.getResources().getString(R.string.admob_interstitial_unit_id)
                LogDebug.d("initInterstitialAd", "onCreate Ad");
            } else {
                LogDebug.d("initAdFullScreen", "admob_interstitial_unit_id not found");
                Toast.makeText(context, "admob_interstitial_unit_id not found", Toast.LENGTH_LONG).show();
            }
        } else {
            LogDebug.d("initInterstitialAd", "onCreate Ad not found internet network");
        }
    }

    public void initAdRewardedVideo(int id){
        this.rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        this.adRewardedVideoId = id;
    }

    public void showRewardedVideo(){
        if (CheckNetwork.isOnline(context)){
            if (this.rewardedVideoAd != null){
                if (!(adRewardedVideoId <= 0)) {
                    this.rewardedVideoAd.loadAd(context.getResources().getString(this.adRewardedVideoId), adRequest);
                } else {
                    LogDebug.d("showRewardedVideo", "admob_rewarded_video_unit_id not found");
                    Toast.makeText(context, "admob_rewarded_video_unit_id not found", Toast.LENGTH_LONG).show();
                }
                this.rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
                    @Override
                    public void onRewardedVideoAdLoaded() {
                        admobCallListener.onRewardedVideoAdLoaded();
                        if (rewardedVideoAd.isLoaded()){
                            rewardedVideoAd.show();
                        }
                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        admobCallListener.onRewardedVideoAdOpened();
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        admobCallListener.onRewardedVideoAdStarted();
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        admobCallListener.onRewardedVideoAdClosed();
                    }

                    @Override
                    public void onRewarded(RewardItem rewardItem) {
                        admobCallListener.onRewardedAd(rewardItem);
                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {
                        admobCallListener.onRewardedVideoAdLeftApplication();
                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int i) {
                        admobCallListener.onRewardedVideoAdFailedToLoad(i);
                    }

                    @Override
                    public void onRewardedVideoCompleted() {
                        admobCallListener.onRewardedVideoAdCompleted();
                    }
                });
            }
        } else {
            LogDebug.d("showRewardedVideo", "showInterstitial Ad not found internet network");
        }
    }

    public void initAdBanner(int id){
        this.adBannerId = id;
        if (CheckNetwork.isOnline(context)){
            if (!(adBannerId <= 0)){
                adView = (AdView) findViewById(adBannerId);
            } else {
                LogDebug.d("initAdBanner", "admob_banner_unit_id not found");
                Toast.makeText(context, "admob_banner_unit_id not found", Toast.LENGTH_LONG).show();
            }

        } else {
            LogDebug.d("initBannerAd", "initBanner Ad not found internet network");
        }
    }

    public void showAdFullScreen(){
        if (CheckNetwork.isOnline(context)){
            if (interstitialAd != null){
                interstitialAd.loadAd(adRequest);
                interstitialAd.setAdListener(new AdListener() {
                    public void onAdClosed() {
                        LogDebug.d("interstitialAd", "Ad Closed");
                        admobCallListener.onInterstialAdClosed();
                    }

                    @Override
                    public void onAdLoaded() {
                        LogDebug.d("interstitialAd", "Ad Loaded");
                        if (interstitialAd.isLoaded()) {
                            interstitialAd.show();
                        }
                        admobCallListener.onInterstialAdLoaded();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        LogDebug.d("interstitialAd", "Ad Failed To Load: " + i);
                        admobCallListener.onInterstialAdFailedToLoad(i);
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        admobCallListener.onInterstialAdClicked();
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        admobCallListener.onInterstialAdOpened();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        admobCallListener.onInterstialAdImpression();
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                        admobCallListener.onInterstialAdLeftApplication();
                    }
                });
            }
        } else {
            LogDebug.d("showInterstitialAd", "showInterstitial Ad not found internet network");
        }
    }

    public void showAdBanner(){
        if (CheckNetwork.isOnline(context)){
            if (adView != null){
                adView.loadAd(adRequest);
                adView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        LogDebug.d("AdViewBanner", "Ad Loaded");
                        adView.setVisibility(View.VISIBLE);
                        admobCallListener.onBannerAdLoaded();
                    }

                    @Override
                    public void onAdOpened() {
                        super.onAdOpened();
                        admobCallListener.onBannerAdOpened();
                    }

                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                        admobCallListener.onBannerAdClicked();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        admobCallListener.onBannerAdClosed();
                    }

                    @Override
                    public void onAdLeftApplication() {
                        super.onAdLeftApplication();
                        admobCallListener.onBannerAdLeftApplication();
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                        admobCallListener.onBannerAdImpression();
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        LogDebug.d("AdViewBanner",  "Ad Failed To Load: " + i);
                        adView.setVisibility(View.GONE);
                        admobCallListener.onBannerAdFailedToLoad(i);
                    }
                });
            }
        } else {
            LogDebug.d("showBannerAd", "showBanner Ad not found internet network");
        }
    }

    public AdRequest getAdRequestAdsNonPersonalize(){
       Bundle extras = new Bundle();
       extras.putString("npa", "1");
       extras.putString("max_ad_content_rating", "G");
       return new AdRequest.Builder().addNetworkExtrasBundle(AdMobAdapter.class, extras).addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
    }

    public AdRequest getAdRequestAdsPersonalize(){
        return new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).build();
    }

    public String getPrivacyUrl() {
        return privacyUrl;
    }

    public void setPrivacyUrl(String privacyUrl) {
        this.privacyUrl = privacyUrl;
    }

    public void setPrivacyUrl(int privacyUrl) {
        this.privacyUrl = context.getResources().getString(privacyUrl);
    }

    public String getPublisherId() {
        return publisherId;
    }

    public void setPublisherId(String publisherId) {
        this.publisherId = publisherId;
    }

    public void setPublisherId(int publisherId) {
        this.publisherId = context.getResources().getString(publisherId);
    }

    public boolean isTestDeviceDebugGeography() {
        return testDeviceDebugGeography;
    }

    public void setTestDeviceDebugGeography(boolean testDeviceDebugGeography) {
        this.testDeviceDebugGeography = testDeviceDebugGeography;
    }

    public static String stripNonDigits(final CharSequence input){
        final StringBuilder sb = new StringBuilder(input.length());
        for(int i = 0; i < input.length(); i++){
            final char c = input.charAt(i);
            if(c > 47 && c < 58){
                sb.append(c);
            }
        }
        return sb.toString();
    }

    public void resume(){
        if (adView != null){
            adView.resume();
        }
    }

    public void pause(){
        if (adView != null){
            adView.pause();
        }
    }

    public void destroy(){
        if (adView != null){
            adView.destroy();
        }
    }
}
