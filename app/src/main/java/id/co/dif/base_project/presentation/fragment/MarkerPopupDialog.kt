package id.co.dif.base_project.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseBottomSheetDialog
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.SiteDetails
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.databinding.DialogMarkerPopUpBinding
import id.co.dif.base_project.presentation.activity.MapSiteActivity
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable

class MarkerPopupDialog(val location: Location?) :
    BaseBottomSheetDialog<BaseViewModel, DialogMarkerPopUpBinding, SiteDetails>() {

    override val layoutResId = R.layout.dialog_marker_pop_up

    var onSiteIsSelected: (location: Location) -> Unit = { _ -> }
    var isSiteSelectable = false

    companion object {
        fun newInstance(location: Location?) = MarkerPopupDialog(location)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.btnSelectThisSite.setOnClickListener {
            location?.let {
                onSiteIsSelected(location)
                dismiss()
            }
        }

        Log.d("TAG", "onViewCreated: ${location?.image}")
        binding.imgIcon.loadImage(location?.image, shimmerDrawable(), R.drawable.ic_bakti, circleCrop = true)
        binding.btnSelectThisSite.isVisible = isSiteSelectable
        binding.titleSelectThisSite.isVisible = isSiteSelectable
        binding.textAddress.text = location?.site_addre_street
        binding.textPic.text = location?.site_end_customer
        binding.contactPhone.text = location?.site_contact_phone
        when (location?.type) {
            "site",
            "TT Site All" -> {
                binding.textName.text = location?.site_name
                binding.textPic.text = location?.site_contact_name
                binding.textAddress.text = location?.site_addre_street
                binding.contactPhone.text = location?.site_contact_phone.orDefault()
                binding.tvAddress.text = location.site_provider
            }

            "TT Map All" -> {
                binding.textName.text = location?.site_name.orDefault()
                binding.textAddress.text = location?.pgroup_nscluster
                binding.contactPhone.text = location?.site_contact_phone
                binding.tvAddress.text = location.site_address_kelurahan
            }

            "technician" -> {
                binding.textName.text = location?.name.orDefault()
                binding.imgIcon.setImageResource(R.drawable.img_profile_technition)
            }

        }
    }

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.btnDetail.setOnClickListener {
            if (location?.type == "TT Site All") {
                preferences.selectedSite.value=location
                startActivity(Intent(requireContext(), MapSiteActivity::class.java))
//                TicketListPopupDialog.newInstance(location).show(
//                    childFragmentManager,
//                    TicketListPopupDialog::class.java.name
                //               )
            } else {
                preferences.selectedSite.value=location
                TicketListPopupDialog.newInstance(location).show(
                    childFragmentManager,
                    TicketListPopupDialog::class.java.name
                )
//                context?.startActivity(Intent(context, DetilTicketActivity::class.java))
//                startActivity(Intent(requireContext(), MapSiteActivity::class.java))
            }
        }
        binding.btnDetail.requestFocus()
    }


}

