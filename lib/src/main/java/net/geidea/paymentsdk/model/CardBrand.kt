package net.geidea.paymentsdk.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.R
import java.util.*

/**
 * Class describing the formatting, validation and visualization specifics of different card brands.
 * Each sub-class represents a card brand.
 */
sealed class CardBrand : Parcelable {

    /**
     * The maximal length of the card number including the spaces.
     */
    open val maxLength: Int = 19

    /**
     * Positions of space characters for separating the card number into groups of digits. Used for better readability.
     */
    open val gapPositions: Set<Int> = setOf(4, 9, 14)

    /**
     * An array of known starting digits for this card brand.
     *
     * Source:
     * http://en.wikipedia.org/wiki/Bank_card_number#Issuer_identification_number
     */
    open val prefixes: List<String> = emptyList()

    /**
     * The acceptable length range of the security code (same as verification code, CVC, CVV,
     * CVV2, etc.) of the card. The common length is normally 3, only for American Express card
     * it is 4 digits long.
     */
    open val securityCodeLengthRange: IntRange = 3..3

    /**
     * Short name used to identify the card brand.
     */
    abstract val name: String

    /**
     * String resource id of the card brand name.
     */
    @get:StringRes
    abstract val displayName: Int

    /**
     * A small icon of the brand logo.
     */
    @get:DrawableRes
    abstract val logo: Int

    /**
     * Checks if the given card number length is valid for the card brand.
     *
     * @param lengthNoSpaces length of a card number without the digit group separators.
     */
    abstract fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean

    /**
     * Checks if a given full or partial card number is known to be of this brand.
     *
     * @param cardNumber the card number to match
     * @return true if cardNumber matches this brand
     */
    fun matches(cardNumber: String): Boolean = prefixes.any { cardNumber.startsWith(it) }

    /**
     * Formats a card number so that it consists of groups of digits separated with a space.
     * The separation is card brand-specific and has the same style as in the physical card.
     *
     * IMPORTANT: Does not perform any validity checks.
     *
     * @param cardNumberNoSpaces the card number string without any spaces
     * @return separated card number
     */
    open fun separateCardNumber(cardNumberNoSpaces: String): String {
        val number = if (cardNumberNoSpaces.length > 16)
            cardNumberNoSpaces.substring(0, 16)
        else
            cardNumberNoSpaces

        val separatedBuilder = StringBuilder(number)
        val separator = ' '

        // Insert separators from end to start

        // All other cards except AmEx
        // Expected: 4111 1111 1111 1111
        if (number.length > 12) {
            separatedBuilder.insert(12, separator)
        }
        if (number.length > 8) {
            separatedBuilder.insert(8, separator)
        }
        if (number.length > 4) {
            separatedBuilder.insert(4, separator)
        }

        return separatedBuilder.toString()
    }

    override fun toString(): String = name

    /**
     * Represents an unknown / unsupported card brand.
     */
    @Parcelize
    object Unknown : CardBrand() {
        override val name = ""
        override val displayName: Int = R.string.gd_card_brand_unknown
        override val logo: Int = R.drawable.gd_ic_card_unknown

        override val securityCodeLengthRange: IntRange = 3..4

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return true     // We cannot know the valid lengths
        }
    }

    /**
     * Mada card
     */
    @Parcelize
    object Mada : CardBrand() {
        override val name: String = "mada"
        override val displayName: Int = R.string.gd_card_brand_mada
        override val logo: Int = R.drawable.gd_ic_card_mada
        override val prefixes = listOf(
                "400861",
                "409201",
                "410685",
                "417633",
                "428331",

                "428671", "428672", "428673",

                "431361",
                "432328",
                "440533",
                "440647",
                "440795",
                "445564",
                "446404",
                "446672",
                "455036",
                "457865",
                "458456",
                "462220",

                "468540", "468541", "468542", "468543",

                "484783",
                "489317",
                "489319",
                "490980",
                "493428",
                "504300",
                "508160",
                "521076",
                "527016",
                "539931",
                "557606",
                "558848",
                "585265",

                "588845", "588846", "588847", "588848", "588849", "588850", "588851",

                "588982", "588983",

                "589005",
                "589206",
                "604906",
                "605141",
                "636120",

                "422817", "422818", "422819",

                "9682",

                "439954",
                "439956",
                "419593",

                "483010", "483010", "483012",

                "532013",
                "531095",
                "530906",
                "455708",
                "524514",
                "529741",
                "537767",
                "535989",

                "486094", "486095", "486096",

                "543357",
                "401757",
                "446393",
                "434107",
                "536023",
                "407197",
                "407395",
                "529415",
                "535825",
                "543085",
                "549760",
                "437980"
        )

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return lengthNoSpaces == 16
        }
    }

    /**
     * Meeza card
     */
    @Parcelize
    object  Meeza : CardBrand() {
        override val name: String = "meeza"
        override val displayName: Int = R.string.gd_card_brand_meeza
        override val logo: Int = R.drawable.gd_ic_card_meeza
        override val prefixes = listOf(
                "507803",
                "507808"
        )

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return lengthNoSpaces == 16
        }
    }

    /**
     * VISA card
     */
    @Parcelize
    object Visa : CardBrand() {
        override val name: String = "visa"
        override val displayName: Int = R.string.gd_card_brand_visa
        override val logo: Int = R.drawable.gd_ic_card_visa
        override val prefixes = listOf(
                "4",
                "501779",
                "575241"
        )

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return lengthNoSpaces == 16
        }
    }

    /**
     * American Express card
     */
    @Parcelize
    object AmericanExpress : CardBrand() {
        override val name: String = "amex"
        override val maxLength: Int = 17
        override val gapPositions = setOf(4, 11)
        override val securityCodeLengthRange: IntRange get() = 4..4
        override val displayName: Int = R.string.gd_card_brand_amex
        override val logo: Int = R.drawable.gd_ic_card_amex
        override val prefixes = listOf(
                "34",
                "37",
                "38"
        )

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return lengthNoSpaces == 15
        }

        override fun separateCardNumber(cardNumberNoSpaces: String): String {
            val number = if (cardNumberNoSpaces.length > 16)
                cardNumberNoSpaces.substring(0, 16)
            else
                cardNumberNoSpaces

            val separatedBuilder = StringBuilder(number)
            val separator = ' '

            // Insert separators from end to start

            // Expected: 3759 876543 21001
            if (number.length > 10) {
                separatedBuilder.insert(10, separator)
            }
            if (number.length > 4) {
                separatedBuilder.insert(4, separator)
            }

            return separatedBuilder.toString()
        }
    }

    /**
     * Mastercard card
     */
    @Parcelize
    object Mastercard : CardBrand() {
        override val name: String = "mastercard"
        override val displayName: Int = R.string.gd_card_brand_mastercard
        override val logo: Int = R.drawable.gd_ic_card_mastercard
        override val prefixes = listOf(
                "2221",
                "2222",
                "2223",
                "2224",
                "2225",
                "2226",
                "2227",
                "2228",
                "2229",
                "223",
                "224",
                "225",
                "226",
                "227",
                "228",
                "229",
                "23",
                "24",
                "25",
                "26",
                "270",
                "271",
                "2720",
                "51",
                "52",
                "53",
                "54",
                "55"
        )

        override fun matchesCardNumberLength(lengthNoSpaces: Int): Boolean {
            return lengthNoSpaces == 16
        }
    }

    companion object {
        const val MIN_LENGTH_STANDARD = 12
        const val MAX_LENGTH_STANDARD = 19

        const val LONGEST_PREFIX = 6

        private val REGEX_CARD_NUM_SEPARATORS = Regex("\\s|-")

        /**
         * A list of all card brands supported by Geidea Payment SDK. Some may not be active for
         * your current Merchant configuration.
         */
        @JvmStatic
        val allSupportedBrands: Set<CardBrand> get() = setOf(
                // Some BIN ranges of MADA are smaller and overlap Mastercard and Visa ones,
                // that is why MADA is given precedence over Visa and Mastercard.
                Mada,
                Meeza,
                Visa,
                AmericanExpress,
                Mastercard
        )

        /**
         * Return a [CardBrand] from a name.
         *
         * @see [CardBrand.name]
         */
        @JvmStatic
        fun fromBrandName(brandName: String): CardBrand {
            return allSupportedBrands.firstOrNull { brand -> brand.name.equals(brandName, ignoreCase = true) }
                    ?: Unknown
        }

        /**
         * Recognizes the card brand by a given first few digits of a card number.
         *
         * @param cardNumberPrefix must be a known prefix of the card number. Please refer to [CardBrand.prefixes] for
         * the lists of all supported brand prefixes.
         * @return the matching card brand
         */
        @JvmStatic
        fun fromCardNumberPrefix(cardNumberPrefix: String): CardBrand {
            val prefixNoSpaces = removeCardNumberSpaces(cardNumberPrefix)
            return allSupportedBrands.firstOrNull { it.matches(prefixNoSpaces) } ?: Unknown
        }

        /**
         * Returns a set of brands that are known to start with the digits given in [partialCardNumber].
         *
         * @param partialCardNumber some starting digits (without separators)
         * @return a set of possible brands. If [partialCardNumber] is null or blank an empty set is returned.
         */
        @JvmStatic
        fun possibleBrands(partialCardNumber: String?): Set<CardBrand> {

            val sanitizedNumber = partialCardNumber?.trim()
            if (sanitizedNumber.isNullOrBlank()) {
                return emptySet()
            }

            val brandPrefixPairList = allSupportedBrands
                    .flatMap { brand -> brand.prefixes.map { prefix -> brand to prefix } }
                    .filter { (brand, prefix) ->
                        if (prefix.length > sanitizedNumber.length) {
                            // E.g.
                            // prefix = 223, sanitizedNumber = 2, matching
                            // prefix = 35, sanitizedNumber = 5, NOT matching
                            prefix.startsWith(sanitizedNumber)
                        } else {
                            // E.g.
                            // prefix = 223, sanitizedNumber = 223, matching
                            // prefix = 223, sanitizedNumber = 2235, matching
                            // prefix = 223, sanitizedNumber = 1235, NOT matching
                            sanitizedNumber.startsWith(prefix)
                        }
                    }

            val exactMatch = brandPrefixPairList.maxByOrNull { it.second.length }
                    ?.takeIf { it.second == partialCardNumber }
                    ?.first

            return if (exactMatch != null) {
                setOf(exactMatch)
            } else {
                brandPrefixPairList.map { it.first }
                        .toSet()
                        .takeIf { it.isNotEmpty() } ?: setOf(Unknown)
            }
        }

        @JvmStatic
        fun isSupportedBrandName(brandName: String): Boolean {
            return allSupportedBrands
                    .minus(Unknown)
                    .map(CardBrand::name)
                    .contains(brandName.lowercase(Locale.getDefault()))
        }

        @JvmStatic
        internal fun removeCardNumberSpaces(cardNumber: String): String {
            return REGEX_CARD_NUM_SEPARATORS.replace(cardNumber, "")
        }
    }
}