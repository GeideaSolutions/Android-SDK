package net.geidea.paymentsdk.internal.ui.fragment.bnpl.souhoola.otp

import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.emptyText
import net.geidea.paymentsdk.internal.util.resourceText

@GeideaSdkInternal
internal data class SouhoolaOtpState(
    val otpHelpTextVisible: Boolean,
    val otpInputEnabled: Boolean,
    val otpInputVisible: Boolean,
    val progressVisible: Boolean,
    val purchaseButtonEnabled: Boolean,
    val purchaseButtonText: NativeText,
    val purchaseButtonProgressVisible: Boolean,
    val errorText: NativeText,
    val errorTextInvisible: Boolean,
    val timeRemainingVisible: Boolean,
    val codesLeftVisible: Boolean,
    val resendCodeButtonEnabled: Boolean,
)

internal val Initial = SouhoolaOtpState(
    otpHelpTextVisible = false,
    otpInputEnabled = false,
    otpInputVisible = true,
    progressVisible = false,
    purchaseButtonEnabled = false,
    purchaseButtonText = resourceText(R.string.gd_bnpl_finalize_purchase),
    purchaseButtonProgressVisible = false,
    errorText = emptyText(),
    errorTextInvisible = true,
    timeRemainingVisible = false,
    codesLeftVisible = false,
    resendCodeButtonEnabled = false,
)

// Mutators

internal fun SouhoolaOtpState.otpGenerating() = copy(
    otpInputEnabled = false,
    progressVisible = true,
    purchaseButtonEnabled = false,
)

internal fun SouhoolaOtpState.otpGenerated() = copy(
    progressVisible = false,
    otpHelpTextVisible = true,
    otpInputEnabled = true,
    timeRemainingVisible = true,
    codesLeftVisible = true
)

internal fun SouhoolaOtpState.otpConfirming() = copy(
    purchaseButtonEnabled = false,
    purchaseButtonText = NativeText.Empty,
    purchaseButtonProgressVisible = true,
    errorTextInvisible = true,
    errorText = emptyText(),
)

internal fun SouhoolaOtpState.withError(errorText: NativeText) = copy(
    otpInputEnabled = true,
    purchaseButtonText = resourceText(R.string.gd_bnpl_finalize_purchase),
    purchaseButtonEnabled = true,
    purchaseButtonProgressVisible = false,
    errorTextInvisible = false,
    errorText = errorText,
)

internal fun SouhoolaOtpState.withResendButtonEnabled() = copy(
    resendCodeButtonEnabled = true
)