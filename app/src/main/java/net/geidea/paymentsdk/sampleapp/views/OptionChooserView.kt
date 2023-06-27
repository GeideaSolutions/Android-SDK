package net.geidea.paymentsdk.sampleapp.views

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import net.geidea.paymentsdk.sampleapp.R

/**
 * Created by nama on 23,May,2023
 */
class OptionChooserView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    private val iconImageView: ImageView
    private val labelTextView: TextView


    init {
        LayoutInflater.from(context).inflate(R.layout.view_geidea_chooser_template, this, true)
        iconImageView = findViewById(R.id.icon_image_view)
        labelTextView = findViewById(R.id.label_text_view)
        if(attrs != null){
            val a = context.obtainStyledAttributes(attrs, R.styleable.MyLayout)
            var drawable = a.getDrawable(R.styleable.MyLayout_chooserIcon)
            if (drawable != null){
                iconImageView.setImageDrawable(drawable)
            }

            var title = a.getString(R.styleable.MyLayout_chooserLabel)
            labelTextView.text = title
        }

    }

    fun setIcon(icon: Drawable) {
        iconImageView.setImageDrawable(icon)
    }

    fun setLabel(label: String) {
        labelTextView.text = label
    }

}