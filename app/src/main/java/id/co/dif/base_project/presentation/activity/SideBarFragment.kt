package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.os.Bundle
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.databinding.FragmentSideBarBinding

class SideBarFragment : BaseFragment<BaseViewModel, FragmentSideBarBinding>() {
    override val layoutResId = R.layout.fragment_side_bar

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        var txDashboard = false
        var info = preferences.myDetailProfile.value

        binding.icSidebar.setOnClickListener {
            activity?.onBackPressed()
        }
//        binding.tvMyDashboard.setOnClickListener {
//            startActivity(Intent(requireContext(), MyDashboardActivity::class.java))
//        }
//        binding.tvTtDashboard.setOnClickListener {
//            startActivity(Intent(requireContext(), TroubleTicketActivity::class.java))
//        }
        binding.tvTeamDashboard.setOnClickListener {
            startActivity(Intent(requireContext(), TeamDashboardActivity::class.java))
        }
        binding.txSetting.setOnClickListener {
            startActivity(Intent(requireContext(), SettingActivity::class.java))
        }

        binding.txMessage.setOnClickListener {
            startActivity(Intent(requireContext(), MessageActivity::class.java))
        }

        binding.spHandling.setOnClickListener {
            startActivity(Intent(requireContext(), SpHandlingActivity::class.java))
        }

        binding.txDashboard.setOnClickListener {
            txDashboard = !txDashboard
            if (txDashboard) {
                binding.tvMyDashboard.isVisible = true
                binding.tvTtDashboard.isVisible = true
                binding.tvTeamDashboard.isVisible = true
            } else {
                binding.tvMyDashboard.isVisible = false
                binding.tvTtDashboard.isVisible = false
                binding.tvTeamDashboard.isVisible = false
            }
        }

        Picasso.get().load(info?.photo_profile).into(binding.imgProfile);
        binding.tvName.setText("${info?.fullname}")
        binding.idPosition.setText("${basiInfo?.emp_position}")

        binding.icSidebar.setOnClickListener {
            activity?.onBackPressed()
        }

    }



}