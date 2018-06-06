package com.google.ads.consent.listener;

import com.google.android.gms.ads.reward.RewardItem;

public abstract class AdCallListener {
    public void onAdReady(){}

    //TODO: InterstialAd Ad Callback result
    public void onInterstialAdClosed(){}
    public void onInterstialAdOpened(){}
    public void onInterstialAdClicked(){}
    public void onInterstialAdLoaded(){}
    public void onInterstialAdFailedToLoad(int i){}
    public void onInterstialAdImpression(){}
    public void onInterstialAdLeftApplication(){}

    //TODO: Banner Ad Callback result
    public void onBannerAdClosed(){}
    public void onBannerAdOpened(){}
    public void onBannerAdClicked(){}
    public void onBannerAdLoaded(){}
    public void onBannerAdFailedToLoad(int i){}
    public void onBannerAdImpression(){}
    public void onBannerAdLeftApplication(){}

    //TODO: Rewarded Video Callback result
    public void onRewardedVideoAdLoaded(){}
    public void onRewardedVideoAdOpened(){}
    public void onRewardedVideoAdStarted(){}
    public void onRewardedAd(RewardItem rewardItem){}
    public void onRewardedVideoAdClosed(){}
    public void onRewardedVideoAdLeftApplication(){}
    public void onRewardedVideoAdFailedToLoad(int i){}
    public void onRewardedVideoAdCompleted(){}

}
