package net.geidea.paymentsdk.internal.ui.widget

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.databinding.GdItemPaymentMethodGroupBinding
import net.geidea.paymentsdk.internal.util.dp

@GeideaSdkInternal
internal class CollapsibleGroupLinearLayout

@JvmOverloads
constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : LinearLayout(context, attrs, defStyleAttr, defStyleRes), Collapsible {

    private val binding: GdItemPaymentMethodGroupBinding

    init {
        orientation = VERTICAL
        binding = GdItemPaymentMethodGroupBinding.inflate(LayoutInflater.from(context), this, true)
    }

    var labelText: CharSequence?
        get() = binding.groupLabelTextView.text
        set(newValue) {
            binding.groupLabelTextView.text = newValue
        }

    fun setOnClickHeaderListener(onClick: (View) -> Unit) {
        binding.headerLinearLayout.setOnClickListener(onClick)
    }

    fun addLogo(@DrawableRes logo: Int) {
        val imageView = AspectRatioImageView(context).apply {
            setImageResource(logo)
            layoutParams = LayoutParams(
                40.dp, //ViewGroup.LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT, 0f
            ).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = 4.dp
                marginEnd = 4.dp
            }
        }
        binding.headerLinearLayout.addView(imageView)
    }

    override var isExpanded: Boolean
        get() = binding.contentLinearLayout.isVisible
        set(newValue) {
            binding.contentLinearLayout.isVisible = newValue
            binding.expandCollapseButton.rotation = if (newValue) 180f else 0f
        }

    fun addItemView(itemView: View) {
        binding.contentLinearLayout.addView(itemView)
    }
}