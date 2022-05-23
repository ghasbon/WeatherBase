package com.example.weatherbase

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.delay

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val viewModel: MainViewModel by viewModels()

    private val jobManager by lazy {
        AsyncJobManager(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        jobManager.add {
            (1..100).forEach {
                delay(500)
                println("I am still active $it")
            }
        }
        jobManager.add(shouldResume = true) {
            (1..100).forEach {
                delay(500)
                println("I am still active and resumable $it")
            }
        }
    }
}
