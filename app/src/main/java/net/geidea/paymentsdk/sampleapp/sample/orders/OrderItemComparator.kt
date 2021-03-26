package net.geidea.paymentsdk.sampleapp.sample.orders

import androidx.recyclerview.widget.DiffUtil
import net.geidea.paymentsdk.model.Order

class OrderItemComparator : DiffUtil.ItemCallback<Order>() {
    override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem.orderId == newItem.orderId

    override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean =
            oldItem == newItem
}