package net.geidea.paymentsdk.ui.model

import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import kotlinx.parcelize.Parcelize
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.model.MerchantConfigurationResponse

/**
 * Describes the names, texts, logos and other aspects of a payment method.
 */
public sealed interface PaymentMethodDescriptor : Parcelable {

    /**
     * Unique name identifying the payment method in server requests and responses.
     */
    val name: String

    @get:StringRes
    val text: Int

    /**
     * Payment method logo drawable.
     *
     * Note: The image and its size might be subject of change in future.
     */
    @get:DrawableRes
    val logo: Int?
        get() = null

    /**
     * Describes a credit or debit card payment.
     *
     * @constructor create a card payment method descriptor with a specific set of brands
     * @property acceptedBrands set of card brands to allow on client-side or empty set to all
     * configured on server card brands. Card brands not configured server-side, that is
     * - not listed in [MerchantConfigurationResponse.paymentMethods] will be discarded by the SDK.
     *
     * @see [MerchantConfigurationResponse.paymentMethods]
     */
    @Parcelize
    data class Card(val acceptedBrands: Set<CardBrand> = emptySet()) : PaymentMethodDescriptor {

        constructor(vararg acceptedBrand: CardBrand) : this(setOf(*acceptedBrand))

        override val name: String get() = "card"
        override val text: Int get() = R.string.gd_pm_card
        override fun toString(): String = "Card(acceptedBrands = $acceptedBrands)"
    }

    @Parcelize
    object MeezaQr : PaymentMethodDescriptor {
        override val name: String get() = "meezadigital"
        override val text: Int get() = R.string.gd_pm_meeza_qr
        override val logo: Int get() = R.drawable.gd_ic_meeza_logo
        override fun toString(): String = "MeezaQr"
    }
}

/**
 * Describes a Buy Now Pay Later (Installments) payment method.
 */
public sealed interface BnplPaymentMethodDescriptor : PaymentMethodDescriptor {

    override val name: String get() = providerName

    /**
     * Unique name identifying the BNPL provider. Same as [name].
     *
     * @see net.geidea.paymentsdk.model.bnpl.BnplDetails.provider
     */
    val providerName: String

    /**
     * Logo image used to embed in the payment screens of other payment methods when in
     * "Down payment" mode.
     */
    @get:DrawableRes
    val embeddableLogo: Int?
        get() = logo

    @get:StringRes
    val nameShort: Int

    @Parcelize
    public object ValuInstallments : BnplPaymentMethodDescriptor {
        override val providerName: String get() = "valu"
        override val text: Int get() = R.string.gd_pm_valu
        override val nameShort: Int get() = R.string.gd_pm_valu_short
        override val logo: Int get() = R.drawable.gd_ic_valu_logo
        override fun toString(): String = "ValuInstallments"
    }

    @Parcelize
    public object ShahryInstallments : BnplPaymentMethodDescriptor {
        override val providerName: String get() = "shahry"
        override val text: Int get() = R.string.gd_pm_shahry
        override val nameShort: Int get() = R.string.gd_pm_shahry_short
        override val logo: Int get() = R.drawable.gd_ic_shahry_logo
        override val embeddableLogo: Int get() = R.drawable.gd_ic_shahry_logo_and_name
        override fun toString(): String = "ShahryInstallments"
    }

    @Parcelize
    public object SouhoolaInstallments : BnplPaymentMethodDescriptor {
        override val providerName: String get() = "souhoola"
        override val text: Int get() = R.string.gd_pm_souhoola
        override val nameShort: Int get() = R.string.gd_pm_souhoola_short
        override val logo: Int get() = R.drawable.gd_ic_souhoola_logo_small2
        override val embeddableLogo: Int get() = R.drawable.gd_ic_souhoola_logo
        override fun toString(): String = "SouhoolaInstallments"
    }
}

internal fun getBnplPaymentMethodBy(providerName: String): BnplPaymentMethodDescriptor =
    bnplPaymentMethods.first { it.providerName.equals(providerName, ignoreCase = true) }

internal val bnplPaymentMethods
    get() = setOf(
        BnplPaymentMethodDescriptor.ValuInstallments,
        BnplPaymentMethodDescriptor.ShahryInstallments,
        BnplPaymentMethodDescriptor.SouhoolaInstallments
    )