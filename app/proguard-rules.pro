# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified

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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hideUserBonus the original source file name.
#-renamesourcefileattribute SourceFile
-include /lib/dexguard-release.pro

# The support library contains references to newer platform versions.
# Don't warn about those in case this app is linking against an older
# platform version.  We know about them, and they are safe.
-dontwarn android.support.**

# Understand the @Keep support annotation.
-keep class android.support.annotation.Keep

-keep @android.support.annotation.Keep class * {*;}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <methods>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <fields>;
}

-keepclasseswithmembers class * {
    @android.support.annotation.Keep <init>(...);
}

-dontwarn com.android.volley.**
-keepattributes InnerClasses
-dontoptimize
-keep class com.android.volley.** { *; }
-keep class com.android.volley.** { *; }

-dontwarn com.google.code.gson.**
-keep class com.google.code.gson.** { *; }
-keep interface com.google.code.gson.** { *; }

-dontwarn com.google.android.gms.**
-keep class com.google.android.gms.** { *; }
-keep interface com.google.android.gms.** { *; }

-dontwarn com.discover.**
-keepattributes InnerClasses
-dontoptimize
-keep class com.discover.** { *; }
-keep interface com.discover.** { *; }

-dontwarn io.jsonwebtoken.**
-keepattributes InnerClasses
-dontoptimize
-keep class io.jsonwebtoken.** { *; }
-keep interface io.jsonwebtoken.** { *; }

-dontwarn com.fasterxml.**
-keepattributes InnerClasses
-dontoptimize
-keep class com.fasterxml.** { *; }
-keep interface com.fasterxml.** { *; }

-dontwarn net.danlew.**
-keepattributes InnerClasses
-dontoptimize
-keep class net.danlew.** { *; }
-keep interface net.danlew.** { *; }

-dontwarn com.symbiotic.**
-keepattributes InnerClasses
-dontoptimize
-keep class com.symbiotic.** { *; }
-keep interface com.symbiotic.** { *; }

-dontoptimize
-keep class com.visa.** { *; }
-keep interface com.visa.** { *; }
-dontwarn com.visa.**

-dontoptimize
-keep class com.threatmetrix.** { *; }
-keep interface com.threatmetrix.** { *; }

#Google libs
-keep class com.google.** { *;}
-dontwarn com.google.common.**
-dontwarn com.google.ads.**
-dontwarn android.support.v4.**
-dontwarn android.support.v7.**
-keep class com.google.android.gms.**
-dontwarn com.google.android.gms.*
-keep class com.google.firebase.messaging.** { *; }

#Rx
-keep class rx.internal.**
-keep class rx.** { *; }
-keep interface rx.** { *; }
-dontwarn rx.**
-dontwarn rx.internal.util.unsafe.**

-keepattributes Signature
-keepattributes *Annotation*

-dontwarn java.lang.invoke.*

-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    long producerNode;
    long consumerNode;
}

-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
# Retrofit 2.X
## https://square.github.io/retrofit/ ##
-dontwarn retrofit.**
-keep class retrofit.** { *; }
-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

-dontwarn retrofit2.**
-keep class retrofit2.** { *; }
-keep class retrofit2.http.** { *; }
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Annotation
-keepattributes Exceptions
-keep class okhttp3.* { *; }
-keep interface okhttp3.* { *; }
-dontwarn okhttp3.*

-keepclasseswithmembers class * {
    @retrofit2.http.* <methods>;
}

# Okio
-keep class sun.misc.Unsafe { *; }
-dontwarn java.nio.file.*
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement
-dontwarn okio.**
-keep class com.squareup.okhttp.** { *; }
-keep interface com.squareup.okhttp.** { *; }
-dontwarn com.squareup.okhttp.**

-keep class com.squareup.retrofit2.**
-dontwarn com.squareup.retrofit2.**

-keep class com.squareup.okhttp3.**
-dontwarn com.squareup.okhttp3.**
-keep class okhttp3.** { *; }

#Enable proguard Picasso
-keep class com.squareup.picasso.**
-dontwarn com.squareup.picasso.**

#Enable joda
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }

#Junit
-dontnote org.junit.**

#AppCompat
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }
-dontwarn android.support.design.**

#Apache org
-dontnote android.net.http.*
-dontnote org.apache.commons.codec.**
-dontnote org.apache.http.**

-dontwarn rx.internal.util.unsafe.**

# Class names are needed in reflection
-keepnames class com.amazonaws.**
-keepnames class com.amazon.**
# Request handlers defined in request.handlers
-keep class com.amazonaws.services.**.*Handler
# The following are referenced but aren't required to run
-dontwarn com.fasterxml.jackson.**
-dontwarn org.apache.commons.logging.**
# Android 6.0 release removes support for the Apache HTTP client
-dontwarn org.apache.http.**
# The SDK has several references of Apache HTTP client
-dontwarn com.amazonaws.http.**

#Google zxing
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-verbose

# ADDED
-keepattributes *Annotation*
-keep public class com.google.vending.licensing.ILicensingService
-keep public class com.android.vending.licensing.ILicensingService

# For native methods, see http://proguard.sourceforge.net/manual/examples.html#native
-keepclasseswithmembernames class * {
    native <methods>;
}

# keep setters in Views so that animations can still work.
# see http://proguard.sourceforge.net/manual/examples.html#beans
-keepclassmembers public class * extends android.view.View {
   void set*(***);
   *** get*();
}

# We want to keep methods in Activity that could be used in the XML attribute onClick
-keepclassmembers class * extends android.app.Activity {
   public void *(android.view.View);
}

#Kotlin
-dontwarn kotlin.**

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

-keepclassmembers class **$WhenMappings {
    <fields>;
}

-assumenosideeffects class kotlin.jvm.internal.Intrinsics {
    static void checkParameterIsNotNull(java.lang.Object, java.lang.String);
}

#java
-keepattributes *Annotation*
-keep class javax.annotation.** { *; }

-dontwarn java.lang.**
-dontwarn sun.misc.Unsafe
-dontwarn org.apache.**

#M4U
-keep public class br.com.mobicare.cielo.login.domain.LoginParams { *; }
-keep public class br.com.mobicare.cielo.meusRecebimentos.domains.entities.BankMaskVO { *;}

# ReTrace
-keepattributes SourceFile,LineNumberTable

-ignorewarnings

-dontwarn kotlin.**
-keepclassmembers class ** {public *;}
-keepattributes Signature,SourceFile,LineNumberTable

-keep public class * extends br.com.mobicare.cielo.commons.router.RouterFragmentInActivityKt

-keepclassmembers class * extends br.com.mobicare.cielo.commons.ui.BaseFragment {
 public <init>();
 public <init>(android.content.Context);
}
-keepclassmembers class * extends br.com.mobicare.cielo.commons.navigation.CieloNavigationListener {
 public <init>();
 public <init>(android.content.Context);
}


# For using GSON @Expose annotation
-keepattributes EnclosingMethod

# Gson specific classes
-keepattributes *Annotation*,Signature
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }

-keepclassmembers class * {
    private <fields>;
}

-keepclassmembers class * {
    public <fields>;
}


# Retrofit2
-dontnote retrofit2.Platform
-dontwarn retrofit2.Platform$Java8
-keepattributes Signature
-keepattributes Exceptions
-dontwarn okio.**
-keep class retrofit2.** { *; }

#RX
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}
-keep class rx.internal.util.unsafe.** { *; }
-dontwarn sun.misc.Unsafe



# ReTrace

-keepattributes SourceFile,LineNumberTable


-dontwarn android.support.v7.**
-keep class android.support.v7.** { *; }
-keep interface android.support.v7.** { *; }

#Dexguard
-virtualizecode class br.com.mobicare.cielo.*
-obfuscatecode,high class br.com.mobicare.cielo.**
-encryptstrings class br.com.mobicare.cielo.*
-encryptresources string

-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**
-keep class * extends android.support.v4.app.Fragment{}
-keep class br.com.mobicare.cielo.commons.navigation.**
-keep class * extends androidx.fragment.app.Fragment{}
-keepnames class * extends android.os.Parcelable
-keepnames class * extends java.io.Serializable

-dontwarn br.com.stoneage.**
-dontoptimize
-keep enum br.com.stoneage.** { *; }
-keep class br.com.stoneage.** { *; }
-keep class br.com.stoneage.**{
    public <methods>;
}

#SalesForce MKTCLOUD
-keep class com.salesforce.** { *; }
-dontwarn com.salesforce.**
-keep enum com.salesforce.** { *; }

-keep class br.com.mobicare.cielo.service.** { *; }
-keepnames class br.com.mobicare.cielo.service.** { *; }
-keep class * extends com.google.firebase.messaging.FirebaseMessagingService
-keep class br.com.mobicare.cielo.CieloApplication

-keep class com.firebase.** { *; }
-keep class org.apache.** { *; }
-keepnames class com.fasterxml.jackson.** { *; }
-keepnames class javax.servlet.** { *; }
-keepnames class org.ietf.jgss.** { *; }
-dontwarn org.w3c.dom.**
-dontwarn org.joda.time.**
-dontwarn org.shaded.apache.**
-dontwarn org.ietf.jgss.**

# Keep the classes that are deserialized by GSON
-keep class com.combateafraude.helpers.server.model.** { <fields>; }

-keep class com.appdynamics.eumagent.runtime.DontObfuscate
-keep @com.appdynamics.eumagent.runtime.DontObfuscate class * { *; }

-keep class sym.c.** { *; }
-keepnames class sym.c.** { *; }
-keepclassmembers class sym.c.** { *; }
-keep enum sym.c.** { *; }
-keepnames enum sym.c.** { *; }
-keepclassmembers enum sym.c.** { *; }

-keepnames class com.symbiotic.** { *; }
-keepclassmembers class com.symbiotic.** { *; }
-keepnames enum com.symbiotic.** { *; }
-keepclassmembers enum com.symbiotic.** { *; }
-dontwarn com.mastercard.terminalsdk.**
-keep class com.mastercard.terminalsdk.** {*;}
-keep class com.mastercard.terminalsdk.**$** {*;}
-keep interface com.mastercard.terminalsdk.** { *; }
-keep enum com.mastercard.terminalsdk.** { *; }
-keep class com.visa.** {*;}
-keep class com.visa.**$** {*;}
-keep enum com.visa.** { *; }

-keepattributes SourceFile,LineNumberTable
-keep public class * extends java.lang.Exception


##---------------Gson---------------
-keep class sun.misc.Unsafe {
<fields>;
<methods>;
}

-keep class com.google.gson.examples.android.model.** {
<fields>;
<methods>;
}
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

##---------------SDK Jar---------------
-keep class com.mastercard.terminalsdk.** {*;}
-keep class com.mastercard.terminalsdk.**$** {*;}
-keep interface com.mastercard.terminalsdk.** { *; }
-keep enum com.mastercard.terminalsdk.** { *; }

##---------------BouncyCastle---------------
-keep class org.bouncycastle.** { *; }
-keepnames class org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

##---------------SpongyCastle---------------
-keep class org.spongycastle.** { *; }
-keepclasseswithmembernames class javax.crypto.**
-keepclasseswithmembernames class javax.crypto.Cipher
-keep interface javax.crypto.** { *; }
-keepclasseswithmembernames class java.security.**
-keep interface java.security.**
-keepclasseswithmembernames class java.security.** { *; }
-keep interface java.security.** { *; }
-keepclasseswithmembernames class java.security.Key

##---------------JsonWebToken---------------
-keep class io.jsonwebtoken.** { *; }
-keepnames class io.jsonwebtoken.** { *; }
-keepnames interface io.jsonwebtoken.** { *; }

##---------------Joda---------------
-dontwarn org.joda.convert.**
-dontwarn org.joda.time.**
-keep class org.joda.time.** { *; }
-keep interface org.joda.time.** { *; }
-keep class net.danlew.** { *; }

##---------------Unico---------------
-keep class kotlin.coroutines.**
-keep class kotlinx.coroutines.**

-keep class com.facetec.sdk.** { *; }
-keep class com.acesso.acessobio_android.** { *; }
-keep class io.unico.** { *; }

-keep class br.com.makrosystems.haven.** { *; }
-keep class HavenSDK.**{ *; }
-keep class HavenSDK** { *; }