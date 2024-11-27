package id.co.dif.base_project.presentation.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.CheckBox
import com.google.android.material.tabs.TabLayout
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.UnreadNumber
import id.co.dif.base_project.databinding.FragmentInboxBinding
import id.co.dif.base_project.databinding.LayoutNotificationTabBinding
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.eval
import id.co.dif.base_project.utils.stringRes
import id.co.dif.base_project.viewmodel.InboxViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class InboxFragment : BaseFragment<InboxViewModel, FragmentInboxBinding>(), KoinComponent {
    override val layoutResId: Int = R.layout.fragment_inbox
    private val fragmentNotification: NotificationFragment by inject()
    private val fragmentMessageNotification: MessageNotificationFragment by inject()
    lateinit var viewPagerAdapter: TitledViewPagerAdapter
    private lateinit var tabTitles: List<String>


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {

        tabTitles = arrayListOf(
            R.string.notification.stringRes(requireContext()),
            R.string.message.stringRes(requireContext())
        )

        viewModel.getUnreadNumber()

        fragmentMessageNotification.onRefresh = {
            viewModel.getUnreadNumber()
        }

        fragmentNotification.onRefresh = {
            viewModel.getUnreadNumber()
        }

        fragmentNotification.onMessageRead = {
            viewModel.getUnreadNumber()
        }

        fragmentMessageNotification.onNotificationRead = {
            viewModel.getUnreadNumber()
        }

        viewModel.responseUnreadNumber.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                updateUnreadNumber(it.data)
            }
        }
        viewPagerAdapter = TitledViewPagerAdapter(childFragmentManager)
        val fragments =
            arrayListOf(fragmentNotification, fragmentMessageNotification)

        viewPagerAdapter.replaceAll(
            fragments, tabTitles
        )
        binding.pager.adapter = viewPagerAdapter
        binding.pager.offscreenPageLimit = fragments.count()
        binding.tabLayout.setupWithViewPager(binding.pager)
        binding.pager.isSaveEnabled = false
        setupTabLayoutTitle()

        binding.tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tvTitle: CheckBox = it.customView!!.findViewById(R.id.tv_title)
                    tvTitle.isChecked = true
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                tab?.let {
                    val tvTitle: CheckBox = it.customView!!.findViewById(R.id.tv_title)
                    tvTitle.isChecked = false
                }
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

    }

    private fun updateUnreadNumber(data: UnreadNumber) {
        val dataArray = listOf(data.unreadNumberNotification, data.unreadNumberMessage)
        Log.d("TAG", "updateUnreadNumber: $data")
        tabTitles.forEachIndexed { index, s ->
            val binding = viewModel.tabBindings[index]
            val number = dataArray[index]
            val maxNumber = 99
            val isExceedMax = number > maxNumber
            binding.tvCount.text = isExceedMax.eval("$maxNumber+", number.toString())
        }
    }

    private fun setupTabLayoutTitle() {

        tabTitles.forEachIndexed { index, s ->
            val tab = binding.tabLayout.getTabAt(index)
            val tabBinding = LayoutNotificationTabBinding.inflate(layoutInflater)
            tabBinding.overlay.setOnClickListener {
                Log.d("TAG", "setupTabLayoutTitle: ${tab?.id}")
                binding.tabLayout.selectTab(tab)
            }
            tabBinding.tvTitle.text = s
            tab?.customView = tabBinding.root
            if (index == 0) tabBinding.tvTitle.isChecked = true
            viewModel.tabBindings.add(tabBinding)
        }

    }

    fun openInbox(menu: Int) {
        startActivity(Intent(requireContext(), fragmentMessageNotification::class.java))
    }

//    override fun onResume() {
//        super.onResume()
//        viewModel.getUnreadNumber()
//        if (binding.pager.adapter != null) {
//            if (binding.pager.currentItem == 0) {
//                fragmentNotification.setupNotificationList()
//            } else {
//                fragmentMessageNotification.setupMessageNotificationList()
//            }
//        }
//    }

}