package id.co.dif.base_project.presentation.fragment

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.OverviewBinding
import id.co.dif.base_project.presentation.activity.AddCollageActivity
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.viewmodel.OverviewViewModel

class OverviewFragment () : BaseFragment<OverviewViewModel, OverviewBinding>() {
    override val layoutResId = R.layout.overview

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseCompletedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                if (it.data.score == 100) {
                    binding.layoutCompletedProfile.isVisible = false
                }
                binding.completedProfile = it.data
            }
        }

        viewModel.responseBasicInfoList.observe(lifecycleOwner) {
            if (it.status == 200) {
                var info = preferences.myDetailProfile.value
                binding.name.text = it.data.emp_manager.orDefault()
                binding.position.text = basiInfo?.position.orDefault()
                binding.jointeam.text =
                    getString(R.string.since, session?.join_team.orDefault())
                binding.location.text = it.data.address.orDefault()
                binding.phone.text = it.data.phone.orDefault()
                binding.email.text = it.data.email.orDefault()
                binding.birthdate.text = it.data.birthday.orDefault()
                binding.`as`.text = basiInfo?.project_name.orDefault()
                binding.tvDescriptionAboutMyself.text = it.data.about.orDefault()

                val filteredArray = basiInfo?.pgroup_nscluster?.filter { it.isNotBlank() }
                val dataAray = filteredArray?.joinToString(", ")
                binding.areaNscluster.text = dataAray?.trim()

                if (it.data.id != info?.id){
                    binding.lineArea.visibility = View.GONE
                } else
                {
                    binding.lineArea.visibility = View.VISIBLE
                }
            }
        }
        val selectedProfileId = preferences.selectedProfileId.value
        val myProfile = preferences.myDetailProfile.value
        if (selectedProfileId == myProfile?.id) {
            binding.layoutAboutMyself.isVisible = false
        }
        fetchData()
    }

    override fun onResume() {
        super.onResume()
        fetchData()
    }

    private fun fetchData() {
        val id = preferences.selectedProfileId.value
        viewModel.getEducationList(id ?: 0)
        viewModel.getDetailedProfile(id)
        viewModel.getCompletedProfile(id)
    }


}