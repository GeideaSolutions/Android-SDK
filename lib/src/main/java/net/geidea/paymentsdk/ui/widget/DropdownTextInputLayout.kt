package net.geidea.paymentsdk.ui.widget

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputLayout
import net.geidea.paymentsdk.R

/**
 * [TextInputLayout] intended to have an exposed dropdown menu. Uses
 * [R.attr.gd_textInputExposedDropdownMenuStyle] as a default style attribute. It should refer
 * to a style inheriting Widget.MaterialComponents.TextInputLayout.*.ExposedDropdownMenu.
 */
open class DropdownTextInputLayout @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = R.attr.gd_textInputExposedDropdownMenuStyle
) : TextInputLayout(context, attrs, defStyleAttr)