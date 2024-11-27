package id.co.dif.base_project.presentation.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import id.co.dif.base_project.presentation.activity.EditSparePartActivity
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.databinding.FragmentSparePartListBinding
import id.co.dif.base_project.presentation.activity.RequestReturnSparePartActivity
import id.co.dif.base_project.presentation.adapter.SparePartAdapter
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.utils.LinearSpacingItemDecoration
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.viewmodel.SparePartViewModel

class SparePartListFragment : BaseFragment<SparePartViewModel, FragmentSparePartListBinding>() {
    override val layoutResId: Int = R.layout.fragment_spare_part_list
    var adapter = SparePartAdapter()
    var onSubmit: (status: Boolean) -> Unit = { _ -> }
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        viewModel.responseGetListSparePart.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                populateRvSparePart(it.data.list.toMutableList())
            } else {
                viewModel.errorWhileLoadingData = true
            }
            binding.layoutOnRefresh.isRefreshing = false
            updateListState()
        }
        binding.rvSparepart.addItemDecoration(
            LinearSpacingItemDecoration(
                top = 14,
                bottomMost = 78,
                topMost = 14,
                left = 14,
                right = 14
            )
        )

        binding.tvRequestSparePart.setOnClickListener {
            val intent = Intent(requireContext(), RequestReturnSparePartActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.btnEmptyListAdd.setOnClickListener {
            val intent = Intent(requireContext(), RequestReturnSparePartActivity::class.java)
            startActivityForResult(intent, 1)
        }
        binding.btnErrorList.setOnClickListener {
            refreshList()
        }
        viewModel.responseDeleteSparePart.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                currentActivity.showToast("Spare part successfully deleted!")
                refreshList()
                onSubmit(true)
            } else {
                currentActivity.showToast("Something went wrong!")
            }
        }
        refreshList()

        binding.layoutOnRefresh.setOnRefreshListener {
            refreshList()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                onSubmit(true)
                refreshList()
            }
        }
    }

    private fun refreshList() {
        val ticketDetails = preferences.ticketDetails.value
        ticketDetails?.let {
            binding.layoutOnRefresh.isRefreshing = true
            viewModel.getListSparePart(it.tic_id)
        }

    }

    private fun updateListState() {
        val dataIsEmpty = adapter.data.isEmpty()
        val shouldShowEmptyTicketView = dataIsEmpty && !viewModel.errorWhileLoadingData
        val shouldShowErrorTicketView =
            !shouldShowEmptyTicketView && viewModel.errorWhileLoadingData

        binding.layoutEmptyState.isVisible = shouldShowEmptyTicketView
        binding.layoutErrorState.isVisible = shouldShowErrorTicketView
    }

    private fun populateRvSparePart(list: MutableList<SparePart>) {
        val ticketDetails = preferences.ticketDetails.value
        adapter = SparePartAdapter().also { adapter ->
            adapter.data = list
            adapter.onMoreVertClicked = { sparePart ->
                ticketDetails?.let {
                    showPickerDialog(sparePart, ticid = it.tic_id.toString())
                }
            }
        }
        binding.rvSparepart.adapter = adapter
        viewModel.errorWhileLoadingData = false
    }

    fun showPickerDialog(sparePart: SparePart, ticid: String) {
        val options = arrayListOf("Delete Spare Part", "Edit Spare Part")
        val dialog = PickerDialog(
            options,
            onSelectedItem = { dialog, position ->
                when (dialog) {
                    0 -> {
                        viewModel.deleteSparePart(
                            spreq_id = sparePart.spreqId.toString(),
                            tic_id = ticid
                        )
                    }

                    1 -> {
                        val intent = Intent(requireContext(), EditSparePartActivity::class.java)
                        intent.putExtra("TIC_ID", ticid)
                        preferences.sparePart.value = sparePart
                        refreshList()
                        startActivityForResult(intent, 1)
                    }
                }
            }
        )
        dialog.show(childFragmentManager, "Action")
    }



}