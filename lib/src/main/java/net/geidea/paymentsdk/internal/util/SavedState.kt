package net.geidea.paymentsdk.internal.util

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Source: https://github.com/wada811/ViewModel-SavedState-ktx
 */
internal class SavedStateProperty<T>(
    private val savedStateHandle: SavedStateHandle,
) : ReadWriteProperty<ViewModel, T> {
    @Suppress("RemoveExplicitTypeArguments", "UNCHECKED_CAST")
    override operator fun getValue(thisRef: ViewModel, property: KProperty<*>): T {
        return savedStateHandle.get<T>(property.name) as T
    }

    override operator fun setValue(thisRef: ViewModel, property: KProperty<*>, value: T) {
        savedStateHandle.set(property.name, value)
    }
}

internal inline fun <reified T> savedStateProperty(savedStateHandle: SavedStateHandle) = SavedStateProperty<T>(savedStateHandle)