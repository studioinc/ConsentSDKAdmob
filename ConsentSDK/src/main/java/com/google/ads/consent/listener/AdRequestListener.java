package com.google.ads.consent.listener;

public interface AdRequestListener {
    void isAdsPersonalize();
    void isAdsNonPersonalize();
    void isUserNotFromEeaPersonalize();
}