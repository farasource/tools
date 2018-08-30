# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in C:\Users\SmartApp\AppData\Local\Android\Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.** { *; }
-keepclassmembers enum * { *; }
-keep class **.R$* { *; }
-keepnames class * extends android.app.Activity
-dontwarn com.unity3d.player.**
-keep class com.auth0.jwt.** { *; }
-dontwarn com.auth0.jwt.**

-keep class com.android.vending.billing
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-keep public class android.support.v7.widget.SearchView {
*;
 }

-keep class com.wang.avi.** { *; }
-keep class com.wang.avi.indicators.** { *; }