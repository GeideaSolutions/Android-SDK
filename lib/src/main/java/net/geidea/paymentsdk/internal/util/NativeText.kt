package net.geidea.paymentsdk.internal.util

import android.content.Context
import android.widget.TextView
import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import com.google.android.material.textfield.TextInputLayout

internal interface NativeText {

    fun toCharSequence(context: Context): CharSequence

    data class Resource(@StringRes val resId: Int) : NativeText {
        override fun toCharSequence(context: Context): CharSequence = html(context.getString(resId))
    }

    data class Plain(val value: CharSequence) : NativeText {
        override fun toCharSequence(context: Context): CharSequence = html(value.toString())
    }

    data class Template(
            @StringRes val resId: Int,
            val args: List<Any>
    ) : NativeText {
        override fun toCharSequence(context: Context): CharSequence {
            val formattedArgs = args.toTypedArray().map {
                if (it is NativeText) {
                    it.toCharSequence(context)
                } else {
                    it
                }
            }

            return html(context.getString(resId, *formattedArgs.toTypedArray()))
        }

        companion object {
            @JvmStatic
            fun of(@StringRes resId: Int, vararg args: Any) = Template(resId, args.toList())
        }
    }

    data class Plural(
            @PluralsRes val resId: Int,
            val number: Int,
            val args: List<Any> = emptyList()
    ) : NativeText {
        override fun toCharSequence(context: Context): CharSequence =
            html(context.resources.getQuantityString(resId, number, *args.toTypedArray()))
    }

    object Empty : NativeText {
        override fun toCharSequence(context: Context): CharSequence = ""
    }
}

// The vast majority of strings are non-HTML but instead to detect which contain HTML we simply
// pretend all of them are HTML.
private fun html(source: String): CharSequence =
    HtmlCompat.fromHtml(source, HtmlCompat.FROM_HTML_MODE_LEGACY)

internal interface NativeTextFormatter {
    fun format(nativeText: NativeText): CharSequence
}

internal class NativeTextFormatterImpl(private val context: Context) : NativeTextFormatter {
    override fun format(nativeText: NativeText): CharSequence {
        return nativeText.toCharSequence(context)
    }
}

internal fun emptyText() = NativeText.Empty
internal fun resourceText(@StringRes resId: Int) = NativeText.Resource(resId)
internal fun plainText(text: CharSequence) = NativeText.Plain(text)
internal fun templateText(@StringRes resId: Int, vararg args: Any) = NativeText.Template(resId, args.toList())
internal fun pluralText(@PluralsRes resId: Int, number: Int, vararg args: Any) = NativeText.Plural(resId, number, args.toList())

// Extensions

// TODO: rename to applyText to avoid naming clash with platform method setText()
internal infix fun TextView.setText(nativeText: NativeText?) {
    this.text = nativeText?.toCharSequence(context)
}

// TODO: rename to applyText to avoid naming clash with platform method setError()
internal infix fun TextInputLayout.setError(nativeText: NativeText?) {
    this.error = nativeText?.toCharSequence(context)
}