package net.geidea.paymentsdk.sampleapp.sample.orders

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import net.geidea.paymentsdk.model.order.Order
import net.geidea.paymentsdk.sampleapp.databinding.ItemOrderBinding

class OrdersAdapter(
    diffCallback: DiffUtil.ItemCallback<Order> = OrderItemComparator(),
    private val onItemClick: (Order) -> Unit,
) : PagingDataAdapter<Order, OrderViewHolder>(diffCallback) {

    override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int
    ): OrderViewHolder {
        val itemBinding = ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(itemBinding, onItemClick)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val item = getItem(position)
        if (item == null) {
            holder.bindPlaceholder()
        } else {
            holder.bind(item)
        }
    }
}

