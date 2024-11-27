package id.co.dif.base_project.presentation.dialog

import android.content.Intent
import android.os.Bundle
import androidx.databinding.ViewDataBinding
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.databinding.FragmentPopUpMeBinding
import id.co.dif.base_project.presentation.activity.EngineerProfileActivity
import id.co.dif.base_project.presentation.activity.MainActivity
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.PopUpProfileItemFragment
import id.co.dif.base_project.viewmodel.BasicInfoViewModel

class PopUpProfileDialog(
    private val technicians: List<Location>,
    private val directionIsAvailable: Boolean = false,
    val onGetDirectionClicked: (engineer: Location) -> Unit
) :
    BaseBottomSheetDialog<BaseViewModel, FragmentPopUpMeBinding, BasicInfo>() {
    override val layoutResId = R.layout.fragment_pop_up_me

    companion object {
        fun newInstance(
            id: List<Location>,
            directionIsAvailable: Boolean = false,
            onGetDirectionClicked: (engineer: Location) -> Unit = {}
        ) = PopUpProfileDialog(id, directionIsAvailable, onGetDirectionClicked)

        fun newInstance(
            id: Location,
            directionIsAvailable: Boolean = false,
            onGetDirectionClicked: (engineer: Location) -> Unit = {}
        ) =
            PopUpProfileDialog(listOf(id), directionIsAvailable, onGetDirectionClicked)
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)
        val fragments = technicians.map { engineer ->
            PopUpProfileItemFragment.newInstance(engineer)
        }


        fragments.forEachIndexed { index, fragment ->
            fragment.onGetDirectionClicked = { engineer ->
                onGetDirectionClicked(engineer)
                dismiss()
            }
            fragment.directionIsAvailable = directionIsAvailable
            fragment.onViewProfileClicked = { engineer ->
                dialog?.dismiss()
                val info = preferences.myDetailProfile.value
                info?.let {
                    val profileId = engineer.id ?: -1
                    preferences.selectedProfileId.value = profileId
                    val intent = Intent(currentActivity, EngineerProfileActivity::class.java)
                    if (it.id == engineer.id) {
                        if (currentActivity is MainActivity) {
                            (currentActivity as MainActivity).openProfileDetail(profileId)
                        } else {
                            currentActivity.startActivity(intent)
                        }
                    } else {
                        currentActivity.startActivity(intent)
                    }
                }
            }
            fragment.shouldShowArrow = index < fragments.lastIndex
        }
        val arrayListFragments =
            arrayListOf<BaseFragment<out BaseViewModel, out ViewDataBinding>>().also {
                it.addAll(fragments)
            }
        val titles = technicians.map { it.name.toString() }

        viewPagerAdapter.replaceAll(arrayListFragments, titles)
        binding.viewPager.adapter = viewPagerAdapter
//

    }
}