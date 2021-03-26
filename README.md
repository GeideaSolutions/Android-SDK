# Module geidea-paymentsdk-android

Geidea Payment SDK for Android provides tools for quick and easy
integration of Geidea Payment Gateway services into your Android app.

## Requirements
- Android 6.0+
- Java 8 or Kotlin

## How to start

### Gradle setup

The SDK is distributed as an archive containing Maven AAR artifact  
and POM file. The easiest way to import it into your project is to:

1. Unpack the SDK archive with the AAR and POM files into
   some local folder (may not necessarily be under your project directory)
2. Add it as a local Maven repository to your project-level build.gradle
```groovy
allprojects {
    repositories {
        maven { url "<LOCAL PATH TO THE EXTRACTED AAR AND POM>" }
    }
}
```
E.g. if you extract the SDK package into <PROJECT_DIR>\geideasdk then
the path to the AAR should be <PROJECT_DIR>\geideasdk\net\geidea\paymentsdk\paymentsdk\1.0.0\paymentsdk-1.0.0.aar

3. Add the SDK as a dependency in your app-level build.gradle

```groovy
implementation 'net.geidea.paymentsdk:paymentsdk:<LATEST VERSION>'
```

Now Geidea Payment SDK should be imported in your project.

### SDK Initialization

As an initialization step the SDK expects that you provide your Merchant
credentials with the `GeideaPaymentAPI.setCredentials()` method.
However it is not required to set them on each start. It could be once  
per installation of the app as the credentials are persisted securely  
encrypted on device. You can check if there credentials already stored  
with the `GeideaPaymentAPI.hasCredentials`. It is only important to be  
stored prior to using the SDK.
```kotlin
if (!GeideaPaymentAPI.hasCredentials) {
    GeideaPaymentAPI.setCredentials(
            merchantKey = "<YOUR MERCHANT KEY>",
            merchantPassword = "<YOUR MERCHANT PASSWORD>"
    )
}
```
IMPORTANT: Do **not** hard-code your merchant password directly into your  
APK but get it dynamically (from an endpoint of your backend or  
elsewhere) due to security reasons.

## The Flow concept

The SDK employs the concept of UI flow which is a sequence of UI  
screens, network calls and various other operations. UI flows are  
implemented based on the typical Android Activity results where one  
activity (or a chain of more activities) is launched with an input  
intent, then it performs its work and finally produces some output which  
contains the result data you are interested in. Each flow is represented  
and managed by an `ActivityContract` implementation.

### Using Activity result contracts

Instead of relying on the traditional and now deprecated  
`startActivityForResult()` method the SDK embraces the newer Activity  
Result APIs which offer some benefits for you. For more info please  
visit [https://developer.android.com/training/basics/intents/result]() .

### Payment flow

The Payment flow expects an input of type `PaymentIntent` and returns a  
result of type `GeideaResult<Order>`. The `PaymentContract` is used to  
manage the input/output parcelization.

Declare a launcher somewhere in your code from where you wish to start  
the payment.

```kotlin
private var paymentLauncher: ActivityResultLauncher<PaymentIntent>
```

…and then to register it with a `PaymentContract` instance and your  
function or lambda that should accept the final result.

```kotlin
fun handleOrderResult(result: GeideaResult<Order>) {
    /** Handle the order response here */
}
paymentLauncher = registerForActivityResult(PaymentContract(), ::handleOrderResult)
```

### Building your PaymentIntent

`PaymentIntent` contains details about the order, customer and preferred  
payment method. It has a few mandatory properties - `amount`, `currency`  
and `paymentMethod`.

Kotlin

```kotlin
val paymentIntent = PaymentIntent {
    // Mandatory properties
    amount = 123.45
    currency = "SAR"
    paymentMethod = PaymentMethod {
        cardHolderName = "John Doe"
        cardNumber = "5123450000000008"
        expiryDate = ExpiryDate(month = 1, year = 25)
        cvv = "123"
    }
    // Optional properties
    callbackUrl = "https://website.hook/"
    merchantReferenceId = "1234"
    customerEmail = "email@noreply.test"
    billingAddress = Address(
            countryCode = "SAU",
            city = "Riyadh",
            street = "Street 1",
            postCode = "1000"
    )
    shippingAddress = Address(
            countryCode = "SAU",
            city = "Riyadh",
            street = "Street 1",
            postCode = "1000"
    )
}
```

or in Java

```java
PaymentIntent paymentIntent = new PaymentIntent.Builder()
        .setAmount(123.45d)
        .setCurrency("SAR")
        .setPaymentMethod(new PaymentMethod.Builder()
                .setCardHolderName("John Doe")
                .setCardNumber("5123450000000008")
                .setExpiryDate(new ExpiryDate(1, 25))
                .setCvv("123")
                .build()
        )
        .setCallbackUrl("https://website.hook/")
        .setMerchantReferenceId("1234")
        .setCustomerEmail("email@noreply.test")
        .setBillingAddress(new Address(
                "SAU",
                "Riyadh",
                "Street 1",
                "1000"
        ))
        .setShippingAddress(new Address(
                "SAU",
                "Riyadh",
                "Street 1",
                "1000"
        ))
        .build();
```

Validations

Multiple basic validation checks are performed on construction of  
`PaymentIntent` and `PaymentMethod`. E.g. check if the CVV is 3 or 4 digits.  
If some validation check does not pass then an `IllegalArgumentException` with a  
message is thrown. For a comprehensive list of validity conditions  
please refer to the Integration guide.
The full validation is performed server-side and `FieldValidationError`  
is returned on bad input.


### Starting payment flow

After registering for results a payment flow can be started
```kotlin
paymentLauncher.launch(paymentIntent)
```

### Receiving the Order result

The final result of the Payment flow is returned as a sealed object of  
type `GeideaResult<Order>`.

```kotlin
fun handleOrderResult(result: GeideaResult<Order>) {
    when (result) {
        is GeideaResult.Success<Order> -> {
            // Payment successful, order returned in result.data
        }
        is GeideaResult.Error -> {
            when (result) {
                is GeideaResult.FieldValidationError -> {
                    // Client error - invalid field values (e.g. a CVV with letters)
                    handleFieldValidationError(
                            result.type,
                            result.title,
                            result.status,
                            result.traceId,
                            result.errors,
                    )
                }
                is GeideaResult.NetworkError -> {
                    // Client or server error
                    handleNetworkError(
                        result.responseCode,
                        result.responseMessage,
                        result.detailedResponseCode,
                        result.detailedResponseMessage,
                    )
                }
                is GeideaResult.SdkError -> {
                    // An unexpected error due to improper SDK
                    // integration or SDK internal bug
                    handleSdkError(result.errorCode, result.errorMessage)
                }
            }
        }
        is GeideaResult.Cancelled -> {
            // Payment flow cancelled by the user (e.g. Back button)
            Toast.makeText(this, "Payment cancelled by the user", 
                           Toast.LENGTH_LONG).show()
        }
    }
}
```
