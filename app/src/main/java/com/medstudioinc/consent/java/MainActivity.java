package com.medstudioinc.consent.java;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.consentsdkgdpr.consentsdkforgdpr.R;
import com.google.ads.consent.ads.AdHelperWithGDPR;
import com.google.ads.consent.listener.AdCallListener;
import com.google.android.gms.ads.reward.RewardItem;

public class MainActivity extends AdHelperWithGDPR {

    @Override
    protected void onStart() {
        super.onStart();
        setPrivacyUrl("your_url_privacy_here");
        setPublisherId("your_publisher_id_here"); //pub-6249501545322879
        setTestDeviceDebugGeography(true);
        applyBuilder();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initAdAppId(R.string.admob_app_unit_id);

        initAdFullScreen(R.string.admob_interstitial_unit_id);

        initAdRewardedVideo(R.string.admob_rewarded_video_unit_id);

        initAdBanner(R.id.adView);


        addAdmobCallListener(new AdCallListener() {
            @Override
            public void onAdReady() {
                super.onAdReady();
                showAdFullScreen();
                showRewardedVideo();
                showAdBanner();
            }

            @Override
            public void onInterstialAdClosed() {
                super.onInterstialAdClosed();
            }

            @Override
            public void onInterstialAdOpened() {
                super.onInterstialAdOpened();
            }

            @Override
            public void onInterstialAdClicked() {
                super.onInterstialAdClicked();
            }

            @Override
            public void onInterstialAdLoaded() {
                super.onInterstialAdLoaded();
            }

            @Override
            public void onInterstialAdFailedToLoad(int i) {
                super.onInterstialAdFailedToLoad(i);
            }

            @Override
            public void onInterstialAdImpression() {
                super.onInterstialAdImpression();
            }

            @Override
            public void onInterstialAdLeftApplication() {
                super.onInterstialAdLeftApplication();
            }

            @Override
            public void onBannerAdClosed() {
                super.onBannerAdClosed();
            }

            @Override
            public void onBannerAdOpened() {
                super.onBannerAdOpened();
            }

            @Override
            public void onBannerAdClicked() {
                super.onBannerAdClicked();
            }

            @Override
            public void onBannerAdLoaded() {
                super.onBannerAdLoaded();
            }

            @Override
            public void onBannerAdFailedToLoad(int i) {
                super.onBannerAdFailedToLoad(i);
            }

            @Override
            public void onBannerAdImpression() {
                super.onBannerAdImpression();
            }

            @Override
            public void onBannerAdLeftApplication() {
                super.onBannerAdLeftApplication();
            }

            @Override
            public void onRewardedVideoAdLoaded() {
                super.onRewardedVideoAdLoaded();
            }

            @Override
            public void onRewardedVideoAdOpened() {
                super.onRewardedVideoAdOpened();
            }

            @Override
            public void onRewardedVideoAdStarted() {
                super.onRewardedVideoAdStarted();
            }

            @Override
            public void onRewardedAd(RewardItem rewardItem) {
                super.onRewardedAd(rewardItem);
            }

            @Override
            public void onRewardedVideoAdClosed() {
                super.onRewardedVideoAdClosed();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {
                super.onRewardedVideoAdLeftApplication();
            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                super.onRewardedVideoAdFailedToLoad(i);
            }

            @Override
            public void onRewardedVideoAdCompleted() {
                super.onRewardedVideoAdCompleted();
            }
        });

    }
}
