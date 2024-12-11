package com.example.bolatix.data.remote.network

import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

class RetryInterceptor(private val maxRetry: Int) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        var attempt = 0
        var response: Response? = null
        var success = false
        while (attempt < maxRetry && !success) {
            response?.close()
            try {
                response = chain.proceed(chain.request())
                success = response.isSuccessful
            } catch (e: Exception) {
                if (attempt >= maxRetry - 1) {
                    throw e
                }
            }
            attempt++
        }
        return response ?: throw IOException("Failed to execute request after $maxRetry retries")
    }
}

