package net.geidea.paymentsdk.internal.ui.widget

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.Checkable
import android.widget.LinearLayout

/**
 * https://gist.github.com/christopherperry/3746480
 */
internal open class CheckableLinearLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), Checkable {
    private var mChecked = false

    constructor(context: Context?) : this(context, null) {
        init()
    }

    private fun init() {
//     setClickable(true);
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return onTouchEvent(ev)
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()
            setCheckedRecursive(this, checked)
        }
    }

    private fun setCheckedRecursive(parent: ViewGroup, checked: Boolean) {
        val count = parent.childCount
        for (i in 0 until count) {
            val v = parent.getChildAt(i)
            if (v is Checkable) {
                (v as Checkable).isChecked = checked
            }
            if (v is ViewGroup) {
                setCheckedRecursive(v, checked)
            }
        }
    }

    // Drawable States
    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(drawableState, CHECKED_STATE_SET)
        }
        return drawableState
    }

    override fun drawableStateChanged() {
        super.drawableStateChanged()
        val drawable = background
        if (drawable != null) {
            val myDrawableState = drawableState
            drawable.state = myDrawableState
            invalidate()
        }
    }

    // State persistence
    internal class SavedState : BaseSavedState {
        var checked = false

        constructor(superState: Parcelable?) : super(superState) {}
        private constructor(`in`: Parcel) : super(`in`) {
            checked = (`in`.readValue(null) as Boolean?)!!
        }

        override fun writeToParcel(out: Parcel, flags: Int) {
            super.writeToParcel(out, flags)
            out.writeValue(checked)
        }

        override fun toString(): String {
            return ("CheckableLinearLayout.SavedState{"
                    + Integer.toHexString(System.identityHashCode(this))
                    + " checked=" + checked + "}")
        }

        companion object {
            @JvmField
            val CREATOR: Parcelable.Creator<SavedState> = object : Parcelable.Creator<SavedState> {
                override fun createFromParcel(`in`: Parcel): SavedState {
                    return SavedState(`in`)
                }

                override fun newArray(size: Int): Array<SavedState?> {
                    return arrayOfNulls(size)
                }
            }
        }
    }

    public override fun onSaveInstanceState(): Parcelable? {
        // Force our ancestor class to save its state
        val superState = super.onSaveInstanceState()
        val ss = SavedState(superState)
        ss.checked = isChecked
        return ss
    }

    public override fun onRestoreInstanceState(state: Parcelable) {
        val ss = state as SavedState
        super.onRestoreInstanceState(ss.superState)
        isChecked = ss.checked
        requestLayout()
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(
                android.R.attr.state_checked
        )
    }

    init {
        init()
    }
}