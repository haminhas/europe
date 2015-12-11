# README #

Set-up for google api key

1. Paste the command into terminal: key tool -list -v -keystore ~/.android/debug.keystore -alias androiddebugkey -storepass android -keypass android

2. Copy the SHA1 key

3. Follow the steps at the following address: https://console.developers.google.com/flows/enableapi?apiid=maps_android_backend&keyType=CLIENT_SIDE_ANDROID. After you have created a project You will be directed to create key and when you do this for the package name use 'boysenberry.europe' and for 	SHA-1 certificate fingerprint field paste the key you got before.

4. Paste the generated key into google_maps_api.xml which is found in the values folder.

NOTE: Best option to run on tablet rather than emulator as sometimes the maps don't work on the emulator.