package com.medstudioinc.consent.kotlin

import android.annotation.SuppressLint
import android.os.Bundle
import com.consentsdkgdpr.consentsdkforgdpr.R
import com.google.ads.consent.ads.AdHelperWithGDPR
import com.google.ads.consent.listener.AdCallListener
import com.google.android.gms.ads.reward.RewardItem

class MainActivity : AdHelperWithGDPR() {


    @SuppressLint("MissingSuperCall")
    override fun onStart() {
        super.onStart()
        privacyUrl = "http://your_url_privacy_here"
        publisherId = "your_publisher_id_here"
        isTestDeviceDebugGeography = true
        applyBuilder()
    }
    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initAdAppId(R.string.admob_app_unit_id)

        initAdFullScreen(R.string.admob_interstitial_unit_id)

        initAdRewardedVideo(R.string.admob_rewarded_video_unit_id)

        initAdBanner(R.id.adView)


        addAdmobCallListener(object : AdCallListener(){
            override fun onAdReady() {
                super.onAdReady()
                showAdBanner()
                showAdFullScreen()
                showRewardedVideo()
            }

            override fun onInterstialAdClosed() {
                super.onInterstialAdClosed()
            }

            override fun onInterstialAdOpened() {
                super.onInterstialAdOpened()
            }

            override fun onInterstialAdClicked() {
                super.onInterstialAdClicked()
            }

            override fun onInterstialAdLoaded() {
                super.onInterstialAdLoaded()
            }

            override fun onInterstialAdFailedToLoad(i: Int) {
                super.onInterstialAdFailedToLoad(i)
            }

            override fun onInterstialAdImpression() {
                super.onInterstialAdImpression()
            }

            override fun onInterstialAdLeftApplication() {
                super.onInterstialAdLeftApplication()
            }

            override fun onBannerAdClosed() {
                super.onBannerAdClosed()
            }

            override fun onBannerAdOpened() {
                super.onBannerAdOpened()
            }

            override fun onBannerAdClicked() {
                super.onBannerAdClicked()
            }

            override fun onBannerAdLoaded() {
                super.onBannerAdLoaded()
            }

            override fun onBannerAdFailedToLoad(i: Int) {
                super.onBannerAdFailedToLoad(i)
            }

            override fun onBannerAdImpression() {
                super.onBannerAdImpression()
            }

            override fun onBannerAdLeftApplication() {
                super.onBannerAdLeftApplication()
            }

            override fun onRewardedVideoAdLoaded() {
                super.onRewardedVideoAdLoaded()
            }

            override fun onRewardedVideoAdOpened() {
                super.onRewardedVideoAdOpened()
            }

            override fun onRewardedVideoAdStarted() {
                super.onRewardedVideoAdStarted()
            }

            override fun onRewardedAd(rewardItem: RewardItem?) {
                super.onRewardedAd(rewardItem)
            }

            override fun onRewardedVideoAdClosed() {
                super.onRewardedVideoAdClosed()
            }

            override fun onRewardedVideoAdLeftApplication() {
                super.onRewardedVideoAdLeftApplication()
            }

            override fun onRewardedVideoAdFailedToLoad(i: Int) {
                super.onRewardedVideoAdFailedToLoad(i)
            }

            override fun onRewardedVideoAdCompleted() {
                super.onRewardedVideoAdCompleted()
            }
        })
    }
}
