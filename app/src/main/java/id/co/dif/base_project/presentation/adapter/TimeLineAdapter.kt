package id.co.dif.base_project.presentation.adapter

import android.content.Intent
import android.net.Uri
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat.startActivity
import androidx.core.view.isVisible
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewHolder
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Note
import id.co.dif.base_project.data.TicketStatus
import id.co.dif.base_project.databinding.ItemTimelineBinding
import id.co.dif.base_project.presentation.activity.MapsNotesActivity
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.drawableRes
import id.co.dif.base_project.utils.ifNot
import id.co.dif.base_project.utils.ifTrue
import id.co.dif.base_project.utils.isNotNullOrEmpty
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.utils.urlTypeOf


class TimeLineAdapter() : BaseAdapter<BaseViewModel, ItemTimelineBinding, Note>() {
    override val layoutResId = R.layout.item_timeline
    var highlightedItemIndex = -1
    var onAlertClick: (anchor: View, message: String?, onAction: () -> Unit) -> Unit =
        { _, _, _ -> }
    var onDeleteClick: (item: Note, message: String) -> Unit =
        { _, _ -> }
    var onPdfDownloadClick: (url: String?) -> Unit = { _ -> }
    var viewingOnly = false

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<ItemTimelineBinding>) {
        super.onViewDetachedFromWindow(holder)
        holder.binding.layoutHighlighted.clearAnimation()
    }

    override fun onLoadItem(
        binding: ItemTimelineBinding,
        data: MutableList<Note>,
        position: Int
    ) {
        context?.let { context ->
            val item = data[position]
            (position == highlightedItemIndex - 1).ifTrue {
                binding.layoutHighlighted.alpha = 1f
            } ifNot {
                binding.layoutHighlighted.alpha = 0f
            }

            (item.id == null) ifTrue {
                binding.clock.setImageResource(R.drawable.ic_clock_pending)
            } ifNot {
                binding.clock.setImageResource(R.drawable.ic_clock)
            }

            binding.stickTimelineFade.background = R.drawable.bg_gray.drawableRes(context)
            binding.stickTimelineFadeBottom.background = R.drawable.bg_gray.drawableRes(context)

            if (position == data.lastIndex) {
                binding.stickTimelineFadeBottom.background =
                    R.drawable.bg_gradient_bottom_timeline.drawableRes(context)
            }
            if (position == 0) {
                binding.stickTimelineFade.background =
                    R.drawable.bg_gradient_top_timeline.drawableRes(context)
            }

            binding.time.text = item.time
            binding.date.text = item.date
            binding.name.text = item.username
            binding.notes.text = item.note
            val ticketStatus = TicketStatus.fromLabel(item.tic_status.orDefault(""))
            binding.status.setTextColor(ticketStatus.foreground.colorRes(context))
            binding.status.text = ticketStatus.label

            binding.idUploadImage.setOnClickListener {
                StfalconImageViewer.Builder<String>(context, arrayOf(item.file)) { view, image ->
                    Picasso.get().load(image).into(view)
                }.show()
            }

            item.file.isNotNullOrEmpty().ifTrue {
                binding.layoutCrosshair.isVisible = true
                val isWithinRadius = item.isWithinRadius == "1"
                val locationAvailable = item.latitude != null && item.longitude != null
                binding.notes.text = item.note
                binding.notes.isVisible = true

                when (urlTypeOf(item.file)) {
                    "document" -> {
                        val content = SpannableString(getFileNameFromUri(item.file))
                        content.setSpan(UnderlineSpan(), 0, content.length, 0)
                        binding.idFilePdf.text = content
                        binding.idFilePdf.isVisible = true
                        binding.layoutImage.visibility = View.GONE
                        binding.layoutAlert.isVisible = false
                        if (!content.endsWith(".pdf")) {
                            binding.idFilePdf.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_video,
                                0,
                                0,
                                0
                            )
                            binding.layoutAlert.isVisible = isWithinRadius
                                    binding.layoutCrosshair.isVisible = !viewingOnly
                            if (!locationAvailable) {
                                binding.layoutCrosshair.isVisible = false
                                binding.layoutAlert.isVisible = false
                            }
                        } else {
                            binding.idFilePdf.setCompoundDrawablesWithIntrinsicBounds(
                                R.drawable.ic_file,
                                0,
                                0,
                                0
                            )
                        }
                    }
                    "image" -> {
                        binding.layoutImage.visibility = View.VISIBLE
                        binding.idFilePdf.isVisible = false
                        binding.idUploadImage.loadImage(item.file, shimmerDrawable())
                        binding.layoutAlert.isVisible = isWithinRadius
                        binding.layoutCrosshair.isVisible = !viewingOnly
                        if (!locationAvailable) {
                            binding.layoutCrosshair.isVisible = false
                            binding.layoutAlert.isVisible = false
                        }
                    }
                }
            }.ifNot {
                binding.idFilePdf.isVisible = false
                binding.layoutAlert.isVisible = false
                binding.layoutImage.visibility = View.GONE
            }


            binding.layoutAlert.setOnClickListener {
                onAlertClick(
                    it,
                    context.getString(R.string.image_captured_is_not_within_the_site_radius_100_meters)
                ) {}
            }

            binding.layoutCrosshair.setOnClickListener {
                onAlertClick(it, context?.getString(R.string.show_on_map)) {
                    openMaps(item, data)
                }
            }

            binding.idFilePdf.setOnClickListener {
                val pdfUrl = item.file
                context.startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            pdfUrl
                        )
                    )
                )
            }
//            val myDetailProfile = preferences.myDetailProfile.value
//            myDetailProfile?.let {
//                if (viewingOnly) {
//                    binding.delete.isVisible = false
//                    return@let
//                }
//                binding.delete.isVisible = item.userId == it.id.toString()
//            }
//            binding.delete.setOnClickListener {
//                Log.d("TAG", "onLoadsdfdsItem: $item")
//                onDeleteClick(
//                    item, context.getString(R.string.are_you_sure_you_want_to_delete_this)
//                )
//            }
            var datastatus = preferences.ticketDetails.value
            var status = datastatus?.tic_status

//            if (status == "Closed") {
//                binding.delete.isVisible = false
//            }
        }
    }

    private fun openMaps(highlightedNote: Note, notes: MutableList<Note>) {
        preferences.highlightedNote.value = highlightedNote
        preferences.listOfNotes.value = notes
        context?.startActivity(
            Intent(context, MapsNotesActivity::class.java)
        )
    }

    fun getFileNameFromUri(url: String?): String? {
        val path = Uri.parse(url).path
        return path?.substringAfterLast("/")
    }

    fun highlight(defaultPosition: Int) {
        highlightedItemIndex = defaultPosition
    }

    fun clearHighlight() {
        val oldHighlightedIndex = highlightedItemIndex
        highlightedItemIndex = -1
        notifyItemChanged(oldHighlightedIndex)
    }


}