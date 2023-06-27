package net.geidea.paymentsdk.ui.widget.address

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.verify
import net.geidea.paymentsdk.model.Country
import net.geidea.paymentsdk.ui.validation.OnInvalidStatusListener
import net.geidea.paymentsdk.ui.validation.OnValidStatusListener
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.address.validator.DefaultCountryValidator
import net.geidea.paymentsdk.ui.validation.dummyReason
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CountryAutoCompleteTextViewTest {

    @MockK
    internal lateinit var mockValidator: DefaultCountryValidator

    @RelaxedMockK
    internal lateinit var mockOnInvalidStatusListener: OnInvalidStatusListener<Country?>

    @RelaxedMockK
    internal lateinit var mockOnValidStatusListener: OnValidStatusListener<Country?>

    @RelaxedMockK
    internal lateinit var mockCountryChangedListener: OnCountryChangedListener

    private lateinit var countryAutoCompleteTextView: CountryAutoCompleteTextView

    @Before
    fun setup() {
        MockKAnnotations.init(this)

        with(mockValidator) {
            every { validate(eq("")) } returns ValidationStatus.Valid
            every { validate(eq("supported")) } returns ValidationStatus.Valid
            every { validate(eq("unsupported")) } returns ValidationStatus.Invalid(dummyReason)
            every { validate(eq("missingInAdapter")) } returns ValidationStatus.Invalid(dummyReason)
        }

        val activityController = Robolectric
                .buildActivity(AddressInputTestActivity::class.java)
                .create()
                .start()

        countryAutoCompleteTextView = activityController.get().countryAutoCompleteTextView
        countryAutoCompleteTextView.setCountryValidator(mockValidator)
        countryAutoCompleteTextView.setOnValidStatusListener(mockOnValidStatusListener)
        countryAutoCompleteTextView.setOnInvalidStatusListener(mockOnInvalidStatusListener)
    }

    @Test
    fun getText_byDefault_isEmpty() {
        assertEquals("", countryAutoCompleteTextView.text?.toString())
    }

    @Test
    fun isValid_byDefault_isTrue() {
        countryAutoCompleteTextView.setText("")
        assertTrue(countryAutoCompleteTextView.isValid)
    }

    @Test
    fun setText_withEmptyText_isValidTrue() {
        countryAutoCompleteTextView.setText("")
        assertTrue(countryAutoCompleteTextView.isValid)
    }

    @Test
    fun setText_withEmptyText_callsListenerWithValidTrue() {
        countryAutoCompleteTextView.setText("supported")
        countryAutoCompleteTextView.setOnValidStatusListener(mockOnValidStatusListener)
        countryAutoCompleteTextView.setText("")
        verify { mockOnValidStatusListener.onValidStatus(isNull()) }
    }

    @Test
    fun setCountry_withNull_isValidTrue() {
        countryAutoCompleteTextView.country = supportedCountry
        countryAutoCompleteTextView.country = null
        assertTrue(countryAutoCompleteTextView.isValid)
    }

    @Test
    fun setCountry_withSupportedCountry_callsOnCountryChanged() {
        countryAutoCompleteTextView.setAdapter(DummyCountryAdapter(supportedCountry))
        countryAutoCompleteTextView.setOnCountryChangedListener(mockCountryChangedListener)

        countryAutoCompleteTextView.country = supportedCountry

        verify { mockCountryChangedListener.onCountryChanged(eq(supportedCountry)) }
    }

    @Test
    fun setCountry_withSupportedCountryNotContainedInAdapter_callsOnCountryChanged() {
        countryAutoCompleteTextView.setAdapter(DummyCountryAdapter())   // empty adapter
        countryAutoCompleteTextView.setOnCountryChangedListener(mockCountryChangedListener)

        countryAutoCompleteTextView.country = missingCountry

        // Supported country that is NOT in the adapter, it will still change but will be invalid
        verify { mockCountryChangedListener.onCountryChanged(eq(missingCountry)) }
    }

    @Test(expected = IllegalArgumentException::class)
    fun setAdapter_withUnsupportedCountry_throws() {
        countryAutoCompleteTextView.setAdapter(DummyCountryAdapter(unsupportedCountry))
    }

    companion object {
        val supportedCountry = Country {
            key2 = "BG";
            key3 = "BGR";
            numericCode = 123
            nameEn = "supported"
            nameAr = ""
            isSupported = true
        }

        val unsupportedCountry = Country {
            key2 = "US";
            key3 = "USA";
            numericCode = 124
            nameEn = "unsupported"
            nameAr = ""
            isSupported = false
        }

        val missingCountry = Country {
            key2 = "FR";
            key3 = "FRA";
            numericCode = 124
            nameEn = "missingInAdapter"
            nameAr = ""
            isSupported = true
        }
    }
}