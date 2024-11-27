package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.databinding.ViewDataBinding
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.FragmentSparePartBinding
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.viewmodel.SparePartViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class SparePartFragment(
    override val layoutResId: Int = R.layout.fragment_spare_part
) : BaseFragment<SparePartViewModel, FragmentSparePartBinding>(), KoinComponent {

    private val sparePartListFragment: SparePartListFragment = SparePartListFragment()
    private val sparePartRequest: SparePartListFragment = SparePartListFragment()
    private lateinit var viewPagerAdapter: TitledViewPagerAdapter
    private val pageTitles = listOf(
        R.string.request,
        R.string.list,
    )
    private val fragments =
        arrayListOf<BaseFragment<out BaseViewModel, out ViewDataBinding>>(
            sparePartRequest,
            sparePartListFragment
        )

    var shouldGoToTimeline = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)
        viewPagerAdapter.replaceAll(
            fragments, pageTitles.map { id -> getString(id) }
        )
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = fragments.count()
        binding.tabLayout.setupWithViewPager(binding.viewPager)
        binding.viewPager.isSaveEnabled = false

    }
}