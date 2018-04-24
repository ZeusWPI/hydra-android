# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/flash/Android/Sdk/tools/proguard/proguard-android.txt
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

# Keep all our classes
-keep class be.ugent.zeus.hydra.** {*;}

# Rules for apache
-dontwarn org.apache.oltu.**
-dontwarn org.slf4j.**

# Disable proguard for ThreeTenABP, since it causes a lot of issues.
-keep class org.threeten.bp.** { *; }

# Rules for OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**
-dontwarn javax.annotation.**
-dontwarn org.conscrypt.**
# A resource is loaded with a relative path so the package of this class must be preserved.
-keepnames class okhttp3.internal.publicsuffix.PublicSuffixDatabase

# Rules for Moshi (duplicates from OkHttp are removed)
-keepclasseswithmembers class * {
    @com.squareup.moshi.* <methods>;
}
-keep @com.squareup.moshi.JsonQualifier interface *