package id.co.dif.base_project.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.WorkBinding
import id.co.dif.base_project.presentation.activity.AddWorkPlaceActivity
import id.co.dif.base_project.presentation.activity.EditWorkPlaceActivity
import id.co.dif.base_project.presentation.adapter.WorkAdapter
import id.co.dif.base_project.viewmodel.WorkViewModel

class WorkFragment : BaseFragment<WorkViewModel, WorkBinding>() {
    override val layoutResId = R.layout.work
    lateinit var adapter: WorkAdapter
    var viewOnly = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

//        val company = binding.company1
//        val position = binding.position1
//        val fromdate = binding.datefrom1
//        val untildate = binding.untilfrom1
//
//
//
//        val edit = binding.edit
        binding.addWorkplace.isVisible = !viewOnly
        binding.addWorkplace.setOnClickListener {
            startActivity(Intent(requireContext(), AddWorkPlaceActivity::class.java))
        }


//        binding.edit.setOnClickListener {
//            showPopup(edit)
//        }

        adapter = WorkAdapter(
            onClickEdit = {
                preferences.userWork.value=it
                startActivity(Intent(context, EditWorkPlaceActivity::class.java))
            },
            onClickDelete = {
                viewModel.deleteWork(it)
            },
            viewOnly = viewOnly
        )
        binding.rvWork.adapter = adapter

        viewModel.responseWorkList.observe(lifecycleOwner) {
            if (it.status == 200) {
                adapter.data.clear()
                adapter.data.addAll(it.data.list)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.responseDelete.observe(lifecycleOwner) {
            if (it.status == 200) {
                val id = preferences.selectedProfileId.value
                viewModel.getWorkList(id)
                showSuccessMessage(requireContext(), "Successfully delete Work!")
            }
        }
        val id = preferences.selectedProfileId.value
        viewModel.getWorkList(id)

    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.getWorkList(id)
    }
}






