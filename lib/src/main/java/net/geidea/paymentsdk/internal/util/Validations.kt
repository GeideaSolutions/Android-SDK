package net.geidea.paymentsdk.internal.util

import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.validation.ValidationStatus
import net.geidea.paymentsdk.ui.validation.card.reason.ExpiredCard
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidExpiryMonth
import net.geidea.paymentsdk.ui.validation.card.reason.InvalidExpiryYear
import net.geidea.paymentsdk.ui.widget.card.CardBrandFilter
import org.intellij.lang.annotations.Language
import java.lang.Integer.parseInt
import java.util.*

internal object Validations {

    // Card validation

    @JvmStatic
    internal fun validateCardHolder(cardHolder: String?, cardBrand: CardBrand): Boolean {
        return !cardHolder.isNullOrBlank() && cardHolder.length in 3..255
    }

    @JvmStatic
    internal fun validateSecurityCode(code: String?, brand: CardBrand?): Boolean {
        if (code == null || code.isBlank()) {
            return false
        }

        return try {
            Integer.parseInt(code)
            brand?.securityCodeLengthRange?.let { range -> code.length in range } ?: false
        } catch (e: NumberFormatException) {
            false
        }
    }

    @JvmStatic
    internal fun validateCardNumber(cardNumber: String?, filter: CardBrandFilter?): Boolean {
        if (cardNumber == null) {
            return false
        }
        val number: String = cardNumber.replace(" ", "")
        val brand = CardBrand.fromCardNumberPrefix(cardNumber)
        val brandAccepted = filter?.accept(brand) ?: true

        return number.isNotEmpty()
                && number.length >= CardBrand.MIN_LENGTH_STANDARD
                && luhnCheck(number)
                && brandAccepted
    }

    @JvmStatic
    internal fun luhnCheck(unformattedNumber: String?): Boolean {
        if (unformattedNumber == null || unformattedNumber.isBlank())
            return false

        val number = unformattedNumber.replace(" ", "")
        var s1 = 0
        var s2 = 0
        val reversed = number.reversed()

        for (i in reversed.indices) {
            val digit = Character.digit(reversed[i], 10)

            if (i % 2 == 0) {//this is for odd digits, they are 1-indexed in the algorithm
                s1 += digit
            } else {//add 2 * digit for 0-4, add 2 * digit - 9 for 5-9
                s2 += 2 * digit
                if (digit >= 5) {
                    s2 -= 9
                }
            }
        }
        return (s1 + s2) % 10 == 0
    }

    @JvmStatic
    internal fun validateEmail(email: String?): Boolean {
        return email != null && simpleVerifyEmail(email)
    }

    // Card expiry date

    @JvmStatic
    internal fun validateExpiryDate(expiryDateText: String?): ValidationStatus {
        if (expiryDateText == null) {
            return ValidationStatus.Invalid(InvalidExpiryYear)
        }
        val expiryNotSeparated = EXPIRY_SEPARATOR_REGEX.replace(expiryDateText, "")
        val parts = splitExpiryDate(expiryNotSeparated)
        return validateExpiryDate(parts[0], parts[1])
    }

    @JvmStatic
    internal fun parseExpiryMonthAndYear(month: String, year: String): Pair<Int, Int>? {
        val monthInt = try {
            parseInt(month)
        } catch (e: NumberFormatException) {
            return null
        }

        val yearInt = try {
            parseInt(year)
        } catch (e: NumberFormatException) {
            return null
        }

        return monthInt to yearInt
    }

    @JvmStatic
    internal fun validateExpiryDate(month: String, year: String): ValidationStatus {
        if (month.length != 2) {
            return ValidationStatus.Invalid(InvalidExpiryMonth)
        }
        val monthInt = try {
            parseInt(month)
        } catch (e: NumberFormatException) {
            return ValidationStatus.Invalid(InvalidExpiryMonth)
        }

        if (year.length != 2) {
            return ValidationStatus.Invalid(InvalidExpiryYear)
        }
        val yearInt = try {
            parseInt(year)
        } catch (e: NumberFormatException) {
            return ValidationStatus.Invalid(InvalidExpiryYear)
        }

        return validateExpiryDate(monthInt, yearInt)
    }

    @JvmStatic
    internal fun validateExpiryDate(month: Int, year: Int): ValidationStatus {
        if (month !in 1..12) {
            return ValidationStatus.Invalid(InvalidExpiryMonth)
        }

        if (year !in 1..99) {
            return ValidationStatus.Invalid(InvalidExpiryYear)
        }

        val fourDigitYear = 2000 + year
        val calendarNow = Calendar.getInstance()
        return if (fourDigitYear > calendarNow.get(Calendar.YEAR) ||
                fourDigitYear == calendarNow.get(Calendar.YEAR)
                && month >= calendarNow.get(Calendar.MONTH) + 1) {
            ValidationStatus.Valid
        } else {
            ValidationStatus.Invalid(ExpiredCard)
        }
    }

    @JvmStatic
    internal fun validateExpiryMonth(expiryMonth: String): Boolean {
        return try {
            expiryMonth.toInt() in 1..12
        } catch (e: NumberFormatException) {
            return false
        }
    }

    internal fun splitExpiryDate(expiryDateWithSeparator: String): Array<String> {
        val parts: Array<String> = arrayOf("", "")
        if (expiryDateWithSeparator.length >= 2) {
            parts[0] = expiryDateWithSeparator.substring(0, 2)
            parts[1] = expiryDateWithSeparator.substring(2)
        } else {
            parts[0] = expiryDateWithSeparator
            parts[1] = ""
        }
        return parts
    }

    @get:Language("regex")
    internal val emailRegex: Regex by lazy { Regex("""(([^]\[<>()\\.,;:\s@"]+(\.[^]\[<>()\\.,;:\s@"]+)*)|(".+"))@(([0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3})|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))""") }
    internal fun simpleVerifyEmail(email: String): Boolean = emailRegex.matches(email)

    @get:Language("regex")
    internal val httpsUrlRegex: Regex by lazy { Regex("""^https://(www\.)?[-a-zA-Z0-9@:%._+~#=]{1,256}\.[a-zA-Z0-9()]{1,6}([-a-zA-Z0-9()@:%_+.~#?&/=]*)${'$'}""") }
    internal fun validateHttpsUrl(url: String) = httpsUrlRegex.matches(url)

    internal val EXPIRY_SEPARATOR_REGEX = Regex("/")
}