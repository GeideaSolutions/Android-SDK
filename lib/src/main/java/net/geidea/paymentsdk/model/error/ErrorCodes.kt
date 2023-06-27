package net.geidea.paymentsdk.model.error

public object ErrorCodes {

    interface ErrorGroup {
        val code: String
    }

    public object CancelledInformationGroup : ErrorGroup {
        public override val code: String = "010"

        public const val CancelledByUser = "001"
    }

    public object BnplErrorGroup : ErrorGroup {
        public override val code = "720"

        public const val SouhoolaTechnicalFailure = "001"
        public const val CustomerNotEnrolledWithBnplProvider = "002"
        public const val CustomerNotEligibleForPaymentsWithBnplProvider = "003"
        public const val PurchaseAmountMoreThanCustomerLimit = "004"
        public const val MerchantCredentialsNotFound = "005"
        public const val DownPaymentLimitsViolated = "006"
        public const val FinancedAmountLimitsViolated = "007"
        public const val OtpNotFound = "008"
        public const val OtpExpired = "009"
        public const val InvalidOtp = "010"
        public const val OtpAlreadyVerified = "011"
        public const val MaximumInvalidOtpAttemptsConsumed = "012"
        public const val MerchantCredentialsNotConfiguredInTheGateway = "013"
        public const val MerchantNotAllowedToRedeemDiscount = "014"
        public const val PhoneNumberIsInvalid = "015"
        public const val OrderIsAlreadyRefunded = "016"
        public const val OrderAlreadyCanceled = "017"
        public const val OrderCannotBeCanceled = "018"
        public const val PurchaseDetailsDifferentFromInitialPurchaseRequest = "019"
        public const val DownPaymentNeedsToBePaidBeforeConfirm = "020"
        public const val AmountPaidUpfrontDifferFromRequired = "021"
        public const val SouhoolaTransactionFailed = "022"
        public const val DownPaymentOutOfRange = "023"
        public const val ToUAmountExceedsWalletBalance = "024"
        public const val CashbackAmountExceedsWalletBalance = "025"
        public const val TotalAmountLessThanRequiredMinimum = "026"
        public const val ItemsPriceSumNotEqualToTotalAmount = "027"
        public const val SumOfLoanAmountAndDownPaymentNotEqualToTotalAmount = "028"
    }
}