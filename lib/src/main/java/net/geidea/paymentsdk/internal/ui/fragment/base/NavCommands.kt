package net.geidea.paymentsdk.internal.ui.fragment.base

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import net.geidea.paymentsdk.GeideaSdkInternal

@GeideaSdkInternal
internal interface NavCommands {
    fun navigate(navDirections: NavDirections, navOptions: NavOptions? = null)
    fun navigate(navCommand: NavigationCommand)
    fun navigateBack()
    fun navigateBackTo(@IdRes destination: Int, inclusive: Boolean = false)
    fun navigateCancel()
    fun navigateFinish()
    fun <T> navigateBackToWithResult(@IdRes destination: Int, key: String, value: T)
}