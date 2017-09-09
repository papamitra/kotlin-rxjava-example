package com.example.insight.rxjavaexample

import android.util.Log
import io.reactivex.functions.Consumer

/**
 * Created by insight on 17/09/09.
 */

class ErrorHandler() : Consumer<Throwable> {
    override fun accept(t: Throwable) {
        Log.e("APP", "Error on " + Thread.currentThread().name + ":", t)
    }

    companion object {
        val INSTANCE = ErrorHandler()

        fun get(): ErrorHandler {
            return INSTANCE
        }
    }
}
