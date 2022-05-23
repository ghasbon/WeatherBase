package com.example.weatherbase

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope

class AsyncJobManager(private val lifecycleOwner: LifecycleOwner) {

    private val asyncJobs = mutableListOf<ResumableJob>()

    private val lifecycleObserver = LifecycleEventObserver { _, event ->
        if (event == Lifecycle.Event.ON_DESTROY) {
            cancelAllJobs()
        } else if (event == Lifecycle.Event.ON_RESUME) {
            restartResumableJobs()
        } else {
            val stopOnEventJobs = asyncJobs.filter {
                it.stopAt == event
            }

            val stopOnEventResumableJobs = stopOnEventJobs.filter {
                it.shouldResume
            }

            val stopOnEventNonResumableJobs = stopOnEventJobs - stopOnEventResumableJobs.toSet()

            stopOnEventNonResumableJobs.forEach {
                it.myJob.stop()
                asyncJobs.remove(it)
            }

            stopOnEventResumableJobs.forEach {
                val resumableJob = ResumableJob(it.myJob.copy(), it.stopAt, it.shouldResume)
                it.myJob.stop()
                asyncJobs.remove(it)
                asyncJobs.add(resumableJob)
            }
        }
    }

    init {
        lifecycleOwner.lifecycle.addObserver(lifecycleObserver)
    }

    fun add(
        stopAt: Lifecycle.Event = Lifecycle.Event.ON_STOP,
        shouldResume: Boolean = false,
        jobBlock: suspend CoroutineScope.() -> Unit
    ) {
        val cancellableJob = CancellableJob.create(lifecycleOwner.lifecycleScope, jobBlock)
        val resumableJob = ResumableJob(cancellableJob, stopAt, shouldResume)
        asyncJobs.add(resumableJob)
        resumableJob.myJob.start()
    }

    private fun cancelAllJobs() {
        asyncJobs.forEach {
            it.myJob.stop()
        }
        asyncJobs.clear()
    }

    private fun restartResumableJobs() {
        asyncJobs.filter {
            it.shouldResume
        }.forEach {
            it.myJob.start()
        }
    }

    private data class ResumableJob(
        val myJob: CancellableJob,
        val stopAt: Lifecycle.Event,
        val shouldResume: Boolean
    )
}
