package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.FragmentReturnSparePartBinding
import id.co.dif.base_project.presentation.adapter.ReturnSparePartAdapter


class ReturnSparePartFragment : BaseFragment<BaseViewModel, FragmentReturnSparePartBinding>() {
    override val layoutResId = R.layout.fragment_return_spare_part

    lateinit var adapter: ReturnSparePartAdapter

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        adapter = ReturnSparePartAdapter()
        binding.rvSparepart.adapter = adapter

    }

}