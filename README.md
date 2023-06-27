# Geidea Online Payments for Android Mobile Apps

 - Contributors: kanti-kiran 
 - Version: 4.0.2 
 - Keywords: credit card, geidea, payment, payment for mobile sdk, payment for android, payment request, android
 - License: GPLv3
 - License URI: https://www.gnu.org/licenses/gpl-3.0.html

Add the Geidea online payments SDK to your mobile app store with Low Code and start accepting online payments seamlessly with Geidea Payment Gateway. Geidea Online Payments SDK for Android provides you with the required tools for quick and easy integration of Geidea Payment Gateway services into your Android app.

## Requirements
- Android 6.0+
- Java 8 or Kotlin

## How to start

### Gradle setup

The SDK is available on GitHub Packages Maven repository.

1. The SDK is available for download in a zipped file format in this repository.
```
Download the zip file in the home directory of your local machine
/Users/{username}
```

 2. Extract the SDK from the downloaded zip file with the below command:
```
unzip -o geidea.zip
```
3. Proceed to update your build.gradle files to access the local maven repository.
buildscript {
```
repositories {
	// ...
        mavenLocal()
    }
}
allprojects {
    repositories {
        // ...
        mavenLocal()
    }
}
```
4. Add the extracted SDK as a dependency in your app-level build.gradle
```
implementation 'net.geidea.paymentsdk:paymentsdk:<LATEST VERSION>'
```

### SDK Initialization

The integration starts by adding your merchant credentials (Merchant Public Key and API password) with the `GeideaPaymentSdk.setCredentials()`method.  You can check if there credentials already stored
with the `GeideaPaymentSdk.hasCredentials`. It is only important to be
stored prior to using the SDK.
```kotlin```
```
if (!GeideaPaymentSdk.hasCredentials) {
    GeideaPaymentSdk.setCredentials(
            merchantKey = "<YOUR MERCHANT KEY>",
            merchantPassword = "<YOUR MERCHANT PASSWORD>"
    )
}
```
IMPORTANT: 
1. The ```setCredentials()``` method can be used to store your credentials securely on the device by using encryption. So it is not required to set them on each app start event. You could set them once per installation of the app on the device.
2. As a good security and coding practice, do **not** hard-code your merchant password directly into your APK file. Always get it securely and dynamically (from the secure endpoint of your backend or some secure server) where the password has been stored with encryption.

## The Flow concept

The SDK employs the concept of UI flow which is a sequence of UI screens, network calls and other operations that are orchestrated in a logical manner. UI flows are implemented based on the typical Android Activity results where one activity (or a chain of more activities) is launched with an input intent, then it performs its task(s) and finally produces some output which contains the result data you are interested in. Each flow is represented and managed by an `ActivityContract` implementation.

### Using Activity result contracts

The latest version of Anroid platform now supports Activity Result APIs that offer a better experience instead of the traditional and now deprecated `startActivityForResult()` method. For more info please visit [https://developer.android.com/training/basics/intents/result]() .

### Payment flow

The Payment flow expects an input of type `PaymentData` and returns a result of type `GeideaResult<Order>`. The `PaymentContract` is used to manage the input/output parcelization.

Declare a launcher in your code from where you wish to start the payment.
```kotlin```
```
private var paymentLauncher: ActivityResultLauncher<PaymentData>
```
After declaring the launcher, register it with a `PaymentContract` instance and your
function or lambda that should accept the final result.
```kotlin
fun handleOrderResult(result: GeideaResult<Order>) {
    /** Handle the order response here */
}
paymentLauncher = registerForActivityResult(PaymentContract(), ::handleOrderResult)
```
### Building your PaymentData

`PaymentData` contains details about the order, customer and preferred payment method. It has 2 mandatory properties - `amount` and `currency`.
```kotlin```
```
val paymentData = PaymentData {
    // Mandatory properties
    amount = 123.45
    currency = "SAR"
    // Optional properties
    paymentMethod = PaymentMethod {
        cardHolderName = "John Doe"
        cardNumber = "5123450000000008"
        expiryDate = ExpiryDate(month = 1, year = 25)
        cvv = "123"
    }
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
```Java```
```
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
The SDK carries out some basic validation checks on construction of the `PaymentData` and `PaymentMethod` objects. For example, whether the CVV is 3 or 4 digits. If any of the validation check does not pass then an `IllegalArgumentException` with a message is thrown. 
For a comprehensive list of validity conditions please refer to the Integration guide.

### Starting payment flow
After registering for results a payment flow can be started
```kotlin```
```
paymentLauncher.launch(paymentData)
```
### Receiving the Order result
The final result of the Payment flow is returned as a sealed object of type `GeideaResult<Order>`.

```kotlin```
```
fun handleOrderResult(result: GeideaResult<Order>) {
    when (result) {
        is GeideaResult.Success<Order> -> {
            // Payment successful, order returned in result.data
        }
        is GeideaResult.Error -> {
            when (result) {
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
