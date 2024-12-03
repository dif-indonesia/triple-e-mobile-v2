package id.co.dif.base_project.data

import androidx.annotation.ColorRes
import id.co.dif.base_project.R

sealed class TicketStatus(
    val label: String,
    @ColorRes val foreground: Int,
    @ColorRes val background: Int
) {
    object Assigned : TicketStatus(
        label = "Assigned",
        background = R.color.dark_green,
        foreground = R.color.dark_green
    )

    object OnProgress : TicketStatus(
        label = "On Progress",
        background = R.color.dark_pink,
        foreground = R.color.dark_pink,
    )

    object OnGoing : TicketStatus(
        label = "On Going",
        background = R.color.dark_pink,
        foreground = R.color.dark_pink,
    )

    object Escalated : TicketStatus(
        label = "Escalated",
        background = R.color.alpha_20_green_moss,
        foreground = R.color.green_moss,
    )

    object Completed : TicketStatus(
        label = "Completed",
        background = R.color.dark_purple,
        foreground = R.color.dark_purple,
    )

    object Pending : TicketStatus(
        label = "Pending",
        background = R.color.red,
        foreground = R.color.red,
    )

    object Closed : TicketStatus(
        label = "Closed",
        background = R.color.black,
        foreground = R.color.black,
    )

    object Incident : TicketStatus(
        label = "Incident",
        background = R.color.black,
        foreground = R.color.black,
    )

    object Event : TicketStatus(
        label = "Event",
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
                "assigned" -> Assigned
                "on progress" -> OnProgress
                "on going" -> OnGoing
                "escalated" -> Escalated
                "completed" -> Completed
                "pending" -> Pending
                "closed" -> Closed
                "Incident" -> Incident
                "Event" -> Event
                else -> Other
            }
        }

        fun getAll(except: List<TicketStatus> = listOf()): List<TicketStatus> {
            val all = mutableListOf(Assigned, OnProgress, OnGoing, Escalated, Completed, Pending, Closed)
            except.forEach { all.remove(it) }
            return all
        }
    }
}