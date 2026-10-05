package com.example

import android.app.Application
import android.util.Log

/**
 * Custom Application class for CommuteIQ.
 * Handles application-level initialization and lifecycle callbacks.
 */
class CommuteIQApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Log.i(TAG, "CommuteIQ Application initialized.")
    }

    companion object {
        private const val TAG = "CommuteIQApp"
    }
}
