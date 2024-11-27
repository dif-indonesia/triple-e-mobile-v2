package id.co.dif.base_project.di

import android.content.Context
import com.google.android.gms.location.LocationServices
import id.co.dif.base_project.service.DefaultLocationClient
import id.co.dif.base_project.service.LocationClient
import id.co.dif.base_project.utils.ColorGenerator

fun provideColorGenerator(): ColorGenerator? {
    return ColorGenerator.DEFAULT
}

fun provideLocationClient(context: Context): LocationClient {
    return DefaultLocationClient(
        context,
        LocationServices.getFusedLocationProviderClient(context)
    )
}