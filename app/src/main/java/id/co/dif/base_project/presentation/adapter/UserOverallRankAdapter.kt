package id.co.dif.base_project.presentation.adapter

import android.annotation.SuppressLint
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewHolder
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.ScoreDetail
import id.co.dif.base_project.data.UserOverallRank
import id.co.dif.base_project.databinding.ItemNotificationLoadingBinding
import id.co.dif.base_project.databinding.ItemUsageRankBinding
import id.co.dif.base_project.utils.gravatar
import id.co.dif.base_project.utils.isNotNullOrEmpty
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.shimmerDrawable

class UserOverallRankAdapter(data: List<UserOverallRank>) :
    BaseAdapter<BaseViewModel, ViewBinding, UserOverallRank>() {
    override val layoutResId: Int
        get() = R.layout.item_usage_rank

    companion object {
        const val ITEM_VIEW_TYPE_CONTENT = 1
        const val ITEM_VIEW_TYPE_CONTENT_GRAYED = 3
        const val ITEM_VIEW_TYPE_LOADING = 2
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): BaseViewHolder<ViewBinding> {
        context = parent.context
        val inflater = LayoutInflater.from(parent.context)
        Log.d("TAG", "onCreateVifewHolder: $viewType")
        val binding = when (viewType) {
            TroubleTicketAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                ItemUsageRankBinding.inflate(inflater, parent, false)
            }

            TroubleTicketAdapter.ITEM_VIEW_TYPE_LOADING -> {
                ItemNotificationLoadingBinding.inflate(inflater, parent, false)
            }

            else -> throw Error("Unrecognized view type $viewType")
        }

        viewModel = ViewModelProvider.NewInstanceFactory().create(getViewModelClass())
        return BaseViewHolder(binding)
    }

    var isLoading = false

    init {
        this.data.addAll(data)
    }

    @SuppressLint("SetTextI18n")
    override fun onLoadItem(
        binding: ViewBinding,
        data: MutableList<UserOverallRank>,
        position: Int
    ) {
        when (getItemViewType(position)) {
            NotificationAdapter.ITEM_VIEW_TYPE_CONTENT -> {
                (binding as ItemUsageRankBinding)
                val userOverallRank = data[position]
                binding.userOverallRank = userOverallRank
                var imageUrl = userOverallRank.photo
                if (imageUrl.isNullOrEmpty()) imageUrl = gravatar(userOverallRank.name ?: "user")
                binding.imgProfilePic.loadImage(
                    imageUrl,
                    shimmerDrawable()
                )
                binding.position = position + 1
                val content = SpannableString(userOverallRank.score.toString())
                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                binding.tvProfileCompletion.text = content

                content.setSpan(UnderlineSpan(), 0, content.length, 0)
                val medalDrawableId = when (position + 1) {
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
    }


    fun removeLoadingFooter() {
        Log.d("TAG", "removeLoadingFooter: ${data.size}")
        isLoading = false
        val position = data.lastIndex
        if (data.lastOrNull() != null) {
            data.removeLast()
            notifyItemRemoved(position)
        }
    }

    fun addAll(newData: List<UserOverallRank>) {
        for (result in newData) {
            add(result)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == data.lastIndex && isLoading) TroubleTicketAdapter.ITEM_VIEW_TYPE_LOADING else TroubleTicketAdapter.ITEM_VIEW_TYPE_CONTENT
    }

    fun addLoadingFooter() {
        isLoading = true
        add(
            UserOverallRank(
                "",
                0,
                "",
                "",
                0,
                ScoreDetail(
                    0,
                    0,
                    0,
                    0,
                    0,
                    0
                ),
                0,
                0,
                0,
                0,
                0,
                0,
                0
            )
        )
        Log.d("TAG", "addLoadingFooter: ${data.size} $isLoading")
    }

    fun add(userOverallRank: UserOverallRank) {
        data.add(userOverallRank)
        notifyItemInserted(data.lastIndex)
    }
}