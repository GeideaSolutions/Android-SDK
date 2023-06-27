package net.geidea.paymentsdk.ui.validation

import android.text.InputFilter
import android.text.Spanned

/**
 * Input filter that applies a predicate [filterFunc] on each
 * character of the source [CharSequence].
 */
open class SimpleCharFilter(private val filterFunc: (c: Char) -> Boolean) : InputFilter {

    override fun filter(
            source: CharSequence?,
            start: Int,
            end: Int,
            dest: Spanned?,
            dstart: Int,
            dend: Int
    ): CharSequence? {

        if (source != null) {
            val builder = StringBuilder()
            (start until end).forEach { i ->
                val c = source[i]
                if (filterFunc(c)) {
                    builder.append(c)
                }
            }

            // If all characters are valid, return null, otherwise only return the filtered characters
            val allCharactersValid = builder.length == end - start

            return if (allCharactersValid) null else builder.toString()
        } else {
            return null
        }
    }
}