package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.AccountSettings
import id.co.dif.base_project.databinding.ActivityBinding
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.bool
import id.co.dif.base_project.utils.int
import id.co.dif.base_project.viewmodel.AccountSettingViewModel

class Activity : BaseActivity<AccountSettingViewModel, ActivityBinding>() {
    override val layoutResId = R.layout.activity_
    var newSettings: AccountSettings? = preferences.myDetailProfile.value?.accountSettings

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        updateSwitches()

        binding.swtSendMeEmailWhenNewAssignmentCreateTicketForMe.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtSendMeEmailWhenNewAssignmentCreateTicketForMe = isChecked)
        }
        binding.swtNewTroubleTicketApproveOrRejected.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtNewTroubleTicketApproveOrRejected = isChecked)
        }
        binding.swtTroubleTicketClosed.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtTroubleTicketClosed = isChecked)
        }

        viewModel.responseAccountSetting.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                showToast(getString(R.string.settings_successfully_updated))
                viewModel.getDetailedProfile(preferences.myDetailProfile.value?.id)
            } else {
                showToast(getString(R.string.something_went_wrong_cannot_update_settings))
                updateSwitches()
            }
        }
        viewModel.responseBasicInfoList.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                preferences.myDetailProfile.value = it.data
                updateSwitches()
            }
        }
    }

    private fun updateSwitches() {
        val settings = preferences.myDetailProfile.value?.accountSettings
        settings?.let {
            binding.swtSendMeEmailWhenNewAssignmentCreateTicketForMe.isChecked =
                it.swtSendMeEmailWhenNewAssignmentCreateTicketForMe.bool
            binding.swtNewTroubleTicketApproveOrRejected.isChecked =
                it.swtNewTroubleTicketApproveOrRejected.bool
            binding.swtTroubleTicketClosed.isChecked = it.swtTroubleTicketClosed.bool
        }
    }


    private fun updateSettings(
        swtSendMeEmailWhenNewAssignmentCreateTicketForMe: Boolean? = null,
        swtNewTroubleTicketApproveOrRejected: Boolean? = null,
        swtTroubleTicketClosed: Boolean? = null,
    ) {
        val newSettings = (preferences.myDetailProfile.value?.accountSettings ?: AccountSettings()).copy()
        swtSendMeEmailWhenNewAssignmentCreateTicketForMe?.let {
            newSettings.swtSendMeEmailWhenNewAssignmentCreateTicketForMe = it.int
        }
        swtNewTroubleTicketApproveOrRejected?.let {
            newSettings.swtNewTroubleTicketApproveOrRejected = it.int
        }
        swtTroubleTicketClosed?.let {
            newSettings.swtTroubleTicketClosed = it.int
        }
        viewModel.updateAccountSettings(newSettings)
    }

}