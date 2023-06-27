package net.geidea.paymentsdk.flow.pay

import android.app.Activity
import android.app.Instrumentation
import com.google.common.truth.Truth
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.order.Order
import org.junit.Assert

internal fun assertSuccessOrderResult(activityResult: Instrumentation.ActivityResult) {
    Truth.assertThat(activityResult.resultCode).isEqualTo(Activity.RESULT_OK)
    Truth.assertThat(activityResult.resultData).isNotNull()
    Truth.assertThat(activityResult.resultData.extras).isNotNull()

    activityResult.resultData.setExtrasClassLoader(Order::class.java.classLoader)
    val result: GeideaResult<Order>? = activityResult.resultData.getParcelableExtra(PaymentActivity.EXTRA_RESULT)
    Assert.assertNotNull(result)
    Assert.assertTrue(result is GeideaResult.Success)
    val orderStatus: String? = (result as GeideaResult.Success).data.status
    Assert.assertEquals("Success", orderStatus)
}