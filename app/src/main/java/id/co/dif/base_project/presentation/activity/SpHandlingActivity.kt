package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TabMenuItem
import id.co.dif.base_project.databinding.ActivityReturnFormBinding
import id.co.dif.base_project.presentation.adapter.ViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.ReturnFromFragment
import id.co.dif.base_project.presentation.fragment.ReturnSparePartFragment

class SpHandlingActivity : BaseActivity<BaseViewModel, ActivityReturnFormBinding>() {

    override val layoutResId = R.layout.activity_return_form

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val tabMenuItems = mutableListOf<TabMenuItem>()
        tabMenuItems.add(TabMenuItem(getString(R.string.return_form), ReturnFromFragment()))
        tabMenuItems.add(TabMenuItem(getString(R.string.return_spare_part), ReturnSparePartFragment()))
        binding.viewPager.adapter = ViewPagerAdapter(this, supportFragmentManager, tabMenuItems)
        binding.tabLayout.setupWithViewPager(binding.viewPager)

        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }

    }


}