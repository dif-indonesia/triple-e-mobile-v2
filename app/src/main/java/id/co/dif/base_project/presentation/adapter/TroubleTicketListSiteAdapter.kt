package id.co.dif.base_project.presentation.adapter

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import androidx.annotation.RequiresApi
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.SiteHistory
import id.co.dif.base_project.databinding.ItemTicketListSiteBinding
import id.co.dif.base_project.presentation.activity.DetilTicketActivity
import id.co.dif.base_project.viewmodel.TicketListSiteViewModel
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.concurrent.TimeUnit


class TroubleTicketListSiteAdapter : BaseAdapter<TicketListSiteViewModel, ItemTicketListSiteBinding, SiteHistory>() {

    override val layoutResId = R.layout.item_ticket_list_site

    val startDate = Date() // Replace with your actual start date
    val endDate = Date() // Replace with your actual end date
    val remainingTime = endDate.time - startDate.time

    override fun onLoadItem(
        binding: ItemTicketListSiteBinding,
        data: MutableList<SiteHistory>,
        position: Int
    ) {
        val item = data[position]

        binding.timer.text = item.created
        binding.txtTechnicianName.text = item.assign_to
        binding.etStatus.text = item.status
        binding.txtTtNumber.text = item.ticket_number
        binding.txtTowerName.text = item.ticket_number
        binding.bgHeader.text = item.severety
        binding.txtDate.text = item.created


        //          Collor Severety Ticket
        if (item.status == "Emergency") {
            binding.bgHeader.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFDFDF")))
            binding.bgHeader.setTextColor(Color.parseColor("#FF5151"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.bgHeader.compoundDrawableTintList =
                    ColorStateList.valueOf(Color.parseColor("#F0EEFF"))
            }
        }
        else if (item.status == "Major") {
            binding.bgHeader.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFF7E5")))
            binding.bgHeader.setTextColor(Color.parseColor("#EEB22B"))
        }
        else if (item.status == "Minor") {
            binding.bgHeader.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F1FFDE")))
            binding.bgHeader.setTextColor(Color.parseColor("#7EB437"))
        }
        else if (item.status == "Low") {
            binding.bgHeader.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#E5FBFF")))
            binding.bgHeader.setTextColor(Color.parseColor("#24A5BF"))
        }


//          Collor Status Ticket
        if (item.status == "On-Progress") {
            binding.etStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#F0EEFF")))
            binding.etStatus.setTextColor(Color.parseColor("#D13AD4"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etStatus.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#F0EEFF"))
            }
        } else if (item.status == "Rejected") {
            binding.etStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFE2E6")))
            binding.etStatus.setTextColor(Color.parseColor("#EE4545"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etStatus.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#EE4545"))
            }
        } else if (item.status == "Escalated") {
            binding.etStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFDC2")))
            binding.etStatus.setTextColor(Color.parseColor("#858004"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etStatus.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#858004"))
            }
        } else if (item.status == "Completed") {
            binding.etStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ECECFF")))
            binding.etStatus.setTextColor(Color.parseColor("#524FDF"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etStatus.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#524FDF"))
            }
        } else if (item.status == "Assigned") {
            binding.etStatus.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D4FFE7")))
            binding.etStatus.setTextColor(Color.parseColor("#027E38"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.etStatus.compoundDrawableTintList = ColorStateList.valueOf(Color.parseColor("#027E38"))
            }
        }

        binding.detil.setOnClickListener {
            preferences.siteHistory.value = item
            context?.startActivity(Intent(context, DetilTicketActivity::class.java))
        }

        val countDownTimer = object : CountDownTimer(remainingTime, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val days = TimeUnit.MILLISECONDS.toDays(millisUntilFinished)
                val hours = TimeUnit.MILLISECONDS.toHours(millisUntilFinished) % 24
                val minutes = TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) % 60
                val seconds = TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) % 60

                val formattedTime = String.format(
                    "%02d:%02d:%02d:%02d",
                    days, hours, minutes, seconds
                )

                // Update the UI with the formatted time
//                binding.timer.text = item.ticReceived.formatDate(formattedTime)
                binding.timer.text = item.created
            }

            @RequiresApi(Build.VERSION_CODES.O)
            override fun onFinish() {
                if(item.created !== null) {
                    val dates = item.created!!.split("-").toTypedArray()
                    println(dates[0])
                    val startDate = LocalDate.of(dates[0].toInt(), dates[1].toInt(), dates[2].toInt())
                    val currentDate = LocalDate.now()
                    var daysCount = 0;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        daysCount = ChronoUnit.DAYS.between(startDate, currentDate).toInt()
                        binding.timer.text = daysCount.toString() + " Days"
                    }
                } else {
                    binding.timer.text = "Ticket finished!"
                    TODO("VERSION.SDK_INT < O")
                }
                // Timer finished, perform any desired action

            }
        }
        countDownTimer.start()
    }



}