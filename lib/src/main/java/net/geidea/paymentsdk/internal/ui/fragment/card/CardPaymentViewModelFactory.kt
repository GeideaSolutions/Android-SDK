package net.geidea.paymentsdk.internal.ui.fragment.card

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.navGraphViewModels
import net.geidea.paymentsdk.GdCardGraphArgs
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.R
import net.geidea.paymentsdk.flow.pay.PaymentViewModel
import net.geidea.paymentsdk.internal.di.SdkComponent

@Suppress("UNCHECKED_CAST")
@GeideaSdkInternal
internal class CardPaymentViewModelFactory(
    private val paymentViewModel: PaymentViewModel,
    private val args: GdCardGraphArgs,
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        val initialPaymentData = defineInitialPaymentData()
        val is3ds2Active = paymentViewModel.merchantConfiguration.useMpgsApiV60 == true
        val viewModel: BaseCardPaymentViewModel = if (!is3ds2Active) {

            // 3DS v1

            when {
                initialPaymentData.tokenId != null -> {

                    // 3DS v1 - tokenized card

                    TokenPaymentViewModel3dsV1(
                        paymentViewModel = paymentViewModel,
                        authenticationV1Service = SdkComponent.authenticationV1Service,
                        paymentService = SdkComponent.paymentService,
                        cancellationService = SdkComponent.cancellationService,
                        merchantConfiguration = paymentViewModel.merchantConfiguration,
                    ).apply(TokenPaymentViewModel3dsV1::start)
                }
                else -> {

                    // 3DS v1 - plain card data

                    CardPaymentViewModel3dsV1(
                        paymentViewModel = paymentViewModel,
                        authenticationV1Service = SdkComponent.authenticationV1Service,
                        paymentService = SdkComponent.paymentService,
                        cancellationService = SdkComponent.cancellationService,
                        merchantConfiguration = paymentViewModel.merchantConfiguration,
                        step = args.step,
                        downPaymentAmount = args.downPaymentAmount,
                    )
                }
            }

        } else {

            // 3DS v2

            when {
                initialPaymentData.tokenId != null -> {

                    // 3DS v2 - tokenized card

                    TokenPaymentViewModel3dsV2(
                        paymentViewModel = paymentViewModel,
                        authenticationV1Service = SdkComponent.authenticationV1Service,
                        authenticationV3Service = SdkComponent.authenticationV3Service,
                        paymentService = SdkComponent.paymentService,
                        cancellationService = SdkComponent.cancellationService,
                        merchantConfiguration = paymentViewModel.merchantConfiguration,
                        initialPaymentData = initialPaymentData,
                    ).apply(TokenPaymentViewModel3dsV2::start)
                }
                else -> {

                    // 3DS v2 - plain card data

                    CardPaymentViewModel3dsV2(
                        paymentViewModel = paymentViewModel,
                        authenticationV1Service = SdkComponent.authenticationV1Service,
                        authenticationV6Service = SdkComponent.authenticationV6Service,
                        paymentService = SdkComponent.paymentService,
                        cancellationService = SdkComponent.cancellationService,
                        merchantConfiguration = paymentViewModel.merchantConfiguration,
                        step = args.step,
                        downPaymentAmount = args.downPaymentAmount,
                    )
                }
            }
        }

        return viewModel as T
    }

    private fun defineInitialPaymentData() = paymentViewModel.initialPaymentData.copy(amount = args.downPaymentAmount ?: paymentViewModel.initialPaymentData.amount)
}

internal inline fun <reified VM : BaseCardPaymentViewModel> Fragment.cardNavGraphViewModel(
    paymentViewModelLazy: Lazy<PaymentViewModel>,
    argsLazy: Lazy<GdCardGraphArgs>,
): Lazy<VM> = navGraphViewModels(
    navGraphId = R.id.gd_card_graph,
    factoryProducer = {
        CardPaymentViewModelFactory(
            paymentViewModel = paymentViewModelLazy.value,
            args = argsLazy.value,
        )
    }
)