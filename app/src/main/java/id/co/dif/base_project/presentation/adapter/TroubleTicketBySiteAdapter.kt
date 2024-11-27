package id.co.dif.base_project.presentation.adapter

//import id.co.dif.base_project.utils.parseTimeToDayAndHour
//import java.time.Duration
//import java.time.LocalDate
//import java.time.LocalDateTime
//import java.time.format.DateTimeFormatter
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
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
import id.co.dif.base_project.databinding.ItemTroubleTicketBinding
import id.co.dif.base_project.presentation.activity.DetilTicketActivity
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.formatDate
import id.co.dif.base_project.utils.ifFalse
import id.co.dif.base_project.utils.str
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/***
 * Created by kikiprayudi
 * on Tuesday, 21/03/23 10:46
 *
 */


class TroubleTicketBySiteAdapter() :
    BaseAdapter<BaseViewModel, ViewBinding, TroubleTicket>() {

    override val layoutResId = R.layout.item_trouble_ticket

    val startDate = Date() // Replace with your actual start date
    val endDate = Date() // Replace with your actual end date
    val remainingTime = endDate.time - startDate.time

    var timeInMillis = System.currentTimeMillis()
    val text: String = TimeAgo.using(timeInMillis)

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_CONTENT_GRAYED = 3
        const val ITEM_VIEW_TYPE_LOADING = 2
    }


    init {
        this.data.addAll(data)
    }


    var isLoading = false

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        val binding = when (viewType) {
            ITEM_VIEW_TYPE_CONTENT -> {
                ItemTroubleTicketBinding.inflate(inflater, parent, false)
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

    //    @RequiresApi(Build.VERSION_CODES.O)
    fun parseTimeToDayAndHour(timeString: String): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance()
        val parsedTime = dateFormat.parse(timeString)
//        parsedTime.time = dateFormat.parse(timeString)
        val durationMillis = now.timeInMillis - parsedTime.time

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

//    fun changeHour(time: String): String {
//        val timeString = "2023-06-10 18:30:00"
//        return parseTimeToDayAndHour(timeString)
//    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<TroubleTicket>,
        position: Int,

        ) {
        context?.let { context ->

            when (getItemViewType(position)) {
                NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                    binding as ItemTroubleTicketBinding
                    val item = data[position]
                    binding.txtTowerName.text = item.ticSite
                    //               binding.txtTtNumber.text = item.ticNumber.toString()
                    binding.txtTtNumber.text = item.ticId.toString()
                    binding.txtTechnicianName.text = item.ticFieldEngineer
                    binding.txtPicName.text = item.ticPersonInCharge
                    binding.txtDate.text =
                        item.ticUpdated.formatDate("yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd h:mm a")
                    binding.etStatus.text = item.ticStatus
                    binding.txtCode.text = item.ticSiteId
                    Log.d("TAG", "onLoadItem: $item")
                    TicketSeverity.fromLabel(item.ticSeverety.str).also {
                        binding.bgHeader.text = it.label
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
                        preferences.selectedTicketId.value  =item.ticId
                        context?.startActivity(Intent(context, DetilTicketActivity::class.java))
                    }

                    binding.timer.text = parseTimeToDayAndHour(item.ticReceived.toString())
                }

            }
        }
    }
}