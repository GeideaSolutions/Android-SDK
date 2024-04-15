package net.geidea.paymentsdk.internal.di

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.GeideaPaymentSdk.applicationContext
import net.geidea.paymentsdk.GeideaSdkInternal
import net.geidea.paymentsdk.internal.client.HttpsClient
import net.geidea.paymentsdk.internal.service.*
import net.geidea.paymentsdk.internal.service.bnpl.shahry.ShahryService
import net.geidea.paymentsdk.internal.service.bnpl.souhoola.SouhoolaService
import net.geidea.paymentsdk.internal.service.bnpl.valu.ValuService
import net.geidea.paymentsdk.internal.service.impl.*
import net.geidea.paymentsdk.internal.service.receipt.ReceiptService
import net.geidea.paymentsdk.internal.util.*
import net.geidea.paymentsdk.internal.util.LocaleUtils.withSdkLocale

@GeideaSdkInternal
internal object SdkComponent {

    val dispatchersProvider: DispatchersProvider by lazy { DispatchersProvider() }

    private val supervisorJob: Job = SupervisorJob()
    val supervisorScope: CoroutineScope = CoroutineScope(dispatchersProvider.unconfined + supervisorJob)

    private val json: Json by lazy { Json { ignoreUnknownKeys = true } }

    internal var httpsClient: HttpsClient = HttpsClient(
        json = json,
        dispatchersProvider = dispatchersProvider
    )

    val formatter: NativeTextFormatter by lazy { NativeTextFormatterImpl(applicationContext.withSdkLocale()) }
    val connectivity: NetworkConnectivity by lazy { NetworkConnectivityDetector(applicationContext) }

    val merchantsService: MerchantsService by lazy { MerchantsServiceImpl(httpsClient) }
    val authenticationV1Service: AuthenticationV1Service by lazy { AuthenticationV1ServiceImpl(httpsClient) }
    val authenticationV3Service: AuthenticationV3Service by lazy { AuthenticationV3ServiceImpl(httpsClient) }
    val authenticationV4Service: AuthenticationV4Service by lazy { AuthenticationV4ServiceImpl(httpsClient) }
    val authenticationV6Service: AuthenticationV6Service by lazy { AuthenticationV6ServiceImpl(httpsClient) }
    val sessionV2Service: SessionV2Service by lazy { SessionV2ServiceImpl(httpsClient) }
    val paymentService: PaymentService by lazy { PaymentServiceImpl(httpsClient) }
    val tokenService: TokenService by lazy { TokenServiceImpl(httpsClient) }
    val tokenPaymentService: TokenPaymentService by lazy { TokenPaymentServiceImpl(httpsClient) }
    val captureService: CaptureService by lazy { CaptureServiceImpl(httpsClient) }
    val cancellationService: CancellationService by lazy { CancellationServiceImpl(httpsClient) }
    val eInvoiceService: EInvoiceService by lazy { EInvoiceServiceImpl(httpsClient) }
    val meezaService: MeezaService by lazy { MeezaServiceImpl(httpsClient) }
    val orderService: OrderService by lazy { OrderServiceImpl(httpsClient) }
    val refundService: RefundService by lazy { RefundServiceImpl(httpsClient) }
    val paymentIntentService: PaymentIntentService by lazy { PaymentIntentServiceImpl(httpsClient) }
    val receiptService: ReceiptService by lazy { ReceiptServiceImpl(httpsClient) }

    // BNPL services

    val valuService: ValuService by lazy { ValuServiceImpl(httpsClient) }
    val shahryService: ShahryService by lazy { ShahryServiceImpl(httpsClient) }
    val souhoolaService: SouhoolaService by lazy { SouhoolaServiceImpl(httpsClient) }
}