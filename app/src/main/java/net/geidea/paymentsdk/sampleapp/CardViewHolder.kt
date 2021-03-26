package net.geidea.paymentsdk.sampleapp

import androidx.recyclerview.widget.RecyclerView
import net.geidea.paymentsdk.sampleapp.databinding.ItemCardBinding

class CardViewHolder(private val binding: ItemCardBinding) : RecyclerView.ViewHolder(binding.root) {
    fun bind(cardItem: CardItem) {
        binding.tokenizedCardView.card = cardItem
    }
}