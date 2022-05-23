package com.example.weatherbase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class CancellableJob(
    private val scope: CoroutineScope,
    private val action: suspend CoroutineScope.() -> Unit
) {
    private var job: Job? = null

    fun start() {
        job?.start() ?: run {
            job = scope.launch {
                while(isActive) {
                    action(this)
                }
            }
        }
    }

    fun stop() {
        job?.cancel()
    }
}
