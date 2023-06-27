package net.geidea.paymentsdk.internal.ui.fragment.base

import androidx.annotation.IdRes
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import net.geidea.paymentsdk.GeideaSdkInternal

@GeideaSdkInternal
internal sealed interface NavigationCommand {

    data class ToDirection(
            val directions: NavDirections,
            val navOptions: NavOptions? = null
    ) : NavigationCommand

    object Back : NavigationCommand

    data class BackTo(
            @IdRes val destinationId: Int,
            val inclusive: Boolean = false
    ) : NavigationCommand

    data class BackToWithResult<T>(
        @IdRes val destinationId: Int,
        val key: String,
        val value: T
    ) : NavigationCommand

    /**
     * Optionally show the Receipt screen or immediately finishes with the current result.
     */
    object Cancel : NavigationCommand

    /**
     * Immediately finish with the current result.
     */
    object Finish : NavigationCommand
}