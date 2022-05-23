package com.example.weatherbase

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

final class CancellableJob private constructor(
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

    fun copy() = CancellableJob(this.scope, this.action)

    companion object {
        fun create(scope: CoroutineScope, action: suspend CoroutineScope.() -> Unit) = CancellableJob(scope, action)
    }
}
