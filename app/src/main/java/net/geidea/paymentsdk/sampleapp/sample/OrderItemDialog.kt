package net.geidea.paymentsdk.sampleapp.sample

import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.flow.pay.OrderItem
import net.geidea.paymentsdk.sampleapp.customViewDialog
import net.geidea.paymentsdk.sampleapp.databinding.DialogOrderItemBinding
import net.geidea.paymentsdk.sampleapp.databinding.ItemOrderItemBinding
import net.geidea.paymentsdk.sampleapp.showErrorResult
import net.geidea.paymentsdk.sampleapp.textOrNull
import java.text.DecimalFormat

object OrderItemDialog {

    private val PRICE_FORMAT = DecimalFormat("#0.00")

    suspend fun FragmentActivity.inputOrderItem(existingItem: OrderItem? = null): OrderItem? {
        return with(DialogOrderItemBinding.inflate(layoutInflater)) {
            // Prepopulate form
            existingItem?.apply {
                merchantItemIdEditText.setText(merchantItemId)
                nameEditText.setText(name)
                descriptionEditText.setText(description)
                categoriesEditText.setText(categories)
                countEditText.setText(count.toString())
                priceEditText.setText(price?.toString())
            }

            try {
                customViewDialog(root, "Create Order Item") {
                    OrderItem {
                        merchantItemId = merchantItemIdEditText.textOrNull
                        name = nameEditText.textOrNull
                        description = descriptionEditText.textOrNull
                        categories = categoriesEditText.textOrNull
                        count = countEditText.textOrNull?.toInt()
                        price = priceEditText.textOrNull?.toBigDecimal()
                    }
                }
            } catch (e: Exception) {
                showErrorResult(e.message ?: "Error")
                null
            }
        }
    }

    fun ItemOrderItemBinding.init(
            activity: FragmentActivity,
            orderItem: OrderItem
    ): ItemOrderItemBinding {
        if (root.tag !== orderItem) {
            root.tag = orderItem
            priceTextView.text = orderItem.price.let(PRICE_FORMAT::format)
            countTextView.text = orderItem.count.toString()
            descriptionTextView.text = orderItem.description

            editItemButton.setOnClickListener { activity.editItem(root) }
            deleteItemButton.setOnClickListener { deleteItem(root) }
        }

        return this
    }

    fun collectOrderItems(orderItemsViewGroup: ViewGroup): List<OrderItem>  {
        // Gather the items which are already stored as tags in each of the item views
        return orderItemsViewGroup.children
                .map { view -> view.tag }
                .filterIsInstance<OrderItem>()
                .toList()
    }

    private fun FragmentActivity.editItem(itemView: View) {
        lifecycleScope.launch {
            val updatedItem = inputOrderItem(itemView.tag as OrderItem)
            if (updatedItem != null) {
                ItemOrderItemBinding
                        .bind(itemView)
                        .init(this@editItem, updatedItem)
            }
        }
    }

    private fun deleteItem(itemView: View) {
        (itemView.parent as ViewGroup).removeView(itemView)
    }
}