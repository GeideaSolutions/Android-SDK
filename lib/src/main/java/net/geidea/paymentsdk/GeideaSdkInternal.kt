package net.geidea.paymentsdk

/**
 * Denotes a kotlin internal which is publicly visible for Java consumers but is not part
 * of the public API and should not be used by the SDK consumers.
 */
@Target(
        AnnotationTarget.CLASS,
        AnnotationTarget.CONSTRUCTOR,
        AnnotationTarget.FUNCTION,
        AnnotationTarget.PROPERTY,
        AnnotationTarget.PROPERTY_GETTER,
        AnnotationTarget.PROPERTY_SETTER,
        AnnotationTarget.TYPEALIAS
)
@Retention
annotation class GeideaSdkInternal