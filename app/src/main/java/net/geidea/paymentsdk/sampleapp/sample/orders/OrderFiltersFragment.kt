package net.geidea.paymentsdk.sampleapp.sample.orders

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.util.Pair
import androidx.core.view.children
import androidx.core.view.forEach
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import net.geidea.paymentsdk.model.order.OrderSearchRequest
import net.geidea.paymentsdk.sampleapp.R
import net.geidea.paymentsdk.sampleapp.databinding.DialogOrderFiltersBinding
import net.geidea.paymentsdk.sampleapp.sample.GeideaPagingSource.Companion.DATE_FORMAT_SERVER
import net.geidea.paymentsdk.sampleapp.sample.GeideaPagingSource.Companion.PAGE_SIZE
import java.text.SimpleDateFormat
import java.util.*

class OrderFiltersFragment : BottomSheetDialogFragment() {

    interface Callbacks {
        fun onFiltersChanged(orderSearchRequest: OrderSearchRequest)
    }

    private var _binding: DialogOrderFiltersBinding? = null
    private val binding get() = _binding!!

    private var callbacks: Callbacks? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callbacks = context as Callbacks
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogOrderFiltersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        _binding = null
        super.onDestroyView()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        with(binding) {
            // Restore UI from the "request" argument

            val searchRequest: OrderSearchRequest = requireArguments().getParcelable(ARG_REQUEST)!!
            statusChipGroup.forEach { view ->
                if (view.tag == searchRequest.status) {
                    (view as Chip).isChecked = true
                }
            }

            detailedStatusChipGroup.forEach { view ->
                val chip = view as Chip
                chip.isChecked = if (chip.tag == null) {
                    // "All" chip
                    searchRequest.detailedStatuses.isEmpty()
                } else {
                    // Other chips
                    searchRequest.detailedStatuses.contains(chip.tag)
                }
            }

            if (searchRequest.fromDate != null) {
                fromDateEditText.setText(searchRequest.fromDate)
            }
            if (searchRequest.toDate != null) {
                toDateEditText.setText(searchRequest.toDate)
            }

            // Set up listeners

            statusChipGroup.setOnCheckedChangeListener { group, checkedId ->
                updateRequest()
            }

            detailedStatusChipGroup.forEach { view ->
                (view as? Chip)?.let { chip ->
                    chip.setOnCheckedChangeListener { compoundButton, isChecked ->
                        onDetailedStatusChipChecked(compoundButton as Chip, isChecked)
                    }
                }
            }
            fromDateEditText.setOnClickListener {
                showDateRangePicker(getFromToDateRange())
            }
            toDateEditText.setOnClickListener {
                showDateRangePicker(getFromToDateRange())
            }
        }
    }

    private fun onDetailedStatusChipChecked(chip: Chip, checked: Boolean) = with(binding) {
        val id = chip.id
        if (checked) {
            if (id == R.id.detailedStatusAllChip) {
                // Clicking "All" un-checks all others
                detailedStatusChipGroup.forEach {
                    if (it != detailedStatusAllChip) {
                        (it as Chip).isChecked = false
                    }
                }
            } else {
                // Checking any other un-checks "All"
                detailedStatusAllChip.isChecked = false
            }
        }
        if (!checked && detailedStatusChipGroup.checkedChipIds.isEmpty()) {
            // If all others are unchecked "All" is automatically checked
            detailedStatusAllChip.isChecked = true
        }

        updateRequest()
    }

    private fun showDateRangePicker(dateRange: Pair<Long, Long>?) = with(binding) {
        val datePicker: MaterialDatePicker<Pair<Long, Long>> = MaterialDatePicker.Builder
                .dateRangePicker()
                .setSelection(dateRange)
                .setCalendarConstraints(CalendarConstraints.Builder()
                        .setValidator(DateValidatorPointBackward.now())
                        .build()
                )
                .build()
        datePicker.addOnPositiveButtonClickListener { fromToPair ->
            fromDateEditText.setText(DATE_FORMAT_SERVER.format(fromToPair.first))
            toDateEditText.setText(DATE_FORMAT_SERVER.format(fromToPair.second))

            updateRequest()
        }
        datePicker.show(childFragmentManager, datePicker.toString())
    }

    private fun updateRequest() {
        val request = createRequestFromUi()
        requireArguments().putParcelable(ARG_REQUEST, request)
        callbacks?.onFiltersChanged(request)
    }

    private fun createRequestFromUi(): OrderSearchRequest = with(binding) {
        val status = statusChipGroup.findViewById<Chip>(statusChipGroup.checkedChipId).tag as String?
        val detailedStatuses: Set<String> = detailedStatusChipGroup.children
                .filter { (it as Chip).isChecked }
                .map { it.tag as String? }
                .filterNotNull()        // The "All" chip has null tag
                .toSet()

        return OrderSearchRequest {
            this.status = status
            this.detailedStatuses = detailedStatuses
            this.fromDate = fromDateEditText.text?.toString()
            this.toDate = toDateEditText.text?.toString()
            this.take = PAGE_SIZE
        }
    }

    private fun getFromToDateRange(): Pair<Long, Long>? {
        val fromText = binding.fromDateEditText.text?.toString()
        val toText = binding.toDateEditText.text?.toString()

        return if (fromText.isNullOrEmpty() || toText.isNullOrEmpty()) {
            null
        } else {
            Pair(DATE_FORMAT_SERVER.parseCalendar(fromText).timeInMillis,
                    DATE_FORMAT_SERVER.parseCalendar(toText).timeInMillis
            )
        }
    }

    private fun SimpleDateFormat.parseCalendar(pattern: String): Calendar {
        return Calendar.getInstance(Locale.getDefault()).apply { time = parse(pattern)!!; roll(Calendar.HOUR_OF_DAY, 12) }
    }

    companion object {
        const val ARG_REQUEST = "request"

        fun newInstance(request: OrderSearchRequest): OrderFiltersFragment {
            return OrderFiltersFragment().apply {
                arguments = bundleOf(ARG_REQUEST to request)
            }
        }
    }
}