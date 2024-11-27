package id.co.dif.base_project.utils

import okhttp3.Interceptor
import okhttp3.Response

class ErrorInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val response = chain.proceed(request)

        // Check if the HTTP status code is 404 (Not Found)
        if (response.code in StatusCode.ERROR) {
            response.close() // Close the response body to free resources
            throw Exception("HTTP 404 Not Found") // Manually throw an exception to indicate an error
        }

        return response
    }
}