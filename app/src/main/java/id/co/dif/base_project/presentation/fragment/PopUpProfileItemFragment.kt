package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.databinding.FragmentPopupUpMeItemBinding
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.utils.str
import id.co.dif.base_project.viewmodel.BasicInfoViewModel

class PopUpProfileItemFragment() :
    BaseFragment<BasicInfoViewModel, FragmentPopupUpMeItemBinding>() {
    override val layoutResId = R.layout.fragment_popup_up_me_item
    lateinit var engineer: Location
    var directionIsAvailable = false
    var onViewProfileClicked: (engineer: Location) -> Unit = { _ -> }
    var onGetDirectionClicked: (engineer: Location) -> Unit = { _ -> }
    var shouldShowArrow = false

    companion object {
        fun newInstance(engineer: Location) = PopUpProfileItemFragment().apply { applyEngineer(engineer) }
    }

    fun applyEngineer(location: Location) {
        engineer = location
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        viewModel.getDetailProfile(engineer.id)
        binding.txtDistance.isVisible = directionIsAvailable
        binding.routeThisSite.isVisible = directionIsAvailable
        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                binding.position.text = it.data.position.orDefault()
                it.data.photo_profile?.let { encoded ->
                    binding.imgProfile.setImageBitmap(base64ImageToBitmap(encoded))
                }
                binding.phone.text = it.data.phone.orDefault()
                binding.txtTechnicianName.text = it.data.fullname.orDefault()
                val info = preferences.myDetailProfile.value
                info?.let { myProfile ->
                    if (myProfile.id == engineer.id) {
                        binding.txtTechnicianName.text =
                            "${it.data.fullname.orDefault()} (You)"
                    }
                }
            }
        }
        binding.txtDistance.setOnClickListener {
            onGetDirectionClicked(engineer)
        }

        binding.viewArrow.isVisible = shouldShowArrow
        binding.viewArrow.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.anim_arrow_moving)
        )
        binding.btnViewProfile.setOnClickListener {
            onViewProfileClicked(engineer)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getDetailProfile(engineer.id)
    }

}