package net.geidea.paymentsdk.internal.ui.widget

import android.view.View
import android.view.ViewGroup
import androidx.annotation.IntRange
import androidx.annotation.Px
import androidx.core.view.ScrollingView
import androidx.core.view.isVisible
import androidx.core.view.updateMargins
import androidx.core.view.updatePadding
import androidx.core.widget.NestedScrollView
import net.geidea.paymentsdk.databinding.GdIncludeAppbarWithStepperBinding
import net.geidea.paymentsdk.internal.util.dp

internal fun GdIncludeAppbarWithStepperBinding.setup(
    step: Step?,
    underlayView: ScrollingView? = null,
    underlayDimension: Int = UNDERLAY,
) {
    if (step != null) {
        appBarLayout.isLiftOnScroll = true
        stepper.isVisible = true
        stepper.step = step
    } else {
        with (appBarLayout) {
            isLiftOnScroll = true
            background = null
            outlineProvider = null
        }
        stepper.isVisible = false
        underlayView?.underlapTop(underlayDimension)
    }
}

/**
 * Simple trick to simulate the behavior of "overlapTop" but not over but underneath the app bar.
 * Used to make underlayView visible underneath a small bottom portion of the rounded app bar.
 */
private fun ScrollingView.underlapTop(@Px @IntRange(from = 0) underlayHeight: Int) {
    if (this is NestedScrollView) {
        clipToPadding = underlayHeight == 0
    }
    with (this as View) {
        updatePadding(top = UNDERLAY)
        (layoutParams as ViewGroup.MarginLayoutParams).updateMargins(top = -UNDERLAY)
    }
}

private val UNDERLAY = 32.dp     // dp