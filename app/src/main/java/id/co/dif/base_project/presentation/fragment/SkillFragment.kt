package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.FragmentSkillBinding
import id.co.dif.base_project.viewmodel.SkillViewModel

class SkillFragment : BaseFragment<SkillViewModel, FragmentSkillBinding>() {
    override val layoutResId = R.layout.fragment_skill

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.responseSkillList.observe(lifecycleOwner) {
            if (it.status == 200) {
                val installationSkill = (it?.data?.skill?.Installation ?: 0f).coerceAtMost(100f)
                binding.skillInstalation.setValue(installationSkill)
                val commissioningSkill = (it?.data?.skill?.Commissioning ?: 0f).coerceAtMost(100f)
                binding.skillCommisionning.setValue(commissioningSkill)
                val integrationSkill = (it?.data?.skill?.Integration ?: 0f).coerceAtMost(100f)
                binding.skillIntegration.setValue(integrationSkill)
                val projectSkill = (it?.data?.skill?.Project ?: 0f).coerceAtMost(100f)
                binding.skillProject.setValue(projectSkill)
                val businessSkill = (it?.data?.skill?.Business ?: 0f).coerceAtMost(100f)
                binding.skillBusiness.setValue(businessSkill)
                preferences.skill.value = it.data.skill
            }
        }
    }

    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.skilllist(id)
    }


}