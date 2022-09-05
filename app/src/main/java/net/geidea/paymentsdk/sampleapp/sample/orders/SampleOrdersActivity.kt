package net.geidea.paymentsdk.sampleapp.sample.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.paging.*
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import net.geidea.paymentsdk.api.gateway.GatewayApi
import net.geidea.paymentsdk.flow.GeideaResult
import net.geidea.paymentsdk.model.MerchantConfigurationResponse
import net.geidea.paymentsdk.model.order.*
import net.geidea.paymentsdk.model.pay.PaymentResponse
import net.geidea.paymentsdk.sampleapp.*
import net.geidea.paymentsdk.sampleapp.databinding.ActivitySampleOrdersBinding
import net.geidea.paymentsdk.sampleapp.sample.BaseSampleActivity
import net.geidea.paymentsdk.sampleapp.sample.GeideaPagingSource.Companion.PAGE_SIZE

@FlowPreview
class SampleOrdersActivity : BaseSampleActivity<ActivitySampleOrdersBinding>(), OrderFiltersFragment.Callbacks {

    private var savedInstanceState: Bundle? = null
    private val searchRequest = MutableStateFlow<OrderSearchRequest?>(null)
    private lateinit var pagingAdapter: OrdersAdapter

    private val ordersPagingFlow = searchRequest
            .filterNotNull()
            .flatMapLatest { searchResults(it) }
            .cachedIn(lifecycleScope)

    override fun createBinding(layoutInflater: LayoutInflater): ActivitySampleOrdersBinding {
        return ActivitySampleOrdersBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.savedInstanceState = savedInstanceState

        with(binding) {
            setSupportActionBar(includeAppBar.toolbar)
            title = "Orders sample"
        }
    }

    override fun setupUi(merchantConfig: MerchantConfigurationResponse) {
        return with(binding) {
            pagingAdapter = OrdersAdapter(onItemClick = ::onOrderClicked)
            recyclerView.adapter = pagingAdapter
            recyclerView.layoutManager = LinearLayoutManager(this@SampleOrdersActivity)
            recyclerView.setHasFixedSize(true)

            pagingAdapter.addLoadStateListener { loadStates: CombinedLoadStates ->
                recyclerView.isVisible = loadStates.refresh !is LoadState.Error
                progressBar.isVisible = loadStates.refresh is LoadState.Loading
                errorView.isVisible = loadStates.refresh is LoadState.Error
                errorTextView.text = (loadStates.refresh as? LoadState.Error)?.error?.message ?: "Loading failed!"
            }

            retryButton.setOnClickListener {
                pagingAdapter.retry()
            }

            lifecycleScope.launch {
                ordersPagingFlow.collectLatest(pagingAdapter::submitData)
            }

            val savedRequest: OrderSearchRequest? = savedInstanceState?.getParcelable(STATE_REQUEST)
            search(savedRequest ?: OrderSearchRequest(take = PAGE_SIZE))
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        searchRequest.value?.let {
            outState.putParcelable(STATE_REQUEST, it)
        }
    }

    private fun search(searchRequest: OrderSearchRequest) {
        this.searchRequest.value = searchRequest
    }

    private fun searchResults(searchRequest: OrderSearchRequest): Flow<PagingData<Order>> {
        return Pager(
                config = PagingConfig(
                        pageSize = PAGE_SIZE,
                        enablePlaceholders = false,
                ),
                initialKey = searchRequest,
        ) {
            OrdersPagingSource()
        }.flow
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_sample_orders, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.item_order_filters -> { showFilters(); true }
            else -> false
        }
    }

    private fun onOrderClicked(order: Order) {
        // Get the detailed version of the order (with all transactions)
        lifecycleScope.launch {
            val orderResult = GatewayApi.getOrder(order.orderId)
            onOrderResult(orderResult)
        }
    }

    private fun onOrderResult(orderResult: GeideaResult<Order>) = with(binding) {
        when (orderResult) {
            is GeideaResult.Success -> {
                showOrder(orderResult.data, ::capture, ::refund, ::cancel)
            }
            is GeideaResult.Error -> {
                showErrorResult(orderResult.toJson(pretty = true))
            }
            is GeideaResult.Cancelled -> {
                snack("Cancelled.")
            }
        }
    }

    private fun capture(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = CaptureRequest {
                this.orderId = order.orderId
                this.callbackUrl = order.callbackUrl
            }
            onOrderResult(GatewayApi.captureOrder(request))
            pagingAdapter.refresh()
        }
    }

    private fun refund(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = RefundRequest {
                this.orderId = order.orderId
                this.callbackUrl = order.callbackUrl
            }
            onOrderResult(GatewayApi.refundOrder(request))
            pagingAdapter.refresh()
        }
    }

    private fun cancel(order: Order) {
        lifecycleScope.launch(Dispatchers.Main) {
            val request = CancelRequest {
                orderId = order.orderId
                reason = "CancelledByUser"
            }
            when (val result = GatewayApi.cancelOrder(request)) {
                is GeideaResult.Success<PaymentResponse> -> showObjectAsJson(result.data)
                is GeideaResult.Error -> showObjectAsJson(result)
                is GeideaResult.Cancelled -> showObjectAsJson(result)
            }
            pagingAdapter.refresh()
        }
    }

    private fun showFilters() {
        searchRequest.value?.let {
            OrderFiltersFragment
                    .newInstance(it)
                    .show(supportFragmentManager, "orderFilters")
        }
    }

    override fun onFiltersChanged(searchRequest: OrderSearchRequest) {
        search(searchRequest)
    }

    companion object {
        const val STATE_REQUEST = "request"
    }
}