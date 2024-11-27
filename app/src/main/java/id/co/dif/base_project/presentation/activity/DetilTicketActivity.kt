package id.co.dif.base_project.presentation.activity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseActivity
import id.co.dif.base_project.databinding.ActivityDetilTicketBinding
import id.co.dif.base_project.presentation.adapter.TitledViewPagerAdapter
import id.co.dif.base_project.presentation.fragment.ChangeLogFragment
import id.co.dif.base_project.presentation.fragment.DetailFragment
import id.co.dif.base_project.presentation.fragment.MapsTicketFragment
import id.co.dif.base_project.presentation.fragment.SiteFragment
import id.co.dif.base_project.presentation.fragment.SparePartListFragment
import id.co.dif.base_project.presentation.fragment.TimelineFragment
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.viewmodel.DetilTicketViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class DetilTicketActivity : BaseActivity<DetilTicketViewModel, ActivityDetilTicketBinding>(),
    KoinComponent {
    override val layoutResId = R.layout.activity_detil_ticket
    private val mapsTicketFragment: MapsTicketFragment by inject()
    private val detailFragment = DetailFragment()
    private val siteFragment: SiteFragment by inject()
    private val timelineFragment: TimelineFragment by inject()
    private val changeLogFragment: ChangeLogFragment by inject()
    private lateinit var viewPagerAdapter: TitledViewPagerAdapter
    private val pageTitles = listOf(
        R.string.maps,
        R.string.ticket,
        R.string.site,
        R.string.timeline,
        R.string.log,
        )
    private val fragments =
        arrayListOf(
            mapsTicketFragment,
            detailFragment,
            siteFragment,
            timelineFragment,
            changeLogFragment,
            )
    var shouldGoToTimeline = false

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        shouldGoToTimeline = intent.getBooleanExtra("should_go_to_timeline", false)
        val notesPosition = intent.getIntExtra("notes_position", 0)
        detailFragment.onSubmit = {
            if (it) {
                shouldGoToTimeline = true
                viewModel.getTicketDetails(preferences.selectedTicketId.value)
            }
        }

        viewModel.responseTicketDetails.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                val ticketDetails = it.data
                val allTicketDetails = preferences.allTicketsDetails.value ?: hashMapOf()
                allTicketDetails[ticketDetails.tic_id] = ticketDetails
                preferences.allTicketsDetails.value = allTicketDetails
                preferences.ticketDetails.value = ticketDetails
                ticketDetails.site_info?.image.log("sdfdsfdsfdsfsdf")


                viewPagerAdapter = TitledViewPagerAdapter(supportFragmentManager)
                viewPagerAdapter.replaceAll(
                    fragments, pageTitles.map { id -> getString(id) }
                )
                binding.viewPager.adapter = viewPagerAdapter
                binding.viewPager.offscreenPageLimit = fragments.count()
                binding.tabLayout.setupWithViewPager(binding.viewPager)
                binding.viewPager.isSaveEnabled = false
//                binding.rootLayout.onBackButtonClicked = {
//                    onBackPressedDispatcher.onBackPressed()
//                }
                if (shouldGoToTimeline) {
                    binding.viewPager.setCurrentItem(pageTitles.indexOf(R.string.timeline), true)
                    timelineFragment.scrollTo(notesPosition)
                    shouldGoToTimeline = false
                }

            } else {
                Toast.makeText(
                    this, it.message, Toast.LENGTH_SHORT
                ).show().log("asuuu")
                finish()
            }

            binding.rootLayout.title = getString(R.string.detail_ticket, it.data.tic_id.toString())
        }
        val ticId = preferences.selectedTicketId.value
        if (ticId == null) {
            showToast(getString(R.string.ticket_is_not_found))
            viewModel.dissmissLoading()
            finish()
        }
        Log.d("TAG", "onViewBinsdfsdfsdfsdfdingCreated: $ticId")
        viewModel.getTicketDetails(ticId.toString())
    }
}


