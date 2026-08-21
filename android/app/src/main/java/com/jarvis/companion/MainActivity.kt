package com.jarvis.companion

import android.app.Activity
import android.os.Bundle
import android.util.Log

class MainActivity : Activity() {
    companion object {
        private const val TAG = "JARVISCompanion"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i(TAG, "JARVIS Companion service starting...")
    }
}
