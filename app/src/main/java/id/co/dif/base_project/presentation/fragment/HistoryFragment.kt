package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.FragmentHistoryBinding
import id.co.dif.base_project.presentation.adapter.HistoryAdapter
import id.co.dif.base_project.viewmodel.SiteInfoViewModel


class HistoryFragment : BaseFragment<SiteInfoViewModel, FragmentHistoryBinding>() {
    override val layoutResId = R.layout.fragment_history

   lateinit var adapter: HistoryAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = HistoryAdapter()
        binding.rvHistory.adapter = adapter

        val data = preferences.siteData.value
//        viewModel.responseSiteInfo.observe(lifecycleOwner){
        adapter.data.clear()
        data?.site_history?.let { adapter.data.addAll(it) }
        adapter.also {
            it.notifyItemRangeChanged(0, it.itemCount)
        }
        binding.layoutEmptyState.isVisible = adapter.data.isEmpty()

//        }

    }

}