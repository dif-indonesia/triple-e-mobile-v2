package id.co.dif.base_project.presentation.activity

import android.content.Intent
import android.os.Bundle
import com.squareup.picasso.Picasso
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityMenuSidebarBinding
import id.co.dif.base_project.presentation.fragment.DashboardFragment
import id.co.dif.base_project.viewmodel.MeViewModel

class MenuSidebarActivity : BaseActivity<MeViewModel, ActivityMenuSidebarBinding>() {
    override val layoutResId = R.layout.activity_menu_sidebar


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        var txDashboard = false
        var info = preferences.myDetailProfile.value

        binding.icSidebar.setOnClickListener {
            onBackPressed()
        }
        binding.txSetting.setOnClickListener {
            startActivity(Intent(this, SettingActivity::class.java))
        }

        binding.txMessage.setOnClickListener {
            startActivity(Intent(this, MessageActivity::class.java))
        }

        binding.spHandling.setOnClickListener {
            startActivity(Intent(this, SpHandlingActivity::class.java))
        }

        binding.txDashboard.setOnClickListener {
            startActivity(Intent(this, DashboardFragment::class.java))
        }

        Picasso.get().load(info?.photo_profile).into(binding.imgProfile);
        binding.tvName.text = "${info?.fullname}"
        binding.idPosition.text = "${basiInfo?.emp_position}"


    }

    override fun onBackPressed() {

        binding.icSidebar.setOnClickListener {
            onBackPressed()
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
        }

        super.onBackPressed()
        overridePendingTransition(R.anim.activity_in, R.anim.activity_out)
    }


}