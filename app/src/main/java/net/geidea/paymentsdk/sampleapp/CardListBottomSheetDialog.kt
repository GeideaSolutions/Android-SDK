package net.geidea.paymentsdk.sampleapp

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import net.geidea.paymentsdk.sampleapp.databinding.DialogCardListBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemCardBinding
import kotlin.math.abs
import kotlin.math.max

class CardListBottomSheetDialog(
        context: Context,
        val cardItems: List<CardItem>,
        val onClearButtonClicked: () -> Unit,
        val onConfirmButtonClicked: (CardItem) -> Unit,
) : BottomSheetDialog(context) {

    private val binding = DialogCardListBinding.inflate(LayoutInflater.from(context))

    init {
        require(cardItems.isNotEmpty()) { "cardItems must not be empty" }
        setContentView(binding.root)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Fixed width so that the view pager items won't be stretched
        window!!.setLayout(context.resources.getDimensionPixelSize(R.dimen.bottom_sheet_width), ViewGroup.LayoutParams.MATCH_PARENT)
    }

    override fun setContentView(view: View) {
        super.setContentView(view)

        with(binding) {
            cardViewPager.adapter = object : RecyclerView.Adapter<CardViewHolder>() {
                override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
                    val itemBinding = ItemCardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                    return CardViewHolder(itemBinding)
                }

                override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
                    holder.bind(cardItems[position])
                }

                override fun getItemCount(): Int = cardItems.size
            }

            cardViewPager.offscreenPageLimit = 1

            val pageMargin = context.resources.getDimensionPixelOffset(R.dimen.page_margin).toFloat()

            cardViewPager.setPageTransformer { page, position ->
                val myOffset: Float = position * -(2 * pageMargin)
                when {
                    position < -1 -> {
                        page.translationX = -myOffset
                    }
                    position <= 1 -> {
                        val scaleFactor = max(0.7f, 1 - abs(position - 0.14285715f))
                        page.translationX = myOffset
                        page.scaleX = scaleFactor
                        page.scaleY = scaleFactor
                        page.alpha = scaleFactor
                    }
                    else -> {
                        page.alpha = 0f
                        page.translationX = myOffset
                    }
                }
            }

            cancelButton.setOnClickListener {
                dismiss()
            }

            clearButton.setOnClickListener {
                dismiss()
                onClearButtonClicked()
            }
            confirmButton.setOnClickListener {
                dismiss()
                onConfirmButtonClicked(cardItems[cardViewPager.currentItem])
            }
        }
    }
}