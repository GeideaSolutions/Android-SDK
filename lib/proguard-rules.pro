# https://www.guardsquare.com/en/products/proguard/manual/examples#library

-keepparameternames
-renamesourcefileattribute SourceFile
-keepattributes Exceptions,InnerClasses,Signature,Deprecated,
                SourceFile,LineNumberTable,EnclosingMethod

# Completely remove all SDK logging through Logger from the SDK binary. However, in HttpsClient
# there is some very basic raw network calls logging which is active in release build only.
-assumenosideeffects class net.geidea.paymentsdk.internal.util.Logger {
    public static void logv(...);
    public static void logd(...);
    public static void logi(...);
    public static void logw(...);
    public static void loge(...);
}

# Preserve all public classes, and their public and protected fields and
# methods.

-keep public class net.geidea.paymentsdk.flow.** {
    public protected*;
}
-keep public class net.geidea.paymentsdk.model.** {
    public protected*;
}
-keep public class net.geidea.paymentsdk.ui.** {
    public protected*;
}
-keep public class net.geidea.paymentsdk.api.** {
    public protected*;
}
-keep public class net.geidea.paymentsdk.util.* {
    public protected*;
}
-keep public class net.geidea.paymentsdk.* {
    public protected*;
}

# Preserve all .class method names.

-keepclassmembernames class * {
    java.lang.Class class$(java.lang.String);
    java.lang.Class class$(java.lang.String, boolean);
}

# Preserve all native method names and the names of their classes.

-keepclasseswithmembernames class * {
    native <methods>;
}

# Explicitly preserve all serialization members. The Serializable interface
# is only a marker interface, so it wouldn't save them.
# You can comment this out if your library doesn't use serialization.
# If your code contains serializable classes that have to be backward
# compatible, please refer to the manual.

-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# Your library may contain more items that need to be preserved;
# typically classes that are dynamically created using Class.forName:

# -keep public class mypackage.MyClass
# -keep public interface mypackage.MyInterface
# -keep public class * implements mypackage.MyInterface

-dontwarn com.google.errorprone.annotations.Immutable
-dontwarn javax.annotation.concurrent.GuardedBy
-dontwarn com.google.android.material.R$attr

-keepnames class * implements android.os.Parcelable
-keepnames class * implements java.io.Serializable

-keep class * extends androidx.fragment.app.Fragment {}