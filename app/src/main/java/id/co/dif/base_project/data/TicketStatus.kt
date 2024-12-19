package id.co.dif.base_project.data

import androidx.annotation.ColorRes
import id.co.dif.base_project.R

sealed class TicketStatus(
    val label: String,
    @ColorRes val foreground: Int,
    @ColorRes val background: Int
) {
    object ASSIGNED : TicketStatus(
        label = "assigned",
        background = R.color.dark_green,
        foreground = R.color.dark_green
    )

    object INPROGRESS : TicketStatus(
        label = "in progress",
        background = R.color.dark_pink,
        foreground = R.color.dark_pink,
    )

    object ONPROGRESS : TicketStatus(
        label = "onprogress",
        background = R.color.dark_pink,
        foreground = R.color.dark_pink,
    )

    object CANCELED : TicketStatus(
        label = "canceled",
        background = R.color.dark_pink,
        foreground = R.color.dark_pink,
    )

    object ESCALATED : TicketStatus(
        label = "escalated",
        background = R.color.alpha_20_green_moss,
        foreground = R.color.green_moss,
    )

    object SUBMITTED : TicketStatus(
        label = "submitted",
        background = R.color.dark_purple,
        foreground = R.color.dark_purple,
    )

    object RESOLVED : TicketStatus(
        label = "resolved",
        background = R.color.red,
        foreground = R.color.red,
    )

    object Pending : TicketStatus(
        label = "pending",
        background = R.color.light_orange,
        foreground = R.color.light_orange,
    )

    object CLOSED : TicketStatus(
        label = "closed",
        background = R.color.black,
        foreground = R.color.black,
    )

    object Incident : TicketStatus(
        label = "incident",
        background = R.color.black,
        foreground = R.color.black,
    )

    object Event : TicketStatus(
        label = "event",
        background = R.color.black,
        foreground = R.color.black,
    )

    object Other : TicketStatus(
        label = "Other",
        background = R.color.transparent,
        foreground = R.color.transparent,
    )


    companion object {
        fun fromLabel(label: String): TicketStatus {
            return when (label.lowercase()) {
                "assigned" -> ASSIGNED
                "in progress" -> INPROGRESS
                "onprogress" -> ONPROGRESS
                "canceled" -> CANCELED
                "escalated" -> ESCALATED
                "submitted" -> SUBMITTED
                "resolved" -> RESOLVED
                "closed" -> CLOSED
                "incident" -> Incident
                "event" -> Event
                "pending" -> Pending
                else -> Other
            }
        }

        fun getAll(except: List<TicketStatus> = listOf()): List<TicketStatus> {
            val all = mutableListOf(ASSIGNED, ONPROGRESS, INPROGRESS, CANCELED, ESCALATED, SUBMITTED, RESOLVED, CLOSED)
            except.forEach { all.remove(it) }
            return all
        }
    }
}