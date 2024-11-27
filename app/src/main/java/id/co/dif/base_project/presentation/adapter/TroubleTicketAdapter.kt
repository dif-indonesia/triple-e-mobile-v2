package id.co.dif.base_project.presentation.adapter
//import id.co.dif.base_project.utils.parseTimeToDayAndHour
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.core.widget.TextViewCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.github.marlonlom.utilities.timeago.TimeAgo
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewHolder
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.TicketSeverity
import id.co.dif.base_project.data.TicketStatus
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.databinding.ItemNotificationLoadingBinding
import id.co.dif.base_project.databinding.ItemTroubleTicketBelitungBinding
import id.co.dif.base_project.databinding.ItemTroubleTicketBinding
import id.co.dif.base_project.presentation.activity.DetilTicketActivity
import id.co.dif.base_project.presentation.dialog.CheckinDialog
import id.co.dif.base_project.presentation.dialog.requestPermitDialog
import id.co.dif.base_project.presentation.dialog.submitTicketDialog
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.formatDate
import id.co.dif.base_project.utils.formatDateBelitung
import id.co.dif.base_project.utils.ifFalse
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.str
import org.koin.core.component.KoinComponent
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 10:46
 *
 */
class TroubleTicketAdapter(
   val onItemClicked: (TroubleTicket)->Unit
    = {},
   val onAlertClicked: (View, String)->Unit = {_,_->}

) :
    BaseAdapter<BaseViewModel, ViewBinding, TroubleTicket>(), KoinComponent {
    override val layoutResId = R.layout.item_trouble_ticket_belitung
    val startDate = Date() // Replace with your actual start date
    val endDate = Date() // Replace with your actual end date
    val remainingTime = endDate.time - startDate.time
    var timeInMillis = System.currentTimeMillis()
    val text: String = TimeAgo.using(timeInMillis)
    var onAlertClick: (anchor: View, message: String?, onAction: () -> Unit) -> Unit =
        { _, _, _ -> }


    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_CONTENT_GRAYED = 3
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    var isLoading = false

    init {
        this.data.addAll(data)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                ItemTroubleTicketBelitungBinding.inflate(inflater, parent, false)
            }

            ITEM_VIEW_TYPE_LOADING -> {
                ItemNotificationLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) ITEM_VIEW_TYPE_LOADING else ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(TroubleTicket())
    }

    fun add(troubleTicket: TroubleTicket) {
        data.add(troubleTicket)
        notifyItemInserted(data.lastIndex)
    }

    fun addAll(newData: List<TroubleTicket>) {
        for (result in newData) {
            add(result)
        }
    }

    fun removeLoadingFooter() {
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }

    fun clearData() {
        data.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): TroubleTicket {
        return data[position]
    }

    fun parseTimeToDayAndHour(fromDate: String, toDate: String? = null): String {
        log("sdlfjdlsf", fromDate, toDate)
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = if (toDate == null) {
            Calendar.getInstance().time
        } else {
            dateFormat.parse(toDate)
        }
        val parsedTime = dateFormat.parse(fromDate)
//        parsedTime.time = dateFormat.parse(timeString)
        val durationMillis = now.time - parsedTime.time
        val days = durationMillis / (1000 * 60 * 60 * 24)
        val hours = (durationMillis / (1000 * 60 * 60)) % 24
        val result = StringBuilder()
        if (days > 0) {
            result.append("$days day")
            if (days > 1) result.append("s")
            result.append(" ")
        }
        result.append("$hours hour")
        if (hours > 1) result.append("s")

        return result.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<TroubleTicket>,
        position: Int,
    ) {
        val myProfile = preferences.myDetailProfile.value
        context?.let { context ->
            when (getItemViewType(position)) {
                NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                    binding as ItemTroubleTicketBelitungBinding
                    val item = data[position]

                    item.permitStatus?.let { permitStatus ->
                        when {
                            item.ticCheckinAt == null && item.checkinStatus != null && item.checkinStatus?.checkinApproved == null  && item.ticPersonInChargeEmpId != myProfile?.id && item.permitStatus?.permitApproved == true -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Waiting Approve Checkin"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.pending)
                                )
                            }
                            item.ticCheckinAt == null && item.checkinStatus != null && item.ticPersonInChargeEmpId == myProfile?.id && item.permitStatus?.permitApproved == true -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Request Approve Checkin"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.ticCheckinAt != null && item.ticCheckoutAt != null  -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Closed"
                                binding.btnCheckin.isEnabled = false
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.closed)
                                )
                            }
                            item.ticCheckinAt != null && item.ticPersonInChargeEmpId != myProfile?.id && item.submitStatus?.submitApproved == true -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Checkout"
                                binding.btnCheckin.isEnabled = true
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.checkout)
                                )
                            }
                            item.ticCheckinAt == null && item.checkinStatus == null && item.ticPersonInChargeEmpId == myProfile?.id && item.permitStatus?.permitApproved == true -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Permit Approved"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.ticCheckinAt != null && item.checkinStatus?.checkinApproved == true && item.ticPersonInChargeEmpId == myProfile?.id && item.submitStatus == null -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Checkin Approved"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.submitStatus?.submitApproved == true && item.ticCheckoutAt == null && item.ticPersonInChargeEmpId == myProfile?.id -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Submit Approved"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.submitStatus?.submitApproved == false  && item.ticPersonInChargeEmpId == myProfile?.id && item.submitStatus != null -> {
                                binding.btnCheckin.isVisible = true
                                binding.layoutAlert.isVisible = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Ticket not accepted"
                                binding.btnCheckin.isEnabled = false
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.not_approve_permit)
                                )
                            }
                            item.submitStatus?.submitApproved == false  && item.ticPersonInChargeEmpId != myProfile?.id && item.submitStatus != null -> {
                                binding.btnCheckin.isVisible = true
                                binding.layoutAlert.isVisible = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Re-Submit Ticket"
                                binding.btnCheckin.isEnabled = true
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.send_request)
                                )
                            }
                            item.ticCheckinAt != null && item.ticPersonInChargeEmpId == myProfile?.id && item.submitStatus != null -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Approve Req Submit"
                                binding.btnCheckin.isEnabled = true
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.ticCheckinAt != null && item.ticPersonInChargeEmpId != myProfile?.id && item.submitStatus != null -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Waiting Approved Submit"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.pending)
                                )
                            }
                            item.ticCheckinAt != null && item.checkinStatus == null && item.ticPersonInChargeEmpId == myProfile?.id -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = false
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Checked"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.checked)
                                )
                            }
                            item.ticCheckinAt != null && item.ticPersonInChargeEmpId != myProfile?.id -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Submit"
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.isEnabled = true
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            item.ticCheckinAt == null && item.checkinStatus?.checkinApproved == false && item.ticPersonInChargeEmpId != myProfile?.id -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnCheckin.isEnabled = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnCheckin.text = "Re-Check"
                                binding.btnCheckin.isEnabled = false
                                binding.btnCheckin.alpha = 1.0f
                                binding.btnCheckin.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.pending)
                                )
                            }
                            permitStatus.permitApproved == null && permitStatus.permitInformation == null && item.ticPersonInChargeEmpId == myProfile?.id -> {
                                binding.btnCheckin.isVisible = false
                                binding.btnApprovePermit.text = "Request Approve"
                                binding.btnApprovePermit.isEnabled = true
                                binding.btnApprovePermit.alpha = 1.0f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.request_approve)
                                )
                            }
                            permitStatus.permitApproved == null && permitStatus.permitInformation == null && item.ticPersonInChargeEmpId != myProfile?.id -> {
                                binding.btnCheckin.isVisible = false
                                binding.btnApprovePermit.text = "Send Request"
                                binding.btnApprovePermit.isEnabled = false
                                binding.btnApprovePermit.alpha = 0.5f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.send_request)
                                )
                            }
                            permitStatus.permitApproved == true && item.ticPersonInChargeEmpId != myProfile?.id -> {
                                binding.btnCheckin.isVisible = true
                                binding.btnApprovePermit.isVisible = false
                                binding.btnApprovePermit.isEnabled = true
                                binding.btnApprovePermit.alpha = 1.0f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.send_request)
                                )
                            }
                            permitStatus.permitApproved == false && item.ticPersonInChargeEmpId == myProfile?.id -> {
                                binding.layoutAlert.isVisible = true
                                binding.btnCheckin.isVisible = false
                                binding.btnApprovePermit.isVisible = true
                                binding.btnApprovePermit.text = "Not Approved "
                                binding.btnApprovePermit.isEnabled = true
                                binding.btnApprovePermit.alpha = 1.0f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.not_approve_permit)
                                )
                            }
                            permitStatus.permitApproved == false && item.ticPersonInChargeEmpId != myProfile?.id -> {
                                binding.layoutAlert.isVisible = true
                                binding.btnCheckin.isVisible = false
                                binding.btnApprovePermit.isVisible = true
                                binding.btnApprovePermit.text = "Re-Submit Permit "
                                binding.btnApprovePermit.isEnabled = true
                                binding.btnApprovePermit.alpha = 1.0f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.not_approve_permit)
                                )
                            }
                            else -> {
                                binding.btnCheckin.isVisible = false
                                binding.btnApprovePermit.text = "Send Request"
                                binding.btnApprovePermit.isEnabled = false
                                binding.btnApprovePermit.alpha = 0.5f
                                binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                    ContextCompat.getColor(context, R.color.send_request)
                                )
                            }
                        }
                    } ?: run {
                        // Jika permitStatus null, set state default
                        binding.btnCheckin.isVisible = false

                        if (item.ticAccepted == true) {
                            binding.btnApprovePermit.text = "Taked"
                            binding.btnApprovePermit.isEnabled = false
                            binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(context, R.color.dark_grey)
                            )
                        }else if (item.ticPersonInChargeEmpId != myProfile?.id) {
                            binding.btnApprovePermit.text = "Take Ticket"
                            binding.btnApprovePermit.isEnabled = true
                            binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(context, R.color.request_approve)
                            )
                        } else if (item.ticPersonInChargeEmpId == myProfile?.id) {
                            binding.btnApprovePermit.text = "Ticket not taken yet"
                            binding.btnApprovePermit.isEnabled = false
                            binding.btnApprovePermit.alpha = 0.5f
                            binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
                                ContextCompat.getColor(context, R.color.no_request_permit)
                            )
                        }

                    }
//                        ?: run {
//                        // Jika permitStatus null, set state default
//                        binding.btnCheckin.isVisible = false
//                        binding.btnApprovePermit.text = "No Request Permit"
//                        binding.btnApprovePermit.isEnabled = false
//                        binding.btnApprovePermit.alpha = 0.5f
//                        binding.btnApprovePermit.backgroundTintList = ColorStateList.valueOf(
//                            ContextCompat.getColor(context, R.color.no_request_permit)
//                        )
//                    }


//                    binding.imgLogo.loadImage(item.images, R.drawable.ic_bakti)
                    binding.txtTowerName.text = item.ticSite.orDefault()
                    binding.txtTtNumber.text = item.ticId.toString()
//                    if (item.ticSite.isNullOrEmpty()) {
//                        binding.alertNoSite.isVisible = true
//                    } else {
//                        binding.alertNoSite.isVisible = false
//                    }
//                    if (item.ticFieldEngineer == null || item.ticFieldEngineer!!.isEmpty()) {
//                        binding.alertNoEngineer.isVisible = true
//                    } else {
//                        binding.alertNoEngineer.isVisible = false
//                    }

                    binding.txtTechnicianName.text = item.ticFieldEngineer
//                    binding.txtPicName.text = item.ticPersonInCharge
//                    binding.txtDate.text = item.ticUpdated.formatDate("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd h:mm a").orDefault()
                    binding.txtDate.text = item.ticUpdated?.formatDateBelitung("yyyy-MM-dd hh:mm a", "dd MMM yyyy").orDefault()
                    binding.etStatus.text = item.ticStatus.orDefault()
//                    binding.txtCode.text = item.ticSiteId.orDefault()
                    TicketSeverity.fromLabel(item.ticSeverety.str).also {
                        binding.bgHeader.text = it.label.orDefault()
                        binding.bgHeader.backgroundTintList =
                            ColorStateList.valueOf(it.background.colorRes(context))
                        binding.bgHeader.setTextColor(it.foreground.colorRes(context))
                    }


                    TicketStatus.fromLabel(item.ticStatus.str).also {
                        val fg = it.foreground.colorRes(context)
                        val bg = it.background.colorRes(context)
                        TextViewCompat.setCompoundDrawableTintList(
                            binding.etStatus,
                            ColorStateList.valueOf(fg)
                        )
                        binding.etStatus.setTextColor(fg)
                        binding.etStatus.backgroundTintList = ColorStateList.valueOf(bg)
                    }


                    item.status_ticket.ifFalse {
                        val fg = R.color.extra_dark_gray.colorRes(context)
                        val bg = R.color.dark_grey.colorRes(context)

                        binding.bgHeader.backgroundTintList =
                            ColorStateList.valueOf(bg)
                        binding.bgHeader.setTextColor(R.color.white.colorRes(context))

                        binding.etStatus.backgroundTintList = ColorStateList.valueOf(bg)
                        TextViewCompat.setCompoundDrawableTintList(
                            binding.etStatus,
                            ColorStateList.valueOf(fg)
                        )
                        binding.etStatus.setTextColor(fg)
                        binding.background.setBackgroundColor(
                            R.color.bg_ticket_grey.colorRes(
                                context
                            )
                        )
                        binding.txtTtNumber.setTextColor(bg)
                        binding.txtTowerName.setTextColor(bg)
                    }

                    binding.background.setOnClickListener {
                        preferences.selectedTicketId.value = item.ticId
                        context.startActivity(Intent(context, DetilTicketActivity::class.java))
                    }

                    binding.btnCheckin.setOnClickListener {
                        onItemClicked(item)
                    }

                    binding.btnApprovePermit.setOnClickListener {
                        onItemClicked(item)
                    }

                    binding.layoutAlert.setOnClickListener {
                        val permitInformation = item.permitStatus?.permitInformation.takeIf { !it.isNullOrEmpty() }
                            ?: item.submitStatus?.submitApprovedInformation.orDefault("")
                        onAlertClicked(it, permitInformation)
                    }

                    binding.timer.text = parseTimeToDayAndHour(item.ticReceived.toString(), item.ticClosedTime)
                }
            }
        }
    }

}