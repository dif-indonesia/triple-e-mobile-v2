package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.FragmentSiteBinding
import id.co.dif.base_project.presentation.activity.SiteViewModel

class SiteFragment : BaseFragment<SiteViewModel, FragmentSiteBinding>() {
    override val layoutResId = R.layout.fragment_site
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        setupData()
    }


    fun setupData(){
        val data = preferences.ticketDetails.value
        binding.siteInfo = data?.site_info
    }
    override fun refresh() {
        setupData()
    }
}