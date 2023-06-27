package net.geidea.paymentsdk.internal.ui.fragment.options

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.*
import androidx.annotation.DrawableRes
import androidx.core.view.children
import androidx.core.view.descendants
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.transition.TransitionManager
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.databinding.GdFragmentPaymentOptionsBinding
import net.geidea.paymentsdk.databinding.GdItemPaymentMethodBinding
import net.geidea.paymentsdk.flow.pay.PaymentMethodsFilter
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent
import net.geidea.paymentsdk.internal.ui.fragment.base.BaseFragment
import net.geidea.paymentsdk.internal.ui.widget.Collapsible
import net.geidea.paymentsdk.internal.ui.widget.CollapsibleGroupLinearLayout
import net.geidea.paymentsdk.internal.ui.widget.MeezaQrOptionView
import net.geidea.paymentsdk.internal.ui.widget.setup
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.model.CardBrand
import net.geidea.paymentsdk.ui.model.BnplPaymentMethodDescriptor
import net.geidea.paymentsdk.ui.model.PaymentMethodDescriptor

internal class PaymentOptionsFragment :
    BaseFragment<PaymentOptionsViewModel>(R.layout.gd_fragment_payment_options) {

    private val args: PaymentOptionsFragmentArgs by navArgs()

    private val binding by viewBinding(GdFragmentPaymentOptionsBinding::bind)

    override val viewModel: PaymentOptionsViewModel by viewModels {
        ViewModelFactory(paymentViewModel, args)
    }

    private lateinit var inflater: LayoutInflater

    private var meezaQrOptionView: MeezaQrOptionView? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) = with(binding) {
        super.onViewCreated(view, savedInstanceState)

        runSafely {
            // Make sure we clear the result value in case errors happened with incomplete payment
            paymentViewModel.setResult(paymentViewModel.makeDefaultCancelledResult(
                orderId = paymentViewModel.orderId)
            )

            if (args.downPaymentAmount == null) {
                // Start on clear
                paymentViewModel.clearSession()
            }

            appBarWithStepper.setup(args.step, nestedScrollView)
            appBarWithStepper.stepper.setOnBackClickListener { viewModel.navigateBack() }
            addBackListener(viewModel::navigateBack)

            viewModel.nextButtonEnabledLiveData.observe(viewLifecycleOwner, nextButton::setEnabled)
            nextButton.setOnClickListener { viewModel.onNextButtonClicked() }
            cancelButton.setOnClickListener { viewModel.navigateCancel() }

            // Restore the selected a radio button on return from back stack
            viewModel.selectedPaymentMethodLiveData.observe(viewLifecycleOwner) { paymentMethod ->
                selectPaymentMethod(paymentMethod)
            }

            viewModel.progressLiveData.observe(viewLifecycleOwner) { inProgress ->
                meezaQrOptionView?.isProgressVisible = inProgress
                meezaQrOptionView?.isPhoneInputEnabled = !inProgress
            }

            inflater = LayoutInflater.from(context)

            paymentMethodsRadioGroup.setOnCheckedChangeListener { _, checkedId ->
                beginDelayedTransition()

                val checkedButton = paymentMethodsRadioGroup.findViewById<CompoundButton>(checkedId)
                viewModel.onPaymentMethodSelected(checkedButton.tag as PaymentMethodDescriptor)

                // Update checked and expanded state of all item views
                paymentMethodsRadioGroup.children.forEach { itemLayout ->

                    // Search deeply if checkedButton is indirect child of itemLayout
                    val isContained = itemLayout is ViewGroup
                            && itemLayout.descendants.contains(checkedButton)

                    if (itemLayout is Checkable) {
                        itemLayout.isChecked = isContained
                    }

                    if (itemLayout is Collapsible) {
                        itemLayout.isExpanded = checkedButton.isChecked && isContained
                    }
                }
                hideKeyboard(root)
            }

            val optionItems: List<PaymentOptionItem> = viewModel.optionItems
            optionItems.map { item ->
                when (item) {
                    is PaymentMethodItem -> {
                        when (item.paymentMethod) {
                            is PaymentMethodDescriptor.Card -> inflateAndBindSimpleItem(item, inGroup = false)
                            is PaymentMethodDescriptor.MeezaQr -> inflateAndBindMeezaQrItem(item)
                            // BNPL items are always grouped as children of PaymentMethodGroup
                            is BnplPaymentMethodDescriptor -> error("Should not reach here")
                        }
                    }
                    is PaymentMethodGroup -> {
                        inflateAndBindGroup(item)
                    }
                }
            }.onEach(paymentMethodsRadioGroup::addView)
        }

        getNavigationResult<Boolean>(R.id.gd_paymentoptionsfragment, "selectMeezaQr") { select ->
            if (select) {
                selectPaymentMethod(PaymentMethodDescriptor.MeezaQr)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.start()
    }

    private fun selectPaymentMethod(paymentMethod: PaymentMethodDescriptor?) {
        with(binding) {
            if (paymentMethod != null) {
                val radioButton = paymentMethodsRadioGroup.descendants
                    .filterIsInstance<RadioButton>()
                    .firstOrNull { it.tag === paymentMethod }
                if (paymentMethodsRadioGroup.checkedItem?.id != radioButton?.id) {
                    paymentMethodsRadioGroup.check(radioButton)
                }
            } else {
                paymentMethodsRadioGroup.clearCheck()
            }
        }
    }

    /**
     * Create the parent (group) view and the child views.
     */
    private fun inflateAndBindGroup(paymentMethodGroup: PaymentMethodGroup): View {
        val paymentMethods = paymentMethodGroup.items
        return CollapsibleGroupLinearLayout(requireContext()).apply {
            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0f)
                .apply { bottomMargin = 16.dp }

            setOnClickHeaderListener {
                toggleExpandCollapse(this)
            }

            labelText = paymentMethodGroup.label.toCharSequence()
            paymentMethods
                .map(PaymentMethodItem::paymentMethod)
                .mapNotNull(PaymentMethodDescriptor::logo)
                .take(MAX_GROUP_LOGOS)
                .onEach(::addLogo)

            // Should be collapsed by default
            isExpanded = false

            paymentMethods
                .map { pm -> inflateAndBindSimpleItem(pm, inGroup = true) }
                .onEach(::addItemView)
        }
    }

    /**
     * Create a simple item - one line radio button with logos. Used for cards and BNPL child items.
     */
    private fun inflateAndBindSimpleItem(
        paymentMethodItem: PaymentMethodItem,
        inGroup: Boolean = false,
    ): View {
        val paymentMethod = paymentMethodItem.paymentMethod
        return with(GdItemPaymentMethodBinding.inflate(inflater)) {

            root.id = View.generateViewId()
            root.layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, 48.dp, 0f)
                .apply {
                    if (!inGroup)
                        bottomMargin = 16.dp
                    else
                        marginStart = 32.dp
                }
            if (inGroup) {
                root.background = null
            }

            // Make sure it has unique id
            radioButton.id = View.generateViewId()
            associateRadioButtonToPaymentMethod(radioButton, paymentMethod)

            // Label

            val defaultLabel = getString(paymentMethod.text)
            radioButton.text = if (inGroup) {
                if (paymentMethod is PaymentMethodDescriptor.Card) {
                    paymentMethod.acceptedBrands.firstOrNull()?.displayName?.let(::getString)
                        ?: defaultLabel
                } else {
                    defaultLabel
                }
            } else {
                paymentMethodItem.label?.toCharSequence() ?: defaultLabel
            }

            // Logo(s)

            val logo = paymentMethod.logo
            if (logo != null) {
                // Payment method logo
                logoImageView.setImageWithAspectRatio(logo)
            } else if (paymentMethod is PaymentMethodDescriptor.Card) {
                // Card brand logos
                paymentMethod.acceptedBrands
                    .map(CardBrand::logo)
                    .take(MAX_ITEM_LOGOS)
                    .map(::createLogoImageView)
                    .onEach(root::addView)
            }

            root
        }
    }

    /**
     * Create a Meeza QR item with a radio button and a phone input field below.
     */
    private fun inflateAndBindMeezaQrItem(paymentMethodItem: PaymentMethodItem): MeezaQrOptionView {

        val paymentMethod = paymentMethodItem.paymentMethod
        val meezaQrOptionView = MeezaQrOptionView(requireContext())

        this.meezaQrOptionView = meezaQrOptionView

        with(meezaQrOptionView) {

            id = View.generateViewId()
            layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT, 0f)
                .apply {
                    bottomMargin = 16.dp
                }

            // TODO this is supposed to expand the option in rare cases when it is selected but
            // collapsed and phone number is not visible. The approach does not work. Investigate why.
            /*root.setOnClickListener {
                if (!root.isExpanded) {
                    beginDelayedTransition()
                    root.isExpanded = true
                    phoneNumberEditText.requestFocus()
                }
            }*/

            // Enables/disables the "Next" button
            setOnValidChangeListener(viewModel.phoneNumberChangedListener)

            // Keyboard "Next" action
            setOnImeActionListener {
                meezaQrOptionView.validatePhoneNumber()
                viewModel.imeNextActionListener()
            }

            associateRadioButtonToPaymentMethod(radioButton, paymentMethod)

            // Label

            val defaultLabel = getString(paymentMethod.text)
            label = paymentMethodItem.label?.toCharSequence() ?: defaultLabel

            // Logo(s)

            paymentMethod.logo?.let(::setLogo)

            // Must be collapsed by default
            isExpanded = false
        }

        return meezaQrOptionView
    }

    private fun associateRadioButtonToPaymentMethod(radioButton: RadioButton, paymentMethod: PaymentMethodDescriptor) {
        radioButton.tag = paymentMethod
    }

    private fun createLogoImageView(@DrawableRes logo: Int): ImageView {
        return ImageView(context).apply {
            setImageResource(logo)
            layoutParams = LinearLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 0f).apply {
                gravity = Gravity.CENTER_VERTICAL
                marginStart = 4.dp
                marginEnd = 4.dp
            }
        }
    }

    private fun toggleExpandCollapse(groupView: Collapsible) {
        beginDelayedTransition()
        groupView.isExpanded = !groupView.isExpanded
        if (groupView.isExpanded) {
            collapseAll(except = groupView)
        }
    }

    private fun getAllCollapsibles(except: Collapsible?): List<Collapsible> {
        return binding.paymentMethodsRadioGroup.children.toList()
            .filterIsInstance<Collapsible>()
            .filter { it !== except }
    }

    private fun collapseAll(except: Collapsible? = null) {
        val collapsibles = getAllCollapsibles(except)
        collapsibles.onEach { it.isExpanded = false }
    }

    private fun beginDelayedTransition() {
        val sceneRoot: ViewGroup = binding.root
        TransitionManager.endTransitions(sceneRoot)
        TransitionManager.beginDelayedTransition(sceneRoot)
    }

    private class ViewModelFactory(
        private val paymentViewModel: PaymentViewModel,
        private val args: PaymentOptionsFragmentArgs,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return PaymentOptionsViewModel(
                paymentViewModel = paymentViewModel,
                paymentMethodsFilter = PaymentMethodsFilter(
                    merchantConfiguration = SdkComponent.merchantsService.cachedMerchantConfiguration,
                    paymentData = paymentViewModel.initialPaymentData,
                    isBnplDownPaymentMode = args.downPaymentAmount != null
                ),
                meezaService = SdkComponent.meezaService,
                connectivity = SdkComponent.connectivity,
                formatter = SdkComponent.formatter,
                step = args.step,
                downPaymentAmount = args.downPaymentAmount,
            ) as T
        }
    }

    companion object {
        const val MAX_ITEM_LOGOS = 7
        const val MAX_GROUP_LOGOS = 3
    }
}