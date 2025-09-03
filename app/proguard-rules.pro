-keep class org.bouncycastle.** { *; }
-keep interface org.bouncycastle.** { *; }
-dontwarn org.bouncycastle.**

-keep class com.jcraft.jsch.** { *; }
-dontwarn com.jcraft.jsch.**

-keep class java.security.KeyFactory { *; }
-keep class java.security.KeyPairGenerator { *; }
-keep class java.security.spec.** { *; }

-keep class com.google.crypto.tink.** { *; }
-dontwarn com.google.crypto.tink.**

-keep class kotlinx.serialization.** { *; }
-dontwarn kotlinx.serialization.**
