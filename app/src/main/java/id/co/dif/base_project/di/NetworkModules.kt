package id.co.dif.base_project.di

import android.content.Context
import com.chuckerteam.chucker.api.ChuckerCollector
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.chuckerteam.chucker.api.RetentionManager
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import id.co.dif.base_project.BuildConfig
import id.co.dif.base_project.service.ApiServices
import id.co.dif.base_project.utils.ErrorInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.security.SecureRandom
import java.security.cert.X509Certificate
import java.util.concurrent.TimeUnit
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.TrustManager
import javax.net.ssl.X509TrustManager


fun provideHttpLogingInterceptor(): HttpLoggingInterceptor {
    return HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
}

fun provideChuckerInterceptor(context: Context): ChuckerInterceptor {
    val chuckerCollector = ChuckerCollector(
        context = context,
        showNotification = true,
        retentionPeriod = RetentionManager.Period.ONE_HOUR
    )
    return ChuckerInterceptor.Builder(context)
        .collector(chuckerCollector)
        .createShortcut(true)
        .build()
}

fun provideOkHttpClient(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    chuckerInterceptor: ChuckerInterceptor,
): OkHttpClient {
//    val trustManager = object : X509TrustManager {
//        override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {}
//        override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {}
//        override fun getAcceptedIssuers(): Array<X509Certificate> {
//            return arrayOf()
//        }
//    }
//    val trustAllCerts = arrayOf<TrustManager>(trustManager)
//    val sslContext = SSLContext.getInstance("SSL")
//    sslContext.init(null, trustAllCerts, SecureRandom())
    return OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)
        .addInterceptor(chuckerInterceptor)
        .addInterceptor(httpLoggingInterceptor)
//        .sslSocketFactory(sslContext.socketFactory, trustManager)
//        .hostnameVerifier { hostname, session -> true }
        .build()
}

fun provideGsonBuilder(): Gson? {
    return GsonBuilder().apply {
        setPrettyPrinting()
    }.create()
}

fun provideRetrofit(okHttpClient: OkHttpClient, gsonBuilder: Gson): Retrofit.Builder {
    return Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(gsonBuilder))
        .addConverterFactory(ScalarsConverterFactory.create())
        .addCallAdapterFactory(CoroutineCallAdapterFactory())
        .client(okHttpClient)
}

fun provideTripleEApi(retrofitBuilder: Retrofit.Builder): ApiServices {
    return retrofitBuilder
        .baseUrl(BuildConfig.BASE_URL)
        .build()
        .create(ApiServices::class.java)
}

fun providePicassoDownloader(
    httpLoggingInterceptor: HttpLoggingInterceptor,
    chuckerInterceptor: ChuckerInterceptor,
): OkHttpClient {

    return OkHttpClient.Builder()
        .connectTimeout(3, TimeUnit.MINUTES)
        .readTimeout(3, TimeUnit.MINUTES)
        .writeTimeout(3, TimeUnit.MINUTES)
        .addInterceptor(httpLoggingInterceptor)
        .addInterceptor(ErrorInterceptor())
        .addInterceptor(chuckerInterceptor)
        .build()
}