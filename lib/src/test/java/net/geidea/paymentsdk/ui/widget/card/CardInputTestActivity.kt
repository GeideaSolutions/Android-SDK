package net.geidea.paymentsdk.ui.widget.card

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.ui.widget.FormEditText


class CardInputTestActivity : AppCompatActivity() {

    internal lateinit var cardInputView: CardInputView

    internal lateinit var formEditText: FormEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MaterialComponents)
        cardInputView = CardInputView(this)
        val linearLayout = LinearLayout(this)
        linearLayout.orientation = LinearLayout.VERTICAL
        linearLayout.addView(cardInputView)
        formEditText = FormEditText(this)
        linearLayout.addView(formEditText)
        setContentView(linearLayout)
    }

    internal val cardNumberEditText: CardNumberEditText
        get() = cardInputView.findViewById(R.id.cardNumberEditText)

    internal val cardHolderEditText: CardHolderEditText
        get() = cardInputView.findViewById(R.id.cardHolderEditText)

    internal val cardExpiryDateEditText: CardExpiryDateEditText
        get() = cardInputView.findViewById(R.id.cardExpiryDateEditText)

    internal val cardSecurityCodeEditText: CardSecurityCodeEditText
        get() = cardInputView.findViewById(R.id.cardSecurityCodeEditText)
}