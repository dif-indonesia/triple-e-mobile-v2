package id.co.dif.base_project.presentation.activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.MarkerItem
import id.co.dif.base_project.databinding.ActivityMapsBinding
import id.co.dif.base_project.presentation.dialog.MarkerInfoDialog
import id.co.dif.base_project.viewmodel.MapsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MapsActivity : BaseActivity<MapsViewModel, ActivityMapsBinding>(), OnMapReadyCallback,
    OnMarkerClickListener {

    override val layoutResId = R.layout.activity_maps
    private lateinit var map: GoogleMap

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)


        viewModel.responseListLocation.observe(lifecycleOwner) {
            if (it.status == 200) {
                binding.viewLoading.isVisible = false
                map.clear()
                val builder = LatLngBounds.Builder()
                it.data?.list?.ttMapAll?.forEach { location ->
                    addMarker(location)
                    builder.include(LatLng(location.latitude, location.longtitude))
                }
                val bounds = builder.build()
                val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 100)
                map.animateCamera(cameraUpdate)
            }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(-2.548926,118.0148634), 3.6f
        )
        map.animateCamera(cameraUpdate)

        map.setOnMarkerClickListener(this)

        viewModel.getListLocation()
    }

    private fun addMarker(location: MarkerItem) {
        val bitmap = BitmapFactory.decodeResource(resources, R.drawable.ic_pin)
        val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 100, 100, false)
        val icon = BitmapDescriptorFactory.fromBitmap(scaledBitmap)
        val latLng = LatLng(location.latitude, location.longtitude)
        val marker = map.addMarker(MarkerOptions().position(latLng).icon(icon))
        if (marker != null) {
            marker.tag = location.tic_id
        }
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        lifecycleScope.launch {
            val location = viewModel.responseListLocation.value?.data?.list?.ttMapAll?.first { it.tic_id == marker.tag }
            val cameraUpdate = CameraUpdateFactory.newLatLng(marker.position)
            map.animateCamera(cameraUpdate)
            delay(1000)
            MarkerInfoDialog.newInstance(location).show(
                supportFragmentManager,
                MarkerInfoDialog::class.java.name
            )
        }
        return true
    }
}