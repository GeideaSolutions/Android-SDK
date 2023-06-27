package net.geidea.paymentsdk.internal.ui.widget

import android.content.Context
import android.text.Editable
import android.util.AttributeSet
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Checkable
import android.widget.RadioButton
import androidx.annotation.DrawableRes
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdItemPaymentMethodMeezaBinding
import net.geidea.paymentsdk.internal.ui.fragment.bnpl.BnplEgyptPhoneValidator
import net.geidea.paymentsdk.internal.ui.fragment.options.PaymentOptionsFragment
import net.geidea.paymentsdk.internal.util.setImageWithAspectRatio
import net.geidea.paymentsdk.internal.util.setupIndeterminateProgressOn
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor
import net.geidea.paymentsdk.ui.widget.TextInputErrorListener
import net.geidea.paymentsdk.ui.widget.TextWatcherAdapter
import net.geidea.paymentsdk.ui.widget.phone.PhoneNumberEditText

/**
 * Compound [Checkable] and [Collapsible] view with a radio button and a [PhoneNumberEditText]
 * shown to customer for selecting [Meeza QR][PaymentMethodDescriptor.MeezaQr] as payment method
 * in the [Payment Options][PaymentOptionsFragment] screen.
 */
@GeideaSdkInternal
internal class MeezaQrOptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : CheckableLinearLayout(context, attrs), Checkable, Collapsible {

    private var phoneNumberChangedListener: OnPhoneNumberChangedListener? = null
    private var imeActionListener: OnImeActionListener? = null

    private val binding: GdItemPaymentMethodMeezaBinding

    init {
        val materialContext = MaterialThemeOverlay.wrap(context, attrs, 0, 0)
        inflate(materialContext, R.layout.gd_item_payment_method_meeza, this)

        binding = GdItemPaymentMethodMeezaBinding.bind(this)
        with(binding) {
            root.background =
                AppCompatResources.getDrawable(context, R.drawable.gd_selector_outline_rounded2)

            orientation = VERTICAL
            layoutDirection = LAYOUT_DIRECTION_INHERIT
            // android:paddingHorizontal="8dp"

            radioButton.id = View.generateViewId()
            with(phoneNumberEditText) {
                setOnErrorListener(TextInputErrorListener(phoneNumberInputLayout))
                setValidator(BnplEgyptPhoneValidator)
                setOnValidStatusListener { phoneNumber ->
                    phoneNumberChangedListener?.invoke(phoneNumber, true)
                }
                setOnInvalidStatusListener { phoneNumber, _ ->
                    phoneNumberChangedListener?.invoke(phoneNumber, false)
                }
                addTextChangedListener(object : TextWatcherAdapter() {
                    override fun afterTextChanged(s: Editable) {
                        if (s.toString().length == BnplEgyptPhoneValidator.MAX_LENGTH) {
                            phoneNumberEditText.updateErrorMessage()
                        }
                    }
                })
                setOnEditorActionListener { _, actionId, keyEvent ->
                    if (actionId == EditorInfo.IME_ACTION_NEXT && imeActionListener != null) {
                        imeActionListener?.invoke()
                        true
                    } else
                        false
                }
            }

            setupIndeterminateProgressOn(phoneNumberInputLayout)
            phoneNumberInputLayout.isEndIconVisible = false
        }
    }

    val isValid: Boolean get() = binding.phoneNumberEditText.isValid

    val radioButton: RadioButton get() = binding.radioButton

    var label: CharSequence?
        get() = binding.radioButton.text
        set(newValue) {
            binding.radioButton.text = newValue
        }

    fun setLogo(@DrawableRes logo: Int) {
        binding.logoImageView.setImageWithAspectRatio(logo)
    }

    var isPhoneInputEnabled: Boolean
        get() = binding.phoneNumberInputLayout.isEnabled
        set(newValue) {
            binding.phoneNumberInputLayout.isEnabled = newValue
        }

    var isProgressVisible: Boolean
        get() = binding.phoneNumberInputLayout.isEndIconVisible
        set(newValue) {
            binding.phoneNumberInputLayout.isEndIconVisible = newValue
        }

    override var isExpanded: Boolean
        get() = binding.phoneNumberInputLayout.isVisible
        set(newValue) {
            binding.phoneNumberInputLayout.isVisible = newValue
            if (!newValue) {
                binding.phoneNumberEditText.clearFocus()
            }
        }

    /**
     * Validate the phone number and show an error below if invalid. Has no effect if valid.
     */
    fun validatePhoneNumber() {
        binding.phoneNumberEditText.updateErrorMessage()
    }

    val phoneNumber get() = binding.phoneNumberEditText.text?.toString() ?: ""

    fun setOnValidChangeListener(listener: OnPhoneNumberChangedListener?) {
        phoneNumberChangedListener = listener
    }

    fun setOnImeActionListener(listener: OnImeActionListener) {
        imeActionListener = listener
    }
}

internal typealias OnPhoneNumberChangedListener = (phoneNumber: String?, isValid: Boolean) -> Unit
internal typealias OnImeActionListener = () -> Unit