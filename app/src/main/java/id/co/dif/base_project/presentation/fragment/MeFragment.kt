package id.co.dif.base_project.presentation.fragment

import android.os.Bundle
import android.util.Log
import com.google.android.material.tabs.TabLayoutMediator
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.FragmentMeBinding
import id.co.dif.base_project.presentation.activity.EditProfileActivity
import id.co.dif.base_project.presentation.adapter.ViewPager2Adapter
import id.co.dif.base_project.utils.base64ImageToBitmap
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.MeViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class MeFragment : BaseFragment<MeViewModel, FragmentMeBinding>(), KoinComponent {
    override val layoutResId = R.layout.fragment_me
    private val overviewFragment: OverviewFragment by inject()
    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        binding.imgRefresh.setOnClickListener {
            viewModel.showLoading()
            setupData()
        }
        setupData()

        binding.rootLayout.onBackButtonClicked = {
            currentActivity.onBackPressedFragment()
        }

        binding.btnEditProfile.setOnClickListener {
            startActivity(EditProfileActivity.newInstance(requireContext()))
        }

        viewModel.responseDetailedProfile.observe(lifecycleOwner) {
            if (it.status == 200) {
                binding.bgHeaderMe.setImageResource(R.drawable.img_default_bg)
                if (it.data.fullname != null) {
                    binding.userName.text = "${it.data.fullname}"
                } else {
                    ""
                }

                binding.userPosition.text = it.data.position.orDefault("")
                preferences.myDetailProfile.value = it.data
                it.data.photo_profile?.let { encoded ->
                    binding.imageMe.setImageBitmap(base64ImageToBitmap(encoded))
                }
                it.data.cover?.let { cover ->
                    Log.d("TAG", "onViewBindingCreatedcover: $cover")
                    binding.bgHeaderMe.loadImage(cover, shimmerDrawable(), skipMemoryCaching = true, default = R.drawable.cover_3e_default)
                }
            }
        }

        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
    }

    private fun setupData() {
        val fragments =
            arrayListOf(overviewFragment)
        val titles = arrayListOf(
            getString(R.string.title_overview)
        )
        val viewPagerAdapter = ViewPager2Adapter(fragments, childFragmentManager, lifecycle)
        binding.viewPager.adapter = viewPagerAdapter
        binding.viewPager.offscreenPageLimit = fragments.count()

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = titles[position]
        }.attach()
        binding.viewPager.isSaveEnabled = false
        val id = preferences.selectedProfileId.value
        viewModel.getDetailProfile(id)
        viewModel.dissmissLoading()
    }

    override fun onResume() {
        super.onResume()
        setupData()
    }

}



