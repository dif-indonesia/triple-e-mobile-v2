package id.co.dif.base_project.presentation.fragment

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.core.view.isVisible
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.databinding.DialogPopupAlertBinding
import id.co.dif.base_project.databinding.FragmentTimelineBinding
import id.co.dif.base_project.presentation.adapter.TimeLineAdapter
import id.co.dif.base_project.utils.AndroidDownloader
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.dimBehind
import id.co.dif.base_project.utils.ifTrue
import id.co.dif.base_project.viewmodel.TimeLineViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject


class TimelineFragment : BaseFragment<TimeLineViewModel, FragmentTimelineBinding>(), KoinComponent {
    override val layoutResId = R.layout.fragment_timeline

    lateinit var adapter: TimeLineAdapter
    lateinit var alertBinding: DialogPopupAlertBinding
    private lateinit var alertWindow: PopupWindow
    private var currentAlertAnchor: View? = null
    private var onAlertPopupClick: () -> Unit = { -> }
    private var defaultPosition = 0
    private val downloader: AndroidDownloader by inject()

    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        val data = preferences.ticketDetails.value
        adapter = TimeLineAdapter()

        adapter.onAlertClick = { anchor, message, onAction ->
            onAlertPopupClick = onAction
            showAlertPopup(anchor = anchor, message = message)
        }
        adapter.onPdfDownloadClick = {
            if (it != null) {
                currentActivity.showToast("Downloading document. See notification")
                downloader.downloadFile(
                    url = it,
                    mimeType = "application/pdf",
                    title = "Triple-E Note"
                )
            } else {
                currentActivity.showToast("Something went wrong!")
            }
        }
        adapter.onDeleteClick = { note, message ->
            currentActivity.showAlert(
                requireContext(),
                message = message,
                iconId = R.drawable.ic_bin,
                iconTint = R.color.red,
                buttonPrimaryText = getString(R.string.delete),
                buttonPrimaryColor = R.color.red
            ) {
                viewModel.deleteNote(note.id, data?.tic_id)
            }
        }
        binding.rvTimeline.adapter = adapter

//        viewModel.responseListTroubleTicket.observe(lifecycleOwner) {
//            if (it.status == 200) {

        changesUpdate()

        Handler().postDelayed({
            adapter.highlight(defaultPosition)
            binding.rvTimeline.smoothScrollToPosition(defaultPosition.coerceAtLeast(0))
        }, 700)

        binding.ticNumber.setText("${data?.tic_number}")
        setupChartSelectedChip()

        viewModel.responseDelete.observe(lifecycleOwner) {
            if (it.status in StatusCode.SUCCESS) {
                viewModel.getDetilTicket(preferences.selectedTicketId.value)
                currentActivity.showToast(getString(R.string.notes_successfully_deleted))
            } else {
                currentActivity.showToast(getString(R.string.something_went_wrong))
            }
        }
        viewModel.responseDetilTicket.observe(lifecycleOwner) {
            if (it.status == 200) {
                preferences.ticketDetails.value = it.data
                changesUpdate()
                binding.tvChronology.setText("${R.string.ticket_chronology_based_on_notes_from_user}. Last update ${it.data.tic_notes?.get(0)}")
            }
        }


    }

    private fun showAlertPopup(
        anchor: View,
        dimBehind: Boolean? = true,
        message: String?
    ) {
        currentAlertAnchor = anchor
        alertWindow.dismiss()
        alertBinding.text.text = message
        alertWindow.contentView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val xOff = -alertWindow.contentView.measuredWidth
        val yOff = (-alertWindow.contentView.measuredHeight - anchor.height) / 2

        alertWindow.showAsDropDown(anchor, xOff, yOff)
        dimBehind.ifTrue {
            alertWindow.dimBehind()
            alertWindow.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            alertWindow.isOutsideTouchable = true
            alertWindow.isFocusable = true
            alertWindow.update()

            alertWindow.contentView.setOnTouchListener { view, event ->
                (event.action == MotionEvent.ACTION_OUTSIDE).ifTrue {
                    alertWindow.dismiss()
                }
                view.performClick()
            }
        }

    }

    private fun setupChartSelectedChip() = fragmentContext?.let { context ->

        binding.root.setOnClickListener {
            alertWindow.dismiss()
        }
        val li = LayoutInflater.from(context)
        alertBinding =
            DialogPopupAlertBinding.inflate(li)
        alertWindow = PopupWindow(
            alertBinding.root,
            FrameLayout.LayoutParams.WRAP_CONTENT,
            FrameLayout.LayoutParams.WRAP_CONTENT
        )
        alertBinding.root.setOnClickListener {
            onAlertPopupClick()
            alertWindow.dismiss()
        }
        binding.root.viewTreeObserver.addOnScrollChangedListener {
            currentAlertAnchor?.let {
                alertWindow.dismiss()
                currentAlertAnchor = null
            }
        }

        // Dismiss if the chip goes out of the visible screen

    }

    private fun changesUpdate() {
        Log.d("TAG", "changesUpdate: bebas")
        val data = preferences.ticketDetails.value
        adapter.data.clear()
        adapter.data.addAll(data?.tic_notes.orEmpty())
        adapter.notifyDataSetChanged()
        binding.layoutEmptyState.isVisible = adapter.data.isEmpty()
    }


    override fun onResume() {
        super.onResume()
        adapter.clearHighlight()

    }

    fun scrollTo(notesPosition: Int) {
        binding.rvTimeline.scrollToPosition(0)
        defaultPosition = notesPosition
    }


}

