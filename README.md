# ConsentSDK GDPR Android Admob Library
library to interact with google Admob consent SDK GDPR easily.

# Example:
Demo version app has been added for a better explanation of the library.

# Required Dependencies:
> Add it in your root build.gradle at the end of repositories:
```gradle
    allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
> Step 2. Add the dependency
```gradle
    dependencies {
	        implementation 'com.github.studioinc:ConsentSDKAdmob:v1.0.0'
	}
```
---
# Get requirement you need:
> **Get your publisher id from admob:**
![Publisher Id](http://lh3.googleusercontent.com/jbo5TVXuXU0DEHD3dfyutomLUTtsKTkg9LunCXh8R_DTv7T__91P0e4KLtAt9foPzQ=w895)
[Source](https://support.google.com/admob/answer/2784578?hl=en)

> To get the device id you have to initialize an adrequest


> How checkConsent works:
```
- Check the location of the user if it's within EEA and with unknown status.
- If the user is within EEA and with unknown status show the dialog for consent with two options to see relevant ads or to show less relevant ads.
- The function retrieve a callback after the consent has been submitted or if it's not necessary not show the dialog.
- It's save the consent of the user and if the user is not within EEA it saves show relevant ads status.
```

# Note:
> This library is just an editing of the official [Google Consent SDK Android](https://github.com/googleads/googleads-consent-sdk-android) with some tweaks and with some helper class to simplify the comply of the app to GDPR policy.

> That's it, if you followed the steps above your app will be comply with the **GDPR** policy.

# Made by Mohammed Ajaroud :heart: :earth_africa:.
