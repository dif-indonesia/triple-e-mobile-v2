package id.co.dif.base_project.presentation.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.EducationBinding
import id.co.dif.base_project.presentation.activity.AddCollageActivity
import id.co.dif.base_project.presentation.activity.EditCollageActivity
import id.co.dif.base_project.presentation.adapter.EducationAdapter
import id.co.dif.base_project.viewmodel.EducationViewModel

class EducationFragment : BaseFragment<EducationViewModel, EducationBinding>() {

    override val layoutResId = R.layout.education
    private lateinit var adapter: EducationAdapter
    var viewOnly = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {


        binding.addCollage.isVisible = !viewOnly
        binding.addCollage.setOnClickListener {
//            requireActivity().run {
            startActivity(Intent(requireContext(), AddCollageActivity::class.java))
////                finish()
//            }
        }

        adapter = EducationAdapter(
            onClickEdit = {
                preferences.userEducation.value=it
                startActivity(Intent(context, EditCollageActivity::class.java))

            },
            onClickDelete = {
                viewModel.deleteEducation(it)
            },
            viewOnly = viewOnly
        )

        binding.rvEducation.adapter = adapter

        viewModel.responseEducationList.observe(lifecycleOwner) {
            if (it.status == 200) {
//                viewModel.educationlist()
                adapter.data.clear()
                adapter.data.addAll(it.data.list)
                adapter.notifyDataSetChanged()
            }
        }

        viewModel.responseDelete.observe(lifecycleOwner) {
            if (it.status == 200) {
                val id = preferences.selectedProfileId.value
                viewModel.getEducationList(id)
                showSuccessMessage(requireContext(), "Succesfully delete Education!")
            }
        }
        val id = preferences.selectedProfileId.value
        viewModel.getEducationList(id)

    }

    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.getEducationList(id)
    }
    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }


}
