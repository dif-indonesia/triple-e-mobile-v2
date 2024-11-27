package id.co.dif.base_project.presentation.dialog

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.databinding.ViewDataBinding
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.databinding.DialogSelectEngineerBinding
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.SelectEngineerItemFragment
import id.co.dif.base_project.utils.log

/***
 * Created by kikiprayudi
 * on Wednesday, 01/03/23 04:28
 *
 */


class SelectEngineerDialog(private val technicians: List<Location>, val listener: Listener, val pingLocation: (Location)->Unit) :
    BaseBottomSheetDialog<BaseViewModel, DialogSelectEngineerBinding, Any?>() {

    override val layoutResId = R.layout.dialog_select_engineer

    companion object {
        fun newInstance(technicians: List<Location>, listener: Listener, pingLocation: (Location)->Unit) =
            SelectEngineerDialog(technicians, listener, pingLocation)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        val viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)

        val fragments = technicians.map {
            SelectEngineerItemFragment(it)
        }

        fragments.forEachIndexed { index, fragment ->
            fragment.onSelectEngineerClicked = { location ->
                "selected".log()
                dialog?.dismiss()
                listener.onSelectedEngineer(location)
            }
            fragment.onClickPing = {
                dialog?.dismiss()
                listener.pingLocation(it)
            }
            fragment.shouldShowArrow = index < fragments.lastIndex
        }

        val arrayListFragments =
            arrayListOf<BaseFragment<out BaseViewModel, out ViewDataBinding>>().also {
                it.addAll(fragments)
            }
        val titles = technicians.map { it.name.toString() }

        viewPagerAdapter.replaceAll(arrayListFragments, titles)
        binding.viewPager.adapter = viewPagerAdapter
//
    }

    interface Listener {
        fun onSelectedEngineer(location: Location?)
        fun pingLocation(engineer : Location)
    }
}