package net.geidea.paymentsdk.sampleapp

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.annotation.ColorInt
import androidx.cardview.widget.CardView
import androidx.core.graphics.ColorUtils
import androidx.core.view.isVisible
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.sampleapp.databinding.ViewTokenizedCardBinding
import kotlin.math.abs

class TokenizedCardView @JvmOverloads constructor(
        context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : CardView(context, attrs, defStyleAttr) {

    private val binding = ViewTokenizedCardBinding.inflate(LayoutInflater.from(context), this, true)

    init {
        // card is null by default, so view should be hidden as well
        isVisible = false
    }

    var card: CardItem? = null
        set(cardItem) {
            if (field != cardItem) {
                field = cardItem

                updateView()
            }
        }

    private fun updateView() = with(binding) {
        isVisible = card != null

        card?.let {
            setCardBackgroundColor(generateCardColor(it))
        }

        val brand: CardBrand = card?.brand?.let(CardBrand::fromBrandName)
                ?: CardBrand.Unknown
        logoImageView.setImageResource(brand.logo)
        tokenIdTextView.text = card?.tokenId
        maskCardNumberTextView.text = card?.let {
            brand.separateCardNumber(it.maskedCardNumber)
        }
        cardHolderTextView.text = card?.cardholderName
        expiryDateTextView.text = card?.expiryDate
    }

    @ColorInt
    private fun generateCardColor(cardItem: CardItem): Int {
        // Generate a pseudo-random hue from the card data
        val hue: Int = abs(cardItem.hashCode()) % 360
        // Use slightly desaturated color as a card background
        return ColorUtils.HSLToColor(floatArrayOf(hue.toFloat(), 0.6f, 0.7f))
    }
}