package net.geidea.paymentsdk.internal.ui.widget

import android.content.Context
import android.os.Parcelable
import android.util.AttributeSet
import android.util.LayoutDirection
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.StringRes
import androidx.core.text.layoutDirection
import androidx.core.view.isVisible
import androidx.core.view.updatePaddingRelative
import com.google.android.material.theme.overlay.MaterialThemeOverlay
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdViewStepperBinding
import net.geidea.paymentsdk.internal.util.dp
import java.util.*


internal class Stepper @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null
) : LinearLayout(MaterialThemeOverlay.wrap(context, attrs, 0, 0), attrs) {

    private var binding: GdViewStepperBinding

    var isBackButtonVisible: Boolean
        get() = binding.backImageButton.isVisible
        set(value) {
            binding.backImageButton.isVisible = value
            val startPadding = if (value) 0 else 16.dp
            binding.textView.updatePaddingRelative(start = startPadding)
        }

    var text: CharSequence
        get() = binding.textView.text
        set(value) { binding.textView.text = value }

    var currentStep: Int = 1
        set(value) {
            if (field != value) {
                field = value
                binding.progressTextView.text = formatText(value, stepCount)
                progress = calculateProgress(currentStep, value).toInt()
            }
        }

    var stepCount: Int = 1
        set(value) {
            if (field != value) {
                field = value
                binding.progressTextView.text = formatText(currentStep, value)
                progress = calculateProgress(currentStep, value).toInt()
            }
        }

    var step: Step
        get() = Step(current = currentStep, stepCount = stepCount, text = text)
        set(value) {
            isBackButtonVisible = value.isBackButtonVisible
            currentStep = value.current
            stepCount = value.stepCount
            val newText = value.text ?: value.textResId.takeIf { it != 0 }?.let(context::getString)
            if (newText != null) {
                text = newText
            }
        }

    var progress: Int
        get() = binding.circularProgressIndicator.progress
        set(value) { binding.circularProgressIndicator.progress = value }

    private fun formatText(currentStep: Int, stepCount: Int): CharSequence {
        return if (Locale.getDefault().layoutDirection == LayoutDirection.RTL)
            "$currentStep \\ $stepCount"
        else
            "$currentStep / $stepCount"
    }

    init {
        val materialContext = MaterialThemeOverlay.wrap(context, attrs, 0, 0)
        inflate(materialContext, R.layout.gd_view_stepper, this)

        binding = GdViewStepperBinding.bind(this)

        val a = materialContext.obtainStyledAttributes(attrs, R.styleable.Stepper, 0, 0)

        text = a.getString(R.styleable.Stepper_gd_text) ?: ""

        val currentStep: Int = a.getInt(R.styleable.Stepper_gd_currentStep, 1)
        require(currentStep > 0)
        this.currentStep = currentStep

        val stepCount: Int = a.getInt(R.styleable.Stepper_gd_stepCount, 1)
        require(stepCount > 0)
        this.stepCount =  stepCount

        this.progress = calculateProgress(currentStep, stepCount).toInt()

        a.recycle()
    }

    private fun calculateProgress(currentStep: Int, stepCount: Int): Float {
        return (currentStep.toFloat() / stepCount.toFloat()) * 100f
    }

    fun setOnBackClickListener(listener: ((View) -> Unit)?) {
        binding.backImageButton.setOnClickListener(listener)
    }
}

@GeideaSdkInternal
@Parcelize
data class Step(
        val isBackButtonVisible: Boolean = false,
        val current: Int,
        val stepCount: Int,
        val text: CharSequence? = null,
        @StringRes val textResId: Int = 0,
) : Parcelable