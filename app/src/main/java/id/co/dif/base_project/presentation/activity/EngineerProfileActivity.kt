package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import com.google.android.material.tabs.TabLayoutMediator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.data.PlainValueLabel
import id.co.dif.base_project.databinding.ActivityEngineerProfileBinding
import id.co.dif.base_project.presentation.adapter.ViewPager2Adapter
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.BasicInfoFragment
import id.co.dif.base_project.presentation.fragment.EducationFragment
import id.co.dif.base_project.presentation.fragment.OverviewFragment
import id.co.dif.base_project.presentation.fragment.SkillFragment
import id.co.dif.base_project.presentation.fragment.WorkFragment
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.MeViewModel
import org.koin.core.component.KoinComponent

class EngineerProfileActivity : BaseActivity<MeViewModel, ActivityEngineerProfileBinding>(), KoinComponent {
    override val layoutResId = R.layout.activity_engineer_profile
    private val overviewFragment: OverviewFragment = OverviewFragment()
    private val workFragment: WorkFragment = WorkFragment()
    private val educationFragment: EducationFragment = EducationFragment()
    private val basicInfoFragment: BasicInfoFragment = BasicInfoFragment()
    private val skillFragment: SkillFragment = SkillFragment()
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val fragments =
            arrayListOf(overviewFragment, workFragment, educationFragment, basicInfoFragment, skillFragment)
        val titles = arrayListOf(
            getString(R.string.title_overview),
            getString(R.string.title_work),
            getString(R.string.title_education),
            getString(R.string.title_basic_info),
            getString(R.string.title_skill)
        )
        val viewPagerAdapter = ViewPager2Adapter(fragments, supportFragmentManager, lifecycle)
        binding.rootLayout.onBackButtonClicked = {
            onBackPressedDispatcher.onBackPressed()
        }
        workFragment.viewOnly = true
        educationFragment.viewOnly = true
        binding.btnPerformance.setOnClickListener {
            needsImplementedToast("Go to user TT Dashboard")
        }
        binding.btnSendMessage.setOnClickListener {
            needsImplementedToast("Go to Message")
        }
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = fragments.count()
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
        binding.viewPager.isSaveEnabled = false


        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status == 200) {
                binding.userName.setText(it.data.fullname.orDefault(""))
                binding.userPosition.setText(it.data?.position.orDefault(""))

                it.data.photo_profile?.let { encoded ->
                    binding.imageMe.setImageBitmap(base64ImageToBitmap(encoded))
                }
                it.data.cover?.let { image ->
                    binding.bgHeaderMe.loadImage(image, shimmerDrawable(), default = R.drawable.cover_3e_default)
                }

            }
        }


        viewModel.responseeditProfileList.observe(lifecycleOwner) {
            if (it.status == 200) {
//                viewModel.responseBasicInfoList()
//                binding.imageMe.setImageURI()
            }
        }

        viewModel.responseGetOtp.observe(lifecycleOwner) {
            if (it.status == 200) {
//                viewModel.responseBasicInfoList()


            }
        }

        viewModel.responseUserActivityLog.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                consumeUserActivity(it.data.list)
            }
        }
        val id = preferences.selectedProfileId.value
        viewModel.getUserActivityLog(id)

    }

    private fun consumeUserActivity(userActivitiesLog: List<PlainValueLabel>) {
        val values = userActivitiesLog.map { it.count }
        val totalMinutes = values.sum()
        val hours = totalMinutes / 60
        val minutes = totalMinutes % 60
        binding.tvTotalUsage.text = getString(R.string.total_time, hours, minutes)

    }

    override fun onResume() {
        super.onResume()
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
    }

}
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        Log.d("sara1997", "$requestCode")
//        viewModel.responseeditProfileList.observe(lifecycleOwner) {
//            if (it.status == 200) {
//                val picture: Bitmap? = data?.getParcelableExtra("data")
//                if (picture != null) {
////                    val file:  File = getFileFromUri(uri) ("imageName", picture)
////                    val requestBody = file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
//                    val image =
//                        MultipartBody.Part.createFormData("userImage", file.name, requestBody)
//                    Picasso.get().load(file.toURL().toString()).into(binding.imageMe)
//                }
//            }
//        }
//    }





