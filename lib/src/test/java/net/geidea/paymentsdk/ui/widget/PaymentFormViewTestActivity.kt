package net.geidea.paymentsdk.ui.widget

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.widget.address.AddressInputView
import net.geidea.paymentsdk.ui.widget.card.CardInputView
import net.geidea.paymentsdk.ui.widget.email.EmailEditText


class PaymentFormViewTestActivity : AppCompatActivity() {

    internal lateinit var paymentFormView: PaymentFormView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Gd_Theme_DayNight_NoActionBar)
        paymentFormView = PaymentFormView(this)
        val linearLayout = LinearLayout(this)
        linearLayout.addView(paymentFormView)
        setContentView(linearLayout)
    }

    internal val cardInputView: CardInputView
        get() = paymentFormView.findViewById(R.id.cardInputView)

    internal val customerEmailInputLayout: TextInputLayout
        get() = paymentFormView.findViewById(R.id.customerEmailInputLayout)

    internal val customerEmailEditText: EmailEditText
        get() = paymentFormView.findViewById(R.id.customerEmailEditText)

    internal val billingAddressLabel: TextView
        get() = paymentFormView.findViewById(R.id.billingAddressLabel)

    internal val billingAddressInputView: AddressInputView
        get() = paymentFormView.findViewById(R.id.billingAddressInputView)

    internal val sameAddressCheckBox: CheckBox
        get() = paymentFormView.findViewById(R.id.sameAddressCheckbox)

    internal val shippingAddressLabel: TextView
        get() = paymentFormView.findViewById(R.id.shippingAddressLabel)

    internal val shippingAddressInputView: AddressInputView
        get() = paymentFormView.findViewById(R.id.shippingAddressInputView)

    internal val payButton: Button
        get() = paymentFormView.findViewById(R.id.payButton)

    internal val cancelButton: Button
        get() = paymentFormView.findViewById(R.id.cancelButton)
}