package net.geidea.paymentsdk.ui.widget

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import net.geidea.paymentsdk.ui.validation.Validator
import net.geidea.paymentsdk.ui.widget.email.EmailEditText
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EmailEditTextTest {

    @MockK(relaxed = true)
    internal lateinit var mockValidator: Validator<String>

    private lateinit var emailEditText: EmailEditText

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        val activityController = Robolectric
                .buildActivity(PaymentFormViewTestActivity::class.java)
                .create()
                .start()

        emailEditText = activityController.get().customerEmailEditText
        emailEditText.setValidator(mockValidator)
    }

    @Test
    fun setText_callsValidate() {
        emailEditText.setText("email")
        verify { mockValidator.validate(eq("email")) }
    }
}