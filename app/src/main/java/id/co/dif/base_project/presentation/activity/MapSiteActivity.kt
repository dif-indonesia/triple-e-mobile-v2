package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.TabMenuItem
import id.co.dif.base_project.databinding.ActivityMapSiteBinding
import id.co.dif.base_project.presentation.adapter.ViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.HistoryFragment
import id.co.dif.base_project.presentation.fragment.SiteInfoFragment
import id.co.dif.base_project.viewmodel.MapSiteViewModel

class MapSiteActivity : BaseActivity<MapSiteViewModel, ActivityMapSiteBinding>() {
    override val layoutResId = R.layout.activity_map_site


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        viewModel.responseaGetSiteByid.observe(lifecycleOwner){
            if(it.status == 200) {
                preferences.siteData.value= it.data
                val tabMenuItems = mutableListOf<TabMenuItem>()
                tabMenuItems.add(TabMenuItem(getString(R.string.site_info), SiteInfoFragment()))
                tabMenuItems.add(TabMenuItem(getString(R.string.history),HistoryFragment()))

                binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
                binding.tabLayout.setupWithViewPager(binding.viewPager)
            }
        }

        viewModel.getSiteById(preferences.selectedSite.value?.site_id)

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

    }
}