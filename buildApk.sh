#!/bin/sh

./gradlew :dynamic_feature1:packageDebug :dynamic_feature2:packageDebug

adb push dynamic_feature1/build/outputs/apk/debug/dynamic_feature1-debug.apk /sdcard
adb push dynamic_feature2/build/outputs/apk/debug/dynamic_feature2-debug.apk /sdcard

./gradlew :app:assembleDebug :app:installDebug

adb shell am start -n "io.zcx.sai.demo/io.zcx.apk.demo.MainActivity" -a android.intent.action.MAIN -c android.intent.category.LAUNCHER