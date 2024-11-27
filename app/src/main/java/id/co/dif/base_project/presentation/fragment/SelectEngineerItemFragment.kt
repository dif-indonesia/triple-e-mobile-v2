package id.co.dif.base_project.presentation.fragment

import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.databinding.FragmentSelectEngineerItemBinding
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.utils.toFormattedDistance
import id.co.dif.base_project.viewmodel.SelectEngineerItemViewModel

class SelectEngineerItemFragment(private val engineer: Location) :
    BaseFragment<SelectEngineerItemViewModel, FragmentSelectEngineerItemBinding>() {
    override val layoutResId: Int
        get() = R.layout.fragment_select_engineer_item
    var onSelectEngineerClicked: (engineer: Location) -> Unit = { _ -> }
    var onClickPing: (engineer: Location) -> Unit = { _ -> }
    var shouldShowArrow = false
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        handleEngineerReadyState(engineer)
        binding.txtLoadCount.text = getString(R.string.on_going_trouble_ticket, engineer.load)
        binding.txtDistance.text = toFormattedDistance(engineer.distance)
        binding.txtTechnicianName.text = engineer.name.orDefault("-")
        binding.txtCompletedCount.text =
            getString(
                R.string.trouble_ticket_completed,
                engineer.completed.orDefault("-")
            )
        binding.txtLocation.text = engineer.address.orDefault("-")

        engineer.image?.let { encoded ->
            binding.imgProfile.setImageBitmap(base64ImageToBitmap(encoded))
        }
        binding.viewArrow.isVisible = shouldShowArrow
        binding.viewArrow.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.anim_arrow_moving)
        )
    }

    private fun handleEngineerReadyState(engineer: Location) {
        if (engineer.locationIsUpdated == true) {
            handleEngineerWithinSiteRadius(engineer)
            return
        }
        Log.d("TAG", "handleEngineerReadyState: ${engineer.locationIsUpdated}")
        handleEngineerNotReady(engineer)
    }

    private fun handleEngineerWithinSiteRadius(engineer: Location) {
        if (engineer.isWithinSiteRadius == true) {
            binding.btnSelectEngineer.setOnClickListener {
                onSelectEngineerClicked(engineer)
            }
            binding.viewAlert.isVisible = false
            return
        }
        binding.btnSelectEngineer.setOnClickListener {
            currentActivity.showAlert(
                context = requireContext(),
                message = getString(R.string.cannot_proceed_with_this_engineer),
                value = getString(R.string.this_engineer_is_not_within_the_site_radius_you_can_still_proceed_but_not_advisable),
                buttonPrimaryText = getString(R.string.proceed),
                buttonSecondaryText = getString(R.string.dismiss),
                onButtonPrimaryClicked = { onSelectEngineerClicked(engineer) },
                onButtonSecondaryClicked = {}
            )
        }

        binding.btnPingEngineer.setOnClickListener {
//            val circularProgressDrawable = CircularProgressDrawable(requireContext())
//            circularProgressDrawable.strokeWidth = 10f
//            circularProgressDrawable.centerRadius = 16f
//            circularProgressDrawable.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK)
//            circularProgressDrawable.start()
//            binding.btnPingEngineer.setImageDrawable(circularProgressDrawable)
            onClickPing(engineer)
        }
        setButtonBackgroundAndShowAlertView()

    }

    private fun handleEngineerNotReady(engineer: Location) {
        binding.btnSelectEngineer.setOnClickListener {
            currentActivity.showAlert(
                context = requireContext(),
                message = getString(R.string.this_engineer_had_not_updated_their_latest_location_yet_you_can_still_proceed_but_the_engineer_is_not_guarantee_to_be_within_the_site_radius_at_the_moment),
                buttonPrimaryText = getString(R.string.proceed),
                onButtonPrimaryClicked = { onSelectEngineerClicked(engineer) },
                buttonSecondaryText = getString(R.string.dismiss),
            )
        }

        binding.btnPingEngineer.setOnClickListener {
            val circularProgressDrawable = CircularProgressDrawable(requireContext())
            circularProgressDrawable.strokeWidth = 10f
            circularProgressDrawable.centerRadius = 16f
            circularProgressDrawable.setColorSchemeColors(Color.BLACK, Color.BLACK, Color.BLACK)
            circularProgressDrawable.start()
            binding.btnPingEngineer.setImageDrawable(circularProgressDrawable)
            onClickPing(engineer)
        }
        setButtonBackgroundAndShowAlertView()
    }

    private fun setButtonBackgroundAndShowAlertView() {
        binding.btnSelectEngineer.backgroundTintList =
            ColorStateList.valueOf(R.color.purple.colorRes(requireContext()))
        binding.viewAlert.isVisible = true
    }

}