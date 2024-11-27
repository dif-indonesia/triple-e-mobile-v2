package id.co.dif.base_project.presentation.adapter

import android.view.View
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.TopUser
import id.co.dif.base_project.databinding.ItemTopUserBinding
import id.co.dif.base_project.utils.gravatar
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.TeamDashboardViewModel

class TopUsersAdapter(data: List<TopUser> = listOf()) :
    BaseAdapter<TeamDashboardViewModel, ItemTopUserBinding, TopUser>() {
    override val layoutResId: Int
        get() = R.layout.item_top_user

    init {
        this.data.addAll(data)
    }

    override fun onLoadItem(
        binding: ItemTopUserBinding,
        data: MutableList<TopUser>,
        position: Int
    ) {
        val topUser = data[position]
        binding.imgProfilePic.loadImage(
            topUser.userPictureUrl ?: gravatar(topUser.username), shimmerDrawable()
        )
        binding.topUser = topUser
        if (position == data.size - 1) binding.dividerItem.visibility = View.GONE

    }
}