package id.co.dif.base_project.base

import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding

/***
 * Created by kikiprayudi
 * on Tuesday, 14/03/23 16:00
 *
 */


class BaseViewHolder<VB : ViewBinding>(var binding: VB) : RecyclerView.ViewHolder(binding.root)