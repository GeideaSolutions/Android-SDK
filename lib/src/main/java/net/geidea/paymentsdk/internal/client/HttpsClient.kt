package net.geidea.paymentsdk.internal.client

import android.util.Base64
import kotlinx.coroutines.withContext
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import net.geidea.paymentsdk.GeideaPaymentSdk
import net.geidea.paymentsdk.internal.util.DispatchersProvider
import net.geidea.paymentsdk.internal.util.Logger.logv
import net.geidea.paymentsdk.internal.util.debugAndReleaseLogd
import net.geidea.paymentsdk.internal.util.encodeToBase64
import net.geidea.paymentsdk.internal.util.releaseLogd
import net.geidea.paymentsdk.model.common.GeideaResponse
import net.geidea.paymentsdk.model.common.LocalizableRequest
import net.geidea.paymentsdk.model.error.GenericError
import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.*
import javax.net.ssl.HttpsURLConnection

@ExperimentalSerializationApi
internal open class HttpsClient(
    private val json: Json,
    private val defaultHeaders: Map<String, String> = JSON_HEADERS,
    private val dispatchersProvider: DispatchersProvider,
    private val connectTimeout: Int = 60_000,
    private val readTimeout: Int = 60_000,
) {
    companion object {
        protected const val CONTENT_TYPE = "Content-Type"
        protected const val ACCEPT = "Accept"
        protected const val MEDIA_TYPE_JSON = "application/json"
        protected const val MEDIA_TYPE_FORM_DATA = "multipart/form-data"

        // Header names
        protected const val HEADER_X_LANGUAGE = "X-Language"

        //Used in the response header to provide failure id
        protected const val HEADER_X_CORRELATION_ID = "X-Correlation-Id"

        @JvmStatic
        val JSON_HEADERS: Map<String, String> =
            mapOf(CONTENT_TYPE to MEDIA_TYPE_JSON, ACCEPT to MEDIA_TYPE_JSON)

        @JvmStatic
        fun isJsonMime(mime: String?): Boolean {
            val jsonMime = "(?i)^(application/json|[^;/ \t]+/[^;/ \t]+[+]json)[ \t]*(;.*)?$"
            return mime != null && (mime.matches(jsonMime.toRegex()) || mime == "*/*")
        }
    }

    // Value is always up-to-date or null
    var correlationId: String? = null
        private set

    protected inline fun <reified T> HttpURLConnection.writeRequestBody(
        content: T,
        mediaType: String = MEDIA_TYPE_JSON
    ) {
        return when {
            content is File -> {
                throw UnsupportedOperationException()
            }
            mediaType == MEDIA_TYPE_FORM_DATA -> {
                throw UnsupportedOperationException()
            }
            mediaType == MEDIA_TYPE_JSON -> {
                val bodyText: String = json.encodeToString(content)
                logv("HTTPS request: $bodyText")
                outputStream.bufferedWriter().use { out -> out.write(bodyText) }
            }
            else -> error("Unsupported media type $mediaType")
        }
    }

    protected inline fun <reified T> HttpURLConnection.readResponseBody(): T {
        val isHttpSuccess = this.responseCode in 200..299
        check(isHttpSuccess) { "Http success response expected" }

        return when {
            T::class.java == File::class.java -> {
                downloadFileFromResponse(this.inputStream) as T
            }
            T::class == Unit::class -> {
                Unit as T
            }
            else -> {
                val contentType = this.getHeaderField("Content-Type")
                    ?: MEDIA_TYPE_JSON

                when {
                    isJsonMime(contentType) -> {
                        val bodyText: String =
                            inputStream.bufferedReader().use(BufferedReader::readText)
                        logv("HTTPS response: $bodyText")
                        json.decodeFromString<T>(bodyText).apply {
                            if (this is GeideaResponse) {
                                debugAndReleaseLogd("HTTPS detailedResponseCode: $responseCode.$detailedResponseCode")
                            }
                        }
                    }
                    else -> error("Unsupported media type $contentType")
                }
            }
        }
    }

    protected inline fun <reified E> HttpURLConnection.readErrorBody(): E? {
        val bodyText: String = this.errorStream.bufferedReader().use(BufferedReader::readText)
        logv("HTTPS response: $bodyText")
        return if (bodyText.isEmpty()) {
            null
        } else {
            json.decodeFromString<E>(bodyText).apply {
                if (this is GenericError) {
                    debugAndReleaseLogd("HTTPS detailedResponseCode: $responseCode.$detailedResponseCode, traceId: $traceId")
                }
            }
        }
    }

    @Throws(IOException::class)
    protected inline fun <reified T, reified R> request(
        requestConfig: RequestConfig,
        body: T? = null
    ): Response<R> {

        val url: URL = requestConfig.toUrl()

        val requestHeaders: MutableMap<String, String> = (defaultHeaders + requestConfig.headers).toMutableMap()
        if (requestHeaders[CONTENT_TYPE].isNullOrEmpty()) {
            error("Missing Content-Type header. It is required.")
        }
        if (requestHeaders[ACCEPT].isNullOrEmpty()) {
            error("Missing Accept header. It is required.")
        }

        // TODO: support multiple contentType,accept options here.
        val contentType =
            (requestHeaders[CONTENT_TYPE] as String).substringBefore(";").lowercase(Locale.US)
        val accept = (requestHeaders[ACCEPT] as String).substringBefore(";").lowercase(Locale.US)

        if (body is LocalizableRequest && body.language != null) {
            requestHeaders[HEADER_X_LANGUAGE] = body.language!!
        }

        val connection: HttpsURLConnection = createUrlConnection(url)
        try {
            connection.requestMethod = requestConfig.method.toString()
            connection.doOutput = body != null
            connection.readTimeout = readTimeout
            connection.connectTimeout = connectTimeout
            requestHeaders.forEach { header ->
                connection.setRequestProperty(header.key, header.value)
            }
            // TODO cookies?
            // TODO SSL/TLS
            val username: String = GeideaPaymentSdk.merchantKey
            val password: String = GeideaPaymentSdk.merchantPassword
            val authorization = "$username:$password".encodeToBase64(Base64.NO_WRAP)
            connection.setRequestProperty("Authorization", "Basic $authorization")

            val methodAndUrl = "HTTPS ${requestConfig.method.name}: ${connection.url}"
            debugAndReleaseLogd(methodAndUrl)

            requestHeaders.forEach { (headerKey, headerValue) ->
                logv("HTTPS request header: $headerKey: $headerValue")
            }
            connection.connect()

            when (requestConfig.method) {
                RequestMethod.HEAD -> {}
                RequestMethod.GET -> {}
                RequestMethod.DELETE,
                RequestMethod.PUT,
                RequestMethod.POST -> {
                    if (body != null) {
                        connection.writeRequestBody(body, contentType)
                    }
                }
            }

            val responseCodeLogMessage = "HTTPS response code: ${connection.responseCode}"
            debugAndReleaseLogd(responseCodeLogMessage)

            connection.headerFields.forEach { (headerKey, headerValue) ->
                logv("HTTPS response header: $headerKey: $headerValue")
            }

            correlationId = connection.headerFields.entries
                .firstOrNull { it.key.equals(HEADER_X_CORRELATION_ID, ignoreCase = true) }
                ?.value
                ?.firstOrNull()

            if (correlationId != null) {
                releaseLogd("HTTPS response header: $HEADER_X_CORRELATION_ID: $correlationId")
            }

            return when (connection.responseCode) {
                in 100..199 -> Informational(
                    connection.responseMessage,
                    connection.responseCode,
                    connection.headerFields
                )
                in 200..299 -> Success(
                    connection.readResponseBody(),
                    connection.responseCode,
                    connection.headerFields
                )
                in 300..399 -> Redirection(
                    connection.responseCode,
                    connection.headerFields
                )
                in 400..499 -> {
                    val error: GenericError? = connection.readErrorBody()
                    ClientError(
                        error,
                        connection.responseCode,
                        connection.headerFields
                    )
                }
                else -> {
                    val error: GenericError? = connection.readErrorBody()
                    ServerError(
                        connection.responseMessage,
                        error,
                        connection.responseCode,
                        connection.headerFields
                    )
                }
            }
        } finally {
            connection.disconnect()
        }
    }

    open fun createUrlConnection(url: URL): HttpsURLConnection {
        return url.openConnection() as HttpsURLConnection
    }

    suspend inline fun <reified R> get(
        path: String,
        headers: Map<String, String> = emptyMap(),
        query: Map<String, List<String>> = emptyMap()
    ): Response<R> {
        return withContext(dispatchersProvider.io) {
            checkStartsWithSlash(path)
            val requestConfig = RequestConfig(
                method = RequestMethod.GET,
                path = path,
                headers = headers,
                query = query
            )
            request<Any, R>(requestConfig)
        }
    }

    suspend inline fun <reified T : Any, reified R : Any> post(
        path: String,
        body: T? = null,
        headers: Map<String, String> = emptyMap(),
        query: Map<String, List<String>> = emptyMap()
    ): Response<R> {
        return withContext(dispatchersProvider.io) {
            checkStartsWithSlash(path)
            val requestConfig = RequestConfig(
                method = RequestMethod.POST,
                path = path,
                headers = headers,
                query = query
            )
            localizeRequestIfNeeded(body)
            request<T, R>(requestConfig, body)
        }
    }

    suspend inline fun <reified T : Any, reified R : Any> put(
        path: String,
        body: T? = null,
        headers: Map<String, String> = emptyMap(),
        query: Map<String, List<String>> = emptyMap()
    ): Response<R> {
        return withContext(dispatchersProvider.io) {
            checkStartsWithSlash(path)
            val requestConfig = RequestConfig(
                method = RequestMethod.PUT,
                path = path,
                headers = headers,
                query = query
            )
            localizeRequestIfNeeded(body)
            request<T, R>(requestConfig, body)
        }
    }

    suspend inline fun <reified T : Any, reified R> delete(
        path: String,
        body: T? = null,
        headers: Map<String, String> = emptyMap(),
        query: Map<String, List<String>> = emptyMap()
    ): Response<R> {
        return withContext(dispatchersProvider.io) {
            checkStartsWithSlash(path)
            val requestConfig = RequestConfig(
                method = RequestMethod.DELETE,
                path = path,
                headers = headers,
                query = query
            )
            localizeRequestIfNeeded(body)
            request<T, R>(requestConfig, body)
        }
    }

    protected fun RequestConfig.toUrl(): URL {
        val urlBuilder = StringBuilder(GeideaPaymentSdk.serverEnvironment.apiBaseUrl)
        urlBuilder.append(this.path)

        if (this.query.isNotEmpty()) {
            urlBuilder.append("?")
        }

        this.query.forEach { query ->
            query.value.forEach { queryValue ->
                val encodedQueryValue = queryValue.encodeToBase64(Base64.NO_WRAP or Base64.URL_SAFE)
                urlBuilder.append("${query.key}=${encodedQueryValue}")
                urlBuilder.append("&")
            }
        }

        return URL(urlBuilder.toString())
    }

    // Keep public to allow inlining, otherwise it crashes at run time
    fun checkStartsWithSlash(path: String) {
        check(path.startsWith('/')) { "path should start with '/' slash" }
    }

    @Throws(IOException::class)
    fun downloadFileFromResponse(inputStream: InputStream): File {
        throw UnsupportedOperationException()
    }

    private fun localizeRequestIfNeeded(request: Any?) {
        val language = GeideaPaymentSdk.language
        if (language != null && request is LocalizableRequest) {
            request.language = language.code
        }
    }
}