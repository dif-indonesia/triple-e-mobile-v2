package id.co.dif.base_project.presentation.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.ActivityAreaAccesTeleglobalBinding
import id.co.dif.base_project.presentation.adapter.RegionalAccesAdapter
import id.co.dif.base_project.viewmodel.OverviewViewModel

class AreaAccesTeleglobalActivity :
    BaseActivity<AreaAccesViewModel, ActivityAreaAccesTeleglobalBinding>() {
    override val layoutResId = R.layout.activity_area_acces_teleglobal
    private lateinit var areaAccesAdapter: RegionalAccesAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        areaAccesAdapter = RegionalAccesAdapter()
        binding.recyclerView.adapter = areaAccesAdapter

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

        viewModel.responseMyAreaAcces.observe(lifecycleOwner) {
            if (it.status == 200) {
                areaAccesAdapter.selectedItem = it.data.list.toMutableList()
                viewModel.getAreaAcces()
            }
            else{
                showToast("Something went wrong !")
            }
        }

        viewModel.responseAreaAcces.observe(lifecycleOwner) {
            if (it.status == 200) {
                if (it.data.list.isNullOrEmpty()){
                    binding.tvNoAreaAccesAlert.visibility = View.VISIBLE
                }
                areaAccesAdapter.data.clear()
                areaAccesAdapter.data.addAll(it.data.list)
                areaAccesAdapter.notifyDataSetChanged()
            }
            else{
                showToast("Something went wrong !")
            }
        }

        viewModel.responseUpdateMyAreaAcces.observe(lifecycleOwner) {
            if (it.status == 200) {
                showToast(it.message)
                finish()
            }
            else{
                showToast("Something went wrong !")
            }
        }

        viewModel.getMyAreaAcces()

    }

//    private fun submitData (){
//        val params = hashMapOf<String, Any?>(
//            "bali" to binding.bali.isChecked,
//            "nusatenggara" to binding.nusatenggara.isChecked
//        )
//    }

}