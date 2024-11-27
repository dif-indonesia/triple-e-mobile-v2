package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import android.view.ContextThemeWrapper
import android.widget.RadioButton
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityProjectBinding

class ProjectActivity : BaseActivity<ProjectViewModel, ActivityProjectBinding>() {
    override val layoutResId = R.layout.activity_project
    var viewModelSelectedButton = false
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseListProject.observe(lifecycleOwner) {
            if (it.status == 200) {
                it.data.list.forEachIndexed { index, any ->
                    val styleResId = R.style.radio_button_project
                    binding.rgProject.addView(RadioButton(ContextThemeWrapper(this, styleResId), null, 0).apply {
                        id = index
                        text = any.toString().trim()
                        var info = preferences.myDetailProfile.value
                        isChecked = any.toString().trim() == info?.project_name
                    })
                }
            } else {
                showToast("Something when wrong !")
            }
        }

        binding.rootLayout.onActionButtonClicked = {
            with(binding.rgProject) {
                val checkedRb = getChildAt(checkedRadioButtonId) as RadioButton
                updateProject(checkedRb.text.toString())
            }
        }

        viewModel.responseUpdateProject.observe(lifecycleOwner) {
            if (it.status == 200) {
                var info = preferences.myDetailProfile.value
                with(binding.rgProject) {
                    val checkedRb = getChildAt(checkedRadioButtonId) as RadioButton
                    info?.project_name = checkedRb.text.toString()
                }
                showToast(it.message)
                finish()
            } else {
                showToast("Something when wrong !")
            }
        }

        viewModel.getListProject()

    }

    private fun updateProject(text: String) {
        viewModel.updateMyProject(
            project = text
        )

    }

}