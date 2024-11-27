package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import android.util.Log
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.AccountSettings
import id.co.dif.base_project.databinding.ActivityAccountSettingBinding
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.bool
import id.co.dif.base_project.utils.int
import id.co.dif.base_project.viewmodel.AccountSettingViewModel

class AccountSettingActivity :
    BaseActivity<AccountSettingViewModel, ActivityAccountSettingBinding>() {
    override val layoutResId = R.layout.activity_account_setting
    var newSettings: AccountSettings? = preferences.myDetailProfile.value?.accountSettings

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        updateSwitches()
        binding.swtCashAdvanceApprovedOrRejected.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtCashAdvanceApprovedOrRejected = isChecked)
        }
        binding.swtExpenseClaimPeriodPaid.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtExpenseClaimPeriodPaid = isChecked)
        }
        binding.swtAnyUpdatedAbout.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtAnyUpdatedAbout = isChecked)
        }
        binding.swtSecondEmailToReceive.setOnCheckedChangeListener { _, isChecked ->
            updateSettings(swtSecondEmailToReceive = isChecked)
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
            binding.swtCashAdvanceApprovedOrRejected.isChecked = it.swtCashAdvanceApprovedOrRejected.bool
            binding.swtExpenseClaimPeriodPaid.isChecked = it.swtExpenseClaimPeriodPaid.bool
            binding.swtAnyUpdatedAbout.isChecked = it.swtAnyUpdatedAbout.bool
            binding.swtSecondEmailToReceive.isChecked = it.swtSecondEmailToReceive.bool
        }
    }

    private fun updateSettings(
        swtCashAdvanceApprovedOrRejected: Boolean? = null,
        swtExpenseClaimPeriodPaid: Boolean? = null,
        swtAnyUpdatedAbout: Boolean? = null,
        swtSecondEmailToReceive: Boolean? = null
    ) {
        val newSettings = (preferences.myDetailProfile.value?.accountSettings ?: AccountSettings()).copy()
        swtCashAdvanceApprovedOrRejected?.let {
            newSettings.swtCashAdvanceApprovedOrRejected = it.int
        }
        swtExpenseClaimPeriodPaid?.let {
            newSettings.swtExpenseClaimPeriodPaid = it.int
        }
        swtAnyUpdatedAbout?.let {
            newSettings.swtAnyUpdatedAbout = it.int
        }
        swtSecondEmailToReceive?.let {
            newSettings.swtSecondEmailToReceive = it.int
        }
        viewModel.updateAccountSettings(newSettings)
    }


}