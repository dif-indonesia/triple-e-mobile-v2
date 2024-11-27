package id.co.dif.base_project.presentation.adapter

import android.annotation.SuppressLint
import androidx.core.content.ContextCompat
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.data.UsageRank
import id.co.dif.base_project.databinding.ItemUsageRankBinding
import id.co.dif.base_project.utils.gravatar
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.viewmodel.TeamDashboardViewModel

class UsageRankAdapter(data: List<UsageRank>) :
    BaseAdapter<TeamDashboardViewModel, ItemUsageRankBinding, UsageRank>() {
    override val layoutResId: Int
        get() = R.layout.item_usage_rank
    init {
        this.data.addAll(data)
    }
    @SuppressLint("SetTextI18n")
    override fun onLoadItem(
        binding: ItemUsageRankBinding,
        data: MutableList<UsageRank>,
        position: Int
    ) {
        val usageRank = data[position]
//        binding.usageRank = usageRank
        binding.imgProfilePic.loadImage(
            usageRank.profilePicUrl ?: gravatar(usageRank.username), shimmerDrawable()
        )
        val medalDrawableId = when (usageRank.position) {
            1 -> R.drawable.bg_medal_1
            2 -> R.drawable.bg_medal_2
            3 -> R.drawable.bg_medal_3
            else -> R.color.white
        }
        val medalDrawable = ContextCompat.getDrawable(context!!, medalDrawableId)
        val textColorId = if (position > 2) R.color.black else R.color.white
        binding.tvPosition.setTextColor(ContextCompat.getColor(context!!, textColorId))
        binding.tvPosition.background = medalDrawable
    }
}