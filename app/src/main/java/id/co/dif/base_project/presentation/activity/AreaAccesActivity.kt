package id.co.dif.base_project.presentation.activity

import android.opengl.Visibility
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityAreaAccesBinding
import id.co.dif.base_project.presentation.adapter.RegionalAccesAdapter
import id.co.dif.base_project.viewmodel.OverviewViewModel

class AreaAccesActivity : BaseActivity<AreaAccesViewModel, ActivityAreaAccesBinding>() {
    override val layoutResId = R.layout.activity_area_acces
    private lateinit var areaAccesAdapter: RegionalAccesAdapter
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        areaAccesAdapter = RegionalAccesAdapter()

        binding.rootLayout.onActionButtonClicked = {

            val regional = mutableListOf<String>()
            val nscluster = mutableListOf<String>()

            areaAccesAdapter.selectedItem.forEach { r ->
                regional.add(r.regional!!)
                r.nscluster?.forEach { c ->
                    nscluster.add(c)
                }
            }

            val param = hashMapOf(
                "regional" to regional,
                "nscluster" to nscluster
            )

            viewModel.updateMyAreaAcces(
                param
            )
        }

        regionalSumbagsel()
        regionalSumbagteng()

//        viewModel.responseMyAreaAcces.observe(lifecycleOwner) {
//            if (it.status == 200) {
//
//
//            }
//        }
//
//
//        viewModel.responseUpdateMyAreaAcces.observe(lifecycleOwner) {
//            if (it.status == 200) {
//                showToast(it.message)
//                finish()
//            }
//        }
//
//        viewModel.getMyAreaAcces()

        binding.regionaLSumbagsel.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.lineSumbagsel.visibility = View.VISIBLE
            } else {
                binding.lineSumbagsel.visibility = View.GONE
            }
        }

        binding.regionalSumbagteng.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.lineSumbagteng.visibility = View.VISIBLE
            } else {
                binding.lineSumbagteng.visibility = View.GONE
            }
        }

        binding.regionalSumbagut.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                binding.lineSumbagut.visibility = View.VISIBLE
            } else {
                binding.lineSumbagut.visibility = View.GONE
            }
        }

    }

    private fun regionalSumbagsel (){
        binding.regionaLSumbagsel.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("SUMBAGSEL") == true
        binding.nsNspalembang.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS PALEMBANG") == true
        binding.nsSumbagselNsbengkulu.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS BENGKULU") == true
        binding.nsNssumbagselNsjambi.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS JAMBI") == true
        binding.nsNslampung.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS LAMPUNG") == true
        binding.nsNspangkalpinang.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS PANGKAL PINANG") == true
    }

    private fun regionalSumbagteng (){
        binding.regionalSumbagteng.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("SUMBAGSEL") == true
        binding.nsSumbagtengBatam.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS BATAM") == true
        binding.nsSubagtengDumai.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS DUMAI") == true
        binding.nsSumbagtengPadang.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS PADANG") == true
        binding.nsSumbagtengPekanbaru.isChecked = basiInfo?.pgroup_regional != null && basiInfo!!.pgroup_regional?.contains("NS PEKAN BARU") == true

    }

}