package id.co.dif.base_project.utils

sealed class TrendType {
    companion object{
        const val DAILY = "daily"
        const val MONTHLY = "month"
        const val YEARLY = "yearly"
    }
}