# Module Geidea Payment SDK for Android

Geidea Payment SDK for Android provides tools for quick and easy
integration of Geidea Payment Gateway services into your Android app.

## Requirements
- Android 6.0+
- Java 8 or Kotlin

## How to start

### Gradle setup

The SDK is available on GitHub Packages Maven repository.

1. Add GitHub Packages as a Maven repository to your project-level build.gradle
```groovy
allprojects {
    maven {
        url = uri("https://maven.pkg.github.com/GeideaSolutions/Android-SDK")
        credentials {
            username = project.findProperty("gpr.user")
            password = project.findProperty("gpr.key")
        }
    }
}
```

2. Define your GitHub username (`gpr.user`) and GitHub Personal Access
   Token (`gpr.key`) in your `gradle.properties` file (without the
   pointy brackets):
```
gpr.user=<YOUR GITHUB USSERNAME>
gpr.key=<YOUR GITHUB PERSONAL ACCCESS TOKEN>
```
You can see your Personal Access Token (PAT) in your
GitHub Profile > Settings > Developer Options > Personal access tokens.
If you do not have one yet, you can generate it as explained [here](https://docs.github.com/en/authentication/keeping-your-account-and-data-secure/creating-a-personal-access-token).

3. Add the SDK as a dependency in your app-level build.gradle

```groovy
implementation 'net.geidea.paymentsdk:paymentsdk:<LATEST VERSION>'
```

### SDK Initialization

As an initialization step the SDK expects that you provide your Merchant
credentials with the `GeideaPaymentAPI.setCredentials()` method. However
it is not required to set them on each start. It could be once per
installation of the app as the credentials are persisted securely
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
IMPORTANT: Do **not** hard-code your merchant password directly into
your APK but get it dynamically (from an endpoint of your backend or
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

The Payment flow expects an input of type `PaymentData` and returns a
result of type `GeideaResult<Order>`. The `PaymentContract` is used to
manage the input/output parcelization.

Declare a launcher somewhere in your code from where you wish to start
the payment.

```kotlin
private var paymentLauncher: ActivityResultLauncher<PaymentData>
```

…and then to register it with a `PaymentContract` instance and your
function or lambda that should accept the final result.

```kotlin
fun handleOrderResult(result: GeideaResult<Order>) {
    /** Handle the order response here */
}
paymentLauncher = registerForActivityResult(PaymentContract(), ::handleOrderResult)
```

### Building your PaymentData

`PaymentData` contains details about the order, customer and preferred
payment method. It has a few mandatory properties - `amount`, `currency`
and `paymentMethod`.

Kotlin

```kotlin
val paymentData = PaymentData {
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
PaymentData paymentData = new PaymentData.Builder()
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
`PaymentData` and `PaymentMethod`. E.g. check if the CVV is 3 or 4
digits. If some validation check does not pass then an
`IllegalArgumentException` with a message is thrown. For a comprehensive
list of validity conditions please refer to the Integration guide. The
full validation is performed server-side and `FieldValidationError` is
returned on bad input.


### Starting payment flow

After registering for results a payment flow can be started
```kotlin
paymentLauncher.launch(paymentData)
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
