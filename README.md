# InAppUpdateManager
A wrapper for Android [In-App-Update Library](https://developer.android.com/guide/playcore/in-app-updates)


# Getting Started

## Note

* In-app updates works only with devices running Android 5.0 (API level 21) or higher.
* In-app updates support apps running on only Android mobile devices and tablets, and Chrome OS devices.

There are two update modes.
### 1. Flexible: 
    A user experience that provides background download and installation with graceful state monitoring. This UX is appropriate when it’s acceptable for the user to use the app while downloading the update. For example, you want to urge users to try a new feature that’s not critical to the core functionality of your app.
  
<img src="https://developer.android.com/images/app-bundle/flexible_flow.png" alt="" width="825"></p>

### 2. Immediate: 
    A full screen user experience that requires the user to update and restart the app in order to continue using the app. This UX is best for cases where an update is critical for continued use of the app. After a user accepts an immediate update, Google Play handles the update installation and app restart.
    
 <img src="https://developer.android.com/images/app-bundle/immediate_flow.png" alt="" width="528"></p>

## Usage

A simple implemenatation of the InAppUpdateManager is
```java
InAppUpdateManager
    .with(this)
    .startUpdate()
```
InAppUpdateManager provides a set of customisation options too. You can apply them by using kotlin `apply` function.

* **Update Type**

By default, update type is set to `InAppUpdateType.FLEXIBLE`.
You can implement force update by setting update type to `InAppUpdateType.IMMEDIATE`
```java
InAppUpdateManager
    .with(this)
    .apply {
        updateType = InAppUpdateType.IMMEDIATE
    }
    .startUpdate()
```
* **Resume Update**

We can define wether to resume updates or not if the user leaves the screen and come back after some time.
By deafult this is set to true
```java
InAppUpdateManager
    .with(this)
    .apply {
        shouldResumeUpdate = false
    }
    .startUpdate()
```
* **Snackbar**

Once the flexible update is downloaded, InAppUpdateManager will show a snackbar to get user confirmation to install the update.
You can customise the snackbar like below
```java
InAppUpdateManager
    .with(this)
    .apply {
        snackbarText = getString(R.string.update_confirmation_message)
        snackbarTextColor = ContextCompat.getColor(this,R.color.snackbar_text_color)
        snackbarAction = getString(R.string.update_confirmation_action)
        snackbarActionTextColor = ContextCompat.getColor(this,R.color.snackbar_action_color)
    }
    .startUpdate()
```

* **Listener**

InAppUpdateManager provides an option to set listener for install state changes.
```java
InAppUpdateManager
    .with(this)
    .apply {
        listener = { state ->
            when {
                state.isCanceled -> Log.d(TAG, "Canceled")
                state.isDownloaded -> Log.d(TAG, "Downloaded ${state.bytesDownloaded}")
                state.isDownloading -> Log.d(TAG, "Downloading ${state.totalBytesToDownload}")
                state.isFailed -> Log.d(TAG, "Failed ${state.installErrorCode}")
                state.isInstalled -> Log.d(TAG, "Installed")
                state.isInstalling -> Log.d(TAG, "Installing ${state.bytesDownloaded}")
                state.isPending -> Log.d(TAG, "Pending ${state.totalBytesToDownload}")
                state.isUnknown -> Log.d(TAG, "Unknown")
            }
        }
    }
    .startUpdate()
```

* **Custom Install Alert**

Sometimes you may want to show some custom alert instead of the snackbar. In this scenario you can tell InAppUpdateManager to don`t show the snackbar and you can show your custom alert by listening to install state.
```java
val inAppUpdateManager = InAppUpdateManager.with(this)
inAppUpdateManager
    .apply {
        shouldShowSnackbar = false
        listener = { state ->
            if (state.isDownloaded)
                showInstallAlert()
            }
        }
inAppUpdateManager.startUpdate()
```
On user confirmation, please call
```java
inAppUpdateManager.completeUpdate()
```

## Test with internal app-sharing
With [internal app sharing](https://support.google.com/googleplay/android-developer/answer/9303479), you can quickly share an app bundle or APK with your internal team and testers by uploading the app bundle you want to test to the Play Console.

You can also use internal app sharing to test in-app updates, as follows:

1. On your test device, make sure you've already installed a version of your app that meets the following requirements:

      * The app was installed using an internal app sharing URL
      * Supports in-app updates
      * Uses a version code that's lower than the updated version of your app

2. Follow the Play Console instructions on how to [share your app internally](https://support.google.com/googleplay/android-developer/answer/9303479). Make sure you upload a version of your app that uses a version code that's higher than the one you have already installed on the test device.

3. On the test device, only click the internal app-sharing link for the updated version of your app. Do not install the app from the Google Play Store page you see after clicking the link.

4. Open the app from the device's app drawer or home screen. The update should now be available to your app, and you can test your implementation of in-app updates.
    
## License    

    Copyright 2020 Vipin KT

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
