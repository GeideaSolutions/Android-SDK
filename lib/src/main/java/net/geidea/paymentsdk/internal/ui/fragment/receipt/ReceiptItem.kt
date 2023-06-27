package net.geidea.paymentsdk.internal.ui.fragment.receipt

import android.graphics.Color
import android.graphics.Typeface
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.annotation.Dimension
import androidx.annotation.Dimension.DP
import androidx.annotation.DrawableRes
import androidx.annotation.GravityInt
import androidx.viewbinding.ViewBinding
import com.google.android.material.color.MaterialColors
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdReceiptItemImageBinding
import net.geidea.paymentsdk.databinding.GdReceiptItemPropertyBinding
import net.geidea.paymentsdk.databinding.GdReceiptItemTextBinding
import net.geidea.paymentsdk.internal.util.NativeText
import net.geidea.paymentsdk.internal.util.dp
import net.geidea.paymentsdk.internal.util.setText

internal sealed interface ReceiptItem {

    fun inflate(layoutInflater: LayoutInflater): View

    data class Property(
        val label: NativeText,
        val value: NativeText,
        val labelTextSize: Float = -1f,
        val labelTextStyle: Int = Typeface.NORMAL,
        val valueTextSize: Float = -1f,
        val valueTextStyle: Int = Typeface.NORMAL,
    ) : ReceiptItem {

        override fun inflate(layoutInflater: LayoutInflater): View {
            return bind(GdReceiptItemPropertyBinding.inflate(layoutInflater)) {
                root.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0f
                )
                    .apply {
                        bottomMargin = 16.dp
                    }

                propertyLabelTextView setText label
                if (labelTextSize > 0) {
                    propertyLabelTextView.textSize = labelTextSize
                }
                if (labelTextStyle != -1) {
                    propertyLabelTextView.setTypeface(propertyLabelTextView.typeface, labelTextStyle)
                }

                propertyValueTextView setText value
                if (valueTextSize > 0) {
                    propertyValueTextView.textSize = valueTextSize
                }
                if (valueTextStyle != -1) {
                    propertyValueTextView.setTypeface(propertyValueTextView.typeface, valueTextStyle)
                }
            }
        }
    }

    data class Spacer(
        @Dimension(unit = DP) val height: Int = 32.dp
    ) : ReceiptItem {

        override fun inflate(layoutInflater: LayoutInflater): View {
            return View(layoutInflater.context).apply {
                id = View.generateViewId()
                tag = this
                layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height, 0f)
                        .apply { bottomMargin = 16.dp }
            }
        }
    }

    data class Divider(
        @Dimension(unit = DP) val verticalMargin: Int = 24.dp
    ) : ReceiptItem {

        override fun inflate(layoutInflater: LayoutInflater): View {
            return View(layoutInflater.context).apply {
                id = View.generateViewId()
                tag = this
                layoutParams =
                    LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 1.dp, 0f).apply {
                        topMargin = verticalMargin
                        bottomMargin = verticalMargin
                    }
                setBackgroundColor(
                    MaterialColors.getColor(
                        context,
                        com.google.android.material.R.attr.colorOnSurface,
                        Color.GRAY
                    )
                )
                alpha = 0.2f
            }
        }
    }

    data class Text(
        val text: NativeText,
        val textSize: Float = -1f,
        val textStyle: Int = Typeface.NORMAL,
        @GravityInt val textGravity: Int = Gravity.CENTER_HORIZONTAL,
    ) : ReceiptItem {

        override fun inflate(layoutInflater: LayoutInflater): View {
            return bind(GdReceiptItemTextBinding.inflate(layoutInflater)) {
                root.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0f
                )
                    .apply {
                        bottomMargin = 16.dp
                    }

                textView setText text
                textView.gravity = textGravity
                if (textSize > 0f) {
                    textView.textSize = textSize
                }
                if (textStyle != -1) {
                    textView.setTypeface(textView.typeface, textStyle)
                }
            }
        }
    }

    data class Image(@DrawableRes val image: Int) : ReceiptItem {

        override fun inflate(layoutInflater: LayoutInflater): View {
            return bind(GdReceiptItemImageBinding.inflate(layoutInflater)) {
                root.layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT, 0f
                )
                    .apply {
                        bottomMargin = 16.dp
                    }

                imageView.setImageResource(image)
            }
        }
    }
}

private fun <VB : ViewBinding, RI : ReceiptItem> RI.bind(
    viewBinding: VB,
    block: VB.() -> Unit
): View {
    return with(viewBinding) {
        root.id = View.generateViewId()
        root.tag = this
        block()

        root
    }
}