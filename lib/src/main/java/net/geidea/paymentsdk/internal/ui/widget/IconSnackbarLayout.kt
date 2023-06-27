package net.geidea.paymentsdk.internal.ui.widget

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.AlphaAnimation
import android.view.animation.DecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorRes
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.material.button.MaterialButton
import com.google.android.material.snackbar.ContentViewCallback
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.internal.util.dp

internal class IconSnackbarLayout @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr), ContentViewCallback {

    val iconImageView: ImageView
    val titleTextView: TextView
    val messageTextView: TextView
    val referenceTextView: TextView
    val actionButton: MaterialButton

    init {
        View.inflate(context, R.layout.gd_view_icon_snackbar, this)
        iconImageView = findViewById(R.id.iconImageView)
        titleTextView = findViewById(R.id.titleTextView)
        messageTextView = findViewById(R.id.messageTextView)
        referenceTextView = findViewById(R.id.referenceTextView)
        actionButton = findViewById(R.id.actionButton)
    }

    fun setOutlineColor(@ColorRes colorRes: Int) {
        val shape = background as GradientDrawable?
        if (shape != null && colorRes != 0) {
            shape.setStroke(1.5.dp, AppCompatResources.getColorStateList(context, colorRes))
        }
    }

    override fun animateContentIn(delay: Int, duration: Int) {
        AlphaAnimation(0F, 1F).apply {
            interpolator = DecelerateInterpolator()
            setDuration(500)
        }.start()
    }

    override fun animateContentOut(delay: Int, duration: Int) {
        AlphaAnimation(1F, 0F).apply {
            interpolator = AccelerateInterpolator()
            setDuration(500)
        }.start()
    }
}