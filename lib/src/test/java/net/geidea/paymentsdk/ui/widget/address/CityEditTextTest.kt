package net.geidea.paymentsdk.ui.widget.address

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.geidea.paymentsdk.ui.validation.Validator
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CityEditTextTest {

    @MockK(relaxed = true)
    internal lateinit var mockValidator: Validator<String>

    private lateinit var cityEditText: CityEditText

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
                .buildActivity(AddressInputTestActivity::class.java)
                .create()
                .start()
        cityEditText = activityController.get().cityEditText
        cityEditText.setValidator(mockValidator)
    }

    @Test
    fun setText_callsValidate() {
        cityEditText.setText("city")
        verify { mockValidator.validate(eq("city")) }
    }
}