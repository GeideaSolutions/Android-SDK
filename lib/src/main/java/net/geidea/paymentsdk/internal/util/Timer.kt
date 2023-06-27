package net.geidea.paymentsdk.internal.util

import kotlinx.coroutines.*

internal class Timer(
        private val timeoutMillis: Long,
        private val action: suspend () -> Unit
) {
    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.Default + job)

    private var timerJob: Job? = null

    fun start() {
        timerJob?.let(Job::cancel)
        val timer = createJob()
        this.timerJob = timer
        timer.start()
    }

    fun cancel() {
        checkNotNull(timerJob) { "Must be started first" }
        timerJob?.cancel()
        timerJob = null
    }

    private fun createJob(): Job = scope.launch(Dispatchers.Default) {
        delay(timeoutMillis)
        action()
    }
}