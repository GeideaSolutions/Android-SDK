package net.geidea.paymentsdk.util

import androidx.lifecycle.LiveData
import com.jraska.livedata.TestObserver
import com.jraska.livedata.test

fun <T> LiveData<T>.assertEmitted(value: T): TestObserver<T> {
    return test()
            .awaitValue()
            .assertHasValue()
            .assertValue(value)
}

fun <T> LiveData<T>.assertEmittedThat(valuePredicate: (T) -> Boolean): TestObserver<T> {
    return test()
            .awaitValue()
            .assertHasValue()
            .assertValue(valuePredicate)
}