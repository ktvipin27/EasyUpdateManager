# EasyUpdateManager
[ ![Download](https://api.bintray.com/packages/ktvipin27/EasyUpdateManager/com.ktvipin.easyupdatemanager/images/download.svg) ](https://bintray.com/ktvipin27/EasyUpdateManager/com.ktvipin.easyupdatemanager/_latestVersion)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/zerobranch/android-remote-debugger/blob/master/LICENSE)

A wrapper for Android [In-App-Update Library](https://developer.android.com/guide/playcore/in-app-updates)


Built with ❤︎ by [Vipin KT](https://twitter.com/ktvipin27)

# Getting Started

## Note

* In-app updates works only with devices running Android 5.0 (API level 21) or higher.
* In-app updates support apps running on only Android mobile devices and tablets, and Chrome OS devices.

There are two update modes.
### 1. Flexible: 
    A user experience that provides background download and installation with graceful state monitoring. This UX is appropriate when it’s acceptable for the user to use the app while downloading the update. For example, you want to urge users to try a new feature that’s not critical to the core functionality of your app.
  
<img src="https://developer.android.com/images/app-bundle/flexible_flow.png" alt="" width="525"></p>

### 2. Immediate: 
    A full screen user experience that requires the user to update and restart the app in order to continue using the app. This UX is best for cases where an update is critical for continued use of the app. After a user accepts an immediate update, Google Play handles the update installation and app restart.
    
 <img src="https://developer.android.com/images/app-bundle/immediate_flow.png" alt="" width="350"></p>

## Installation

Add this in your app's build.gradle file:

<details open>
<summary>Groovy</summary>
  
```groovy
  dependencies {
       implementation 'com.ktvipin:easyupdatemanager:1.0.0-beta'
  }
```

</details>
<details open>
<summary>Kotlin</summary>
  
```kotlin
  dependencies {
       implementation("com.ktvipin:easyupdatemanager:1.0.0-beta")
  }
```

</details>

## Usage

A simple implementation of the EasyUpdateManager is
```kotlin
EasyUpdateManager
    .with(this)
    .startUpdate()
```

## Customisation

### Options

EasyUpdateManager provides a set of options for customisation.

```kotlin
EasyUpdateManager
    .with(this)
    .options {
        resumeUpdate = true
        forceUpdate = true
        updateType = UpdateType.IMMEDIATE
        updatePriority = UpdatePriority.FIVE
        daysForFlexibleUpdate = 2
        customNotification = false
    }
    .startUpdate()
```



| Option                  | Description                                                                                                                                                                                 | Values                                                                                                                             | Default Value            |
|-------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------------------------------------------------|--------------------------|
| `updateType`            | Type of update                                                                                                                                                                              | UpdateType.FLEXIBLE, UpdateType.IMMEDIATE                                                                                          | UpdateType.FLEXIBLE      |
| `resumeUpdate`          | Whether to resume updates or not if the user leaves the screen and come back after some time.                                                                                               | true, false                                                                                                                        | true                     |
| `forceUpdate`           | Whether to force the user to install the update (available only for `IMMEDIATE` updates).                                                                                                   | true, false                                                                                                                        | true                     |
| `updatePriority`        | Check the priority level for a given update ([more info](https://developer.android.com/guide/playcore/in-app-updates#check-priority))                                                       | UpdatePriority.ONE, UpdatePriority.TWO,  UpdatePriority.THREE,  UpdatePriority.FOUR,  UpdatePriority.FIVE                          | UpdatePriority.ONE       |
| `daysForFlexibleUpdate` | To check for the number of days that have passed since the Google Play Store learns of an update ([more info](https://developer.android.com/guide/playcore/in-app-updates#check-staleness)) | Any Integer                                                                                                                        | 0                        |
| `customNotification`    | To show some custom alert instead of the snackbar                                                                                                                                           | true, false                                                                                                                        | false                    |


### Snackbar

Once the flexible update is downloaded, EasyUpdateManager will show a snackbar to get user confirmation to install the update.
You can customise the snackbar with `snackbar` lambda.
```kotlin
EasyUpdateManager
    .with(this)
    .snackbar {
        text = getString(R.string.update_confirmation_message)
        textColor = ContextCompat.getColor(this,R.color.snackbar_text_color)
        action = getString(R.string.update_confirmation_action)
        actionTextColor = ContextCompat.getColor(this,R.color.snackbar_action_color)
    }
    .startUpdate()
```

### Listener

EasyUpdateManager provides an option to set listener for install state changes.
```kotlin
EasyUpdateManager
    .with(this)
    .listener { state ->
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
    .startUpdate()
```

### Custom Install Alert

Sometimes you may want to show some custom alert instead of the snackbar. In this scenario you can tell the EasyUpdateManager to not show the snackbar by setting `customNotification = false` and how your custom alert by listening to install state.
```kotlin
val easyUpdateManager = EasyUpdateManager.with(this)
easyUpdateManager
    .options {
        customNotification = false
    }
    .listener { state ->
        if (state.isDownloaded)
            showInstallAlert()
    }
easyUpdateManager.startUpdate()
```
On user confirmation, call
```kotlin
easyUpdateManager.completeUpdate()
```

### Progress

You can show downloading progress by listening to install state.
```kotlin
EasyUpdateManager
    .with(this)
    .listener { state ->
        when {
            state.isDownloading -> showProgress(state.totalBytesToDownload, state.totalBytesToDownload)
            state.isDownloaded -> hideProgress()
        }
    }
    .startUpdate()
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
