package net.geidea.paymentsdk.internal.ui.widget

import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDialogFragment
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.fragment.base.Snack
import net.geidea.paymentsdk.internal.ui.widget.IconSnackbar.Companion.make
import net.geidea.paymentsdk.internal.util.findFirstChildRecursive

internal class IconSnackbar(
        parent: ViewGroup,
        content: IconSnackbarLayout
) : BaseTransientBottomBar<IconSnackbar>(parent, content, content) {

    private var hasAction = false

    /**
     * Update the icon in this [IconSnackbar].
     *
     * @param iconResId The new icon resource id for this [IconSnackbar].
     */
    fun setIcon(@DrawableRes iconResId: Int): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val imageView = contentLayout.iconImageView
        imageView.isVisible = iconResId != 0
        imageView.setImageResource(iconResId)
        return this
    }

    /**
     * Update the icon in this [IconSnackbar].
     *
     * @param drawable Drawable of the new icon for this [IconSnackbar].
     */
    fun setIcon(drawable: Drawable?): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val imageView = contentLayout.iconImageView
        imageView.isVisible = drawable != null
        imageView.setImageDrawable(drawable)
        return this
    }

    /**
     * Update the title in this [IconSnackbar].
     *
     * @param title The new title for this [IconSnackbar].
     */
    fun setTitle(title: CharSequence?): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val titleTextView = contentLayout.titleTextView
        titleTextView.isVisible = title != null
        titleTextView.text = title
        return this
    }

    /**
     * Update the message text in this [IconSnackbar].
     *
     * @param message The new message text for this [IconSnackbar].
     */
    fun setMessage(message: CharSequence?): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val messageTextView = contentLayout.messageTextView
        messageTextView.isVisible = message != null
        messageTextView.text = message
        return this
    }

    /**
     * Update the reference text in this [IconSnackbar].
     *
     * @param reference The new reference text for this [IconSnackbar].
     */
    fun setReference(reference: CharSequence?): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val referenceTextView = contentLayout.referenceTextView
        referenceTextView.isVisible = reference != null
        referenceTextView.text = reference
        return this
    }

    /**
     * Set the action to be displayed in this [IconSnackbar].
     *
     * @param textResId text resource to display for the action
     * @param iconResId drawable resource to use as an action icon
     * @param listener callback to be invoked when the action is clicked
     */
    @JvmOverloads
    fun setAction(
            @StringRes textResId: Int,
            @DrawableRes iconResId: Int = 0,
            listener: View.OnClickListener? = null
    ): IconSnackbar {
        val text = if (textResId != 0) context.getText(textResId) else null
        val icon = if (iconResId != 0) ContextCompat.getDrawable(context, iconResId) else null
        return setAction(text, icon, listener)
    }

    /**
     * Set the action to be displayed in this [IconSnackbar].
     *
     * @param text text to display for the action
     * @param icon drawable resource to use as an action icon
     * @param listener callback to be invoked when the action is clicked
     */
    private fun setAction(
            text: CharSequence?,
            icon: Drawable?,
            listener: View.OnClickListener?): IconSnackbar {
        val contentLayout = view.getChildAt(0) as IconSnackbarLayout
        val textView: TextView = contentLayout.actionButton
        val isEmptyText = TextUtils.isEmpty(text)
        if (isEmptyText && icon == null) {
            // No text, no icon => no button
            textView.visibility = View.GONE
            textView.setOnClickListener(null)
            hasAction = false
        } else {
            hasAction = true
            textView.visibility = View.VISIBLE

            textView.setOnClickListener { view ->
                listener?.onClick(view)
                // Now dismiss the IconSnackbar
                dispatchDismiss(BaseCallback.DISMISS_EVENT_ACTION)
            }

            if (!isEmptyText) {
                textView.text = text
            }

            if (textView is MaterialButton) {
                textView.icon = icon
            }
        }

        return this
    }

    companion object {
        @JvmStatic
        internal fun make(
                view: View,
                title: CharSequence? = null,
                message: CharSequence? = null,
                reference: CharSequence? = null,
                @DrawableRes iconResId: Int = 0,
                @ColorRes outlineColorResId: Int = 0,
                duration: Int = Snackbar.LENGTH_SHORT
        ): IconSnackbar {
            val parent = findSuitableParent(view)
                    ?: throw IllegalArgumentException(
                            "No suitable parent found from the given view. Please provide a valid view.")

            val customView = LayoutInflater.from(parent.context).inflate(
                    R.layout.gd_layout_icon_snackbar,
                    parent,
                    false
            ) as IconSnackbarLayout

            return IconSnackbar(parent, customView)
                    .setTitle(title)
                    .setMessage(message)
                    .setReference(reference)
                    .setIcon(iconResId)
                    .setDuration(duration)
                    .apply {
                        (customView.parent as ViewGroup).background = null
                        customView.setOutlineColor(outlineColorResId)
                    }
        }
    }
}

internal fun AppCompatActivity.snackbar(coordinatorLayout: CoordinatorLayout, snack: Snack): IconSnackbar = make(
    view = coordinatorLayout,
    title = snack.title?.toCharSequence(this),
    message = snack.message?.toCharSequence(this),
    reference = snack.reference?.toCharSequence(this),
    iconResId = snack.iconResId,
    outlineColorResId = snack.outlineColorResId,
    duration = snack.duration,
).apply { show() }

internal fun BaseFragment<*>.snackbar(coordinatorLayout: CoordinatorLayout, snack: Snack): IconSnackbar = make(
    view = coordinatorLayout,
    title = snack.title?.toCharSequence(requireContext()),
    message = snack.message?.toCharSequence(requireContext()),
    reference = snack.reference?.toCharSequence(requireContext()),
    iconResId = snack.iconResId,
    outlineColorResId = snack.outlineColorResId,
    duration = snack.duration,
).apply { show() }

internal fun AppCompatDialogFragment.snackbar(snack: Snack): IconSnackbar = make(
    view = requireActivity().window.findViewById(android.R.id.content),
    title = snack.title?.toCharSequence(requireContext()),
    message = snack.message?.toCharSequence(requireContext()),
    reference = snack.reference?.toCharSequence(requireContext()),
    iconResId = snack.iconResId,
    outlineColorResId = snack.outlineColorResId,
    duration = snack.duration,
).apply { show() }

private fun BaseFragment<*>.findCoordinatorLayout(): CoordinatorLayout {
    return findCoordinatorLayout(requireView())
}

private fun findCoordinatorLayout(root: View): CoordinatorLayout {
    return if (root is CoordinatorLayout)
        root
    else
        requireNotNull(root.findFirstChildRecursive())
}

private fun findSuitableParent(view: View): ViewGroup? {
    var view: View? = view
    var fallback: ViewGroup? = null
    do {
        if (view is CoordinatorLayout) {
            // We've found a CoordinatorLayout, use it
            return view
        } else if (view is FrameLayout) {
            fallback = if (view.getId() == android.R.id.content) {
                // If we've hit the decor content view, then we didn't find a CoL in the
                // hierarchy, so use it.
                return view
            } else {
                // It's not the content view but we'll use it as our fallback
                view
            }
        }
        if (view != null) {
            // Else, we will loop and crawl up the view hierarchy and try to find a parent
            val parent = view.parent
            view = if (parent is View) parent else null
        }
    } while (view != null)

    // If we reach here then we didn't find a CoL or a suitable content view so we'll fallback
    return fallback
}