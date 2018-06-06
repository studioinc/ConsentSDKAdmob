package com.google.ads.consent.builder.kotlin

import android.content.Context
import android.util.Log

import com.google.ads.consent.ConsentForm
import com.google.ads.consent.ConsentFormListener
import com.google.ads.consent.ConsentInformation
import com.google.ads.consent.ConsentStatus
import com.google.ads.consent.DebugGeography
import com.google.ads.consent.listener.AdRequestListener
import com.google.ads.consent.listener.ConsentInfoUpdateListener

import java.net.MalformedURLException
import java.net.URL

class ConsentGDPR(internal var context: Context) {


    init {
        this.context = context
    }

    fun show() {
        checkConsentGDPR()
    }


    class Builder {
        fun setContext(context: Context): Builder {
            ConsentGDPR.context = context
            return this
        }

        fun setTestDeviceId(testDeviceId: String): Builder {
            ConsentGDPR.testDeviceId = testDeviceId
            return this
        }

        fun setPrivacyUrl(privacyUrl: String): Builder {
            ConsentGDPR.privacyUrl = privacyUrl
            return this
        }

        fun setPublisherId(publisherId: String): Builder {
            ConsentGDPR.publisherId = publisherId
            return this
        }

        fun setDebugGeography(isDebugGeographyEea: Boolean): Builder {
            ConsentGDPR.isDebugGeographyEea = isDebugGeographyEea
            return this
        }

        fun listener(listener: AdRequestListener): Builder {
            consentGDPRListener = listener
            return this
        }

        fun build(): ConsentGDPR {
            return ConsentGDPR(context!!.applicationContext)
        }

        companion object {

            val testDeviceId: String
                get() = testDeviceId

            val privacyUrl: String
                get() = privacyUrl

            val publisherId: String
                get() = publisherId
        }
    }

    companion object {
        private val TAG = ConsentGDPR::class.java!!.getName()
        private var consentGDPR: ConsentGDPR? = null
        private var context: Context? = null
        private var testDeviceId = ""
        private var privacyUrl = ""
        private var publisherId = ""
        private var isDebugGeographyEea = false
        private var consentGDPRListener: AdRequestListener? = null
        private var form: ConsentForm? = null

        @Synchronized
        fun getInstance(context: Context): ConsentGDPR {
            if (consentGDPR == null) {
                consentGDPR = ConsentGDPR(context)
            }
            return consentGDPR as ConsentGDPR
        }

        fun checkConsentGDPR() {
            val consentInformation = ConsentInformation.getInstance(context)
            consentInformation.addTestDevice(Builder.testDeviceId)
            // Geography appears as in EEA for test devices.
            if (isDebugGeographyEea) {
                consentInformation.setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_EEA)
            }
            // Geography appears as not in EEA for debug devices.
            //ConsentInformation.getInstance(this).setDebugGeography(DebugGeography.DEBUG_GEOGRAPHY_NOT_EEA);
            val publisherIds = arrayOf(Builder.publisherId)
            consentInformation.requestConsentInfoUpdate(publisherIds, object : ConsentInfoUpdateListener {
                override fun onConsentInfoUpdated(consentStatus: ConsentStatus) {
                    if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown) {
                        Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown.toString() + "")
                        if (consentStatus == ConsentStatus.UNKNOWN) {
                            form = showForm()
                            form!!.load()
                            if (form != null) {
                                showForm()
                            }
                        } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                            ConsentInformation.getInstance(context).consentStatus = ConsentStatus.PERSONALIZED
                            consentGDPRListener!!.isAdsPersonalize()
                        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                            ConsentInformation.getInstance(context).consentStatus = ConsentStatus.NON_PERSONALIZED
                            consentGDPRListener!!.isAdsNonPersonalize()
                        }
                    } else {
                        consentGDPRListener!!.isUserNotFromEeaPersonalize()
                        Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown.toString() + "")
                    }
                }

                override fun onFailedToUpdateConsentInfo(errorDescription: String) {

                }
            })

        }

        fun showForm(): ConsentForm {
            var privacyUrl: URL? = null
            try {
                privacyUrl = URL(Builder.privacyUrl)
            } catch (e: MalformedURLException) {
                e.printStackTrace()
            }

            return ConsentForm.Builder(context, privacyUrl).withListener(object : ConsentFormListener() {
                override fun onConsentFormLoaded() {
                    form!!.show()
                    Log.e("onConsentFormLoaded", "consentForm.show()")
                }

                override fun onConsentFormOpened() {
                    Log.e("onConsentFormOpened", "onConsentFormOpened()")
                }

                override fun onConsentFormClosed(consentStatus: ConsentStatus, userPrefersAdFree: Boolean?) {
                    Log.e("onConsentFormClosed", consentStatus.toString())
                    if (ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown) {
                        Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown.toString() + "")
                        if (consentStatus == ConsentStatus.UNKNOWN) {
                            form = showForm()
                            form!!.load()
                            if (form != null) {
                                showForm()
                            }
                        } else if (consentStatus == ConsentStatus.PERSONALIZED) {
                            ConsentInformation.getInstance(context).consentStatus = ConsentStatus.PERSONALIZED
                            consentGDPRListener!!.isAdsPersonalize()
                        } else if (consentStatus == ConsentStatus.NON_PERSONALIZED) {
                            ConsentInformation.getInstance(context).consentStatus = ConsentStatus.NON_PERSONALIZED
                            consentGDPRListener!!.isAdsNonPersonalize()
                        }
                    } else {
                        consentGDPRListener!!.isUserNotFromEeaPersonalize()
                        //activityListener.onStartActivity();
                        Log.d(TAG, ConsentInformation.getInstance(context).isRequestLocationInEeaOrUnknown.toString() + "")
                    }
                }

                override fun onConsentFormError(errorDescription: String) {
                    Log.e("onConsentFormError", errorDescription)
                }
            }).withPersonalizedAdsOption()
                    .withNonPersonalizedAdsOption()
                    .build()
        }


        fun addSharedPrefUserFromEEA(bool: Boolean) {
            val preferences = context!!.getSharedPreferences("gdpr", Context.MODE_PRIVATE)
            val editor = preferences.edit()
            editor.putBoolean("is_ads_personalize", bool)
            editor.apply()
            editor.commit()
        }

        val isUserFromEeaOrUnknown: Boolean
            get() {
                val preferences = context!!.getSharedPreferences("gdpr", Context.MODE_PRIVATE)
                return preferences.getBoolean("is_ads_personalize", false)
            }
    }


}
