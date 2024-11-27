package id.co.dif.base_project.presentation.adapter

import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.UserActivity
import id.co.dif.base_project.databinding.ItemUserActivityBinding
import id.co.dif.base_project.viewmodel.MyDashboardViewModel


class UserActivityAdapter :
    BaseAdapter<MyDashboardViewModel, ItemUserActivityBinding, UserActivity>() {

    var designMaxHeight: Int? = null
    var maxHours: UserActivity? = null
    override val layoutResId: Int
        get() = R.layout.item_user_activity

    fun setListData(listOf: List<UserActivity>) {
        data.clear()
        data.addAll(listOf)
        maxHours = data.maxBy { it.hours }
    }

    override fun onLoadItem(
        binding: ItemUserActivityBinding,
        data: MutableList<UserActivity>,
        position: Int
    ) {

        if (designMaxHeight == null) {
            designMaxHeight = binding.bar.layoutParams.height
        }
        val item = data[position]
        val percentage = item.hours.toFloat() / maxHours!!.hours
        val newHeight = designMaxHeight!! * percentage
        val params = binding.bar.layoutParams
        params.height = newHeight.toInt()
        binding.bar.layoutParams = params
    }
}