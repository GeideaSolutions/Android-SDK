package net.geidea.paymentsdk.sampleapp.sample.orders

import android.annotation.SuppressLint
import androidx.recyclerview.widget.RecyclerView
import net.geidea.paymentsdk.model.Order
import net.geidea.paymentsdk.model.TransactionStatus
import net.geidea.paymentsdk.sampleapp.R
import net.geidea.paymentsdk.sampleapp.databinding.ItemOrderBinding

class OrderViewHolder(
        private val binding: ItemOrderBinding,
        private val onClick: (Order) -> Unit
) : RecyclerView.ViewHolder(binding.root) {

    @SuppressLint("SetTextI18n")
    fun bind(order: Order) = with(binding) {
        binding.root.setOnClickListener { onClick(order) }

        // Show only the first group of 8 digits of the UUID
        idTextView.text = order.orderId.substring(0..7)
        dateTextView.text = order.updatedDate?.split('T')?.get(0)    // Get the date part of "2021-02-16T09:26:10.6854195"
        amountTextView.text = "${order.amount} ${order.currency}"

        val statusIcon: Int = when (order.status) {
            TransactionStatus.SUCCESS -> R.drawable.ic_round_check_circle
            TransactionStatus.IN_PROGRESS -> R.drawable.ic_round_change_circle
            TransactionStatus.FAILED -> R.drawable.ic_round_remove_circle
            else -> 0
        }
        statusImageView.setImageResource(statusIcon)
    }

    fun bindPlaceholder() {
        //TODO()
    }
}
