# kotlinx-serialization

-keepattributes *Annotation*, InnerClasses
-dontnote kotlinx.serialization.AnnotationsKt # core serialization annotations

# kotlinx-serialization-json specific. Add this if you have java.lang.NoClassDefFoundError kotlinx.serialization.json.JsonObjectSerializer
#-keepclassmembers class kotlinx.serialization.json.** {
#    *** Companion;
#}
-keepclasseswithmembers class kotlinx.serialization.json.** {
    kotlinx.serialization.KSerializer serializer(...);
}

-keep,includedescriptorclasses class net.geidea.paymentsdk.**$$serializer { *; }
-keepclassmembers class net.geidea.paymentsdk.** {
    *** Companion;
}
-keepclasseswithmembers class net.geidea.paymentsdk.** {
    kotlinx.serialization.KSerializer serializer(...);
}

# Classes sent as SafeArgs
-keep class net.geidea.paymentsdk.flow.pay.PaymentData
-keep class net.geidea.paymentsdk.internal.ui.widget.Step
-keep class net.geidea.paymentsdk.internal.ui.fragment.receipt.ReceiptArgs
-keep class net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor