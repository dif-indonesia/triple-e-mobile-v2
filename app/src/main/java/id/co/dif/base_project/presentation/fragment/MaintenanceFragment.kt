package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import android.view.View
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.FragmentMaintenanceBinding

class MaintenanceFragment : BaseFragment<BaseViewModel, FragmentMaintenanceBinding>() {

    override val layoutResId = R.layout.fragment_maintenance

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

    }
}