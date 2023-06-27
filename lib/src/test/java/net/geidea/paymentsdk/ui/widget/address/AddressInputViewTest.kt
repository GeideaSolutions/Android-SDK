package net.geidea.paymentsdk.ui.widget.address

import com.google.android.material.textfield.TextInputLayout
import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.model.Address
import net.geidea.paymentsdk.model.Country
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AddressInputViewTest {

    private lateinit var addressInputView: AddressInputView
    private lateinit var childViews: ChildViews

    @MockK(relaxUnitFun = true)
    internal lateinit var mockAddressListener: AddressInputListener

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
                .buildActivity(AddressInputTestActivity::class.java)
                .create()
                .start()
                .resume()
        addressInputView = activityController.get().addressInputView
        childViews = ChildViews(addressInputView)
    }

    @Test
    fun setAddress_withValid_fieldsAreFilled() {
        addressInputView.setCountryDropDownAdapter(DummyCountryAdapter(supportedCountry1, supportedCountry2))
        addressInputView.address = validAddress

        assertEquals("Saudi Arabia", childViews.countryAutoCompleteTextView.text?.toString())
        assertEquals("street", childViews.streetEditText.text?.toString())
        assertEquals("city", childViews.cityEditText.text?.toString())
        assertEquals("1234", childViews.postCodeEditText.text?.toString())
    }

    @Test
    fun setAddress_withAllNulls_fieldsAreEmpty() {
        addressInputView.setCountryDropDownAdapter(DummyCountryAdapter(supportedCountry1, supportedCountry2))
        addressInputView.address = Address(null, null, null, null)

        assertEquals("", childViews.countryAutoCompleteTextView.text?.toString())
        assertEquals("", childViews.streetEditText.text?.toString())
        assertEquals("", childViews.cityEditText.text?.toString())
        assertEquals("", childViews.postCodeEditText.text?.toString())
    }

    @Test(expected = IllegalArgumentException::class)
    fun setCountryCode_withInvalidCountryCode() {
        addressInputView.setCountryDropDownAdapter(DummyCountryAdapter(supportedCountry1, supportedCountry2))
        addressInputView.countryCode = "BGR"    // Valid code but not in the adapter
        assertFalse(addressInputView.isValid)
        assertNull(addressInputView.countryCode)
        // No error because no country with this code in the adapter
        assertNull(childViews.countryInputLayout.error)
    }

    @Test
    fun setStreet_withInvalid() {
        addressInputView.street = tooLong
        assertFalse(addressInputView.isValid)
        assertEquals(tooLong, addressInputView.street)
        assertNotNull(childViews.streetInputLayout.error)
    }

    @Test
    fun setCity_withInvalid() {
        addressInputView.city = tooLong
        assertFalse(addressInputView.isValid)
        assertEquals(tooLong, addressInputView.city)
        assertNotNull(childViews.cityInputLayout.error)
    }

    @Test
    fun setPostCode_withInvalid() {
        addressInputView.postCode = tooLong
        assertFalse(addressInputView.isValid)
        assertEquals(tooLong, addressInputView.postCode)
        assertNotNull(childViews.postCodeInputLayout.error)
    }

    private class ChildViews(parent: AddressInputView) {
        val countryInputLayout: TextInputLayout = parent.findViewById(R.id.countryInputLayout)
        val streetInputLayout: TextInputLayout = parent.findViewById(R.id.streetInputLayout)
        val cityInputLayout: TextInputLayout = parent.findViewById(R.id.cityInputLayout)
        val postCodeInputLayout: TextInputLayout = parent.findViewById(R.id.postCodeInputLayout)

        val countryAutoCompleteTextView: CountryAutoCompleteTextView = parent.findViewById(R.id.countryAutoCompleteTextView)
        val streetEditText: StreetEditText = parent.findViewById(R.id.streetEditText)
        val cityEditText: CityEditText = parent.findViewById(R.id.cityEditText)
        val postCodeEditText: PostCodeEditText = parent.findViewById(R.id.postCodeEditText)
    }

    companion object {
        val supportedCountry1 = Country {
            key2 = "SA";
            key3 = "SAU";
            numericCode = 123
            nameEn = "Saudi Arabia"
            nameAr = ""
            isSupported = true
        }

        val supportedCountry2 = Country {
            key2 = "US";
            key3 = "USA";
            numericCode = 124
            nameEn = "USA"
            nameAr = ""
            isSupported = true
        }

        val validAddress = Address(
            countryCode = "SAU",
            street = "street",
            city = "city",
            postCode = "1234"
        )

        val tooLong = "a".repeat(255+1)
    }
}