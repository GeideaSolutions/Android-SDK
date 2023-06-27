package net.geidea.paymentsdk.ui.widget.address

import android.os.Bundle
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import net.geidea.paymentsdk.R


class AddressInputTestActivity : AppCompatActivity() {

    internal lateinit var addressInputView: AddressInputView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setTheme(R.style.Theme_MaterialComponents)
        addressInputView = AddressInputView(this)
        val linearLayout = LinearLayout(this)
        linearLayout.addView(addressInputView)
        setContentView(linearLayout)
    }

    internal val countryAutoCompleteTextView: CountryAutoCompleteTextView
        get() = addressInputView.findViewById(R.id.countryAutoCompleteTextView)

    internal val streetEditText: StreetEditText
        get() = addressInputView.findViewById(R.id.streetEditText)

    internal val cityEditText: CityEditText
        get() = addressInputView.findViewById(R.id.cityEditText)

    internal val postCodeEditText: PostCodeEditText
        get() = addressInputView.findViewById(R.id.postCodeEditText)
}