package net.geidea.paymentsdk.internal.ui.fragment.base

import androidx.annotation.IdRes
import androidx.lifecycle.ViewModel
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.util.Event
import net.geidea.paymentsdk.internal.util.LiveEvent
import net.geidea.paymentsdk.internal.util.MutableLiveEvent

@GeideaSdkInternal
internal abstract class BaseViewModel : ViewModel(), NavCommands {

    private val _snackbarLiveEvent = MutableLiveEvent<Snack>()
    val snackbarLiveEvent: LiveEvent<Snack> = _snackbarLiveEvent

    private val _snackbarDismissLiveEvent = MutableLiveEvent<Unit>()
    val snackbarDismissLiveEvent: LiveEvent<Unit> = _snackbarDismissLiveEvent

    private val _navigationLiveEvent = MutableLiveEvent<NavigationCommand>()
    val navigationLiveEvent: LiveEvent<NavigationCommand> = _navigationLiveEvent

    override fun navigate(navDirections: NavDirections, navOptions: NavOptions?) {
        navigate(NavigationCommand.ToDirection(navDirections, navOptions))
    }

    override fun navigate(navCommand: NavigationCommand) {
        _navigationLiveEvent.value = Event(navCommand)
    }

    override fun navigateBack() {
        navigate(NavigationCommand.Back)
    }

    override fun navigateBackTo(@IdRes destination: Int, inclusive: Boolean) {
        navigate(NavigationCommand.BackTo(destination, inclusive))
    }

    override fun <T> navigateBackToWithResult(@IdRes destination: Int, key: String, value: T) {
        navigate(NavigationCommand.BackToWithResult(destination, key, value))
    }

    override fun navigateCancel() {
        navigate(NavigationCommand.Cancel)
    }

    override fun navigateFinish() {
        navigate(NavigationCommand.Finish)
    }

    fun showSnack(snack: Snack) {
        _snackbarLiveEvent.value = Event(snack)
    }

    fun dismissSnack() {
        _snackbarDismissLiveEvent.value = Event(Unit)
    }
}