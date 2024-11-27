package id.co.dif.base_project.presentation.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView.Adapter
import com.google.gson.GsonBuilder
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseViewHolder
import id.co.dif.base_project.data.Regional
import id.co.dif.base_project.databinding.ItemAreaAccesBinding
import id.co.dif.base_project.databinding.ItemClusterBinding
import id.co.dif.base_project.presentation.activity.AreaAccesViewModel

class RegionalAccesAdapter() :
    id.co.dif.base_project.base.BaseAdapter<AreaAccesViewModel, ItemAreaAccesBinding, Regional>() {
    override val layoutResId = R.layout.item_area_acces

    var selectedItem = mutableListOf<Regional>()

    override fun onLoadItem(
        binding: ItemAreaAccesBinding,
        data: MutableList<Regional>,
        regionalPosition: Int
    ) {
        val regionalItem = data[regionalPosition]

        val selectedRegional: Regional? = selectedItem.firstOrNull {
            it.regional == regionalItem.regional
        }

        binding.regional.isChecked = selectedRegional != null
        binding.recyclerView.isVisible = selectedRegional != null

        binding.regional.setOnCheckedChangeListener { buttonView, isChecked ->
            val selectedRegional: Regional? = selectedItem.firstOrNull {
                it.regional == regionalItem.regional
            }
            if (selectedRegional == null && isChecked) {
                selectedItem.add(Regional(mutableListOf(), regionalItem.regional))
            } else if (selectedRegional != null && !isChecked) {
                selectedItem.remove(selectedRegional)
            }
            selectedItem.sortBy { it.regional }
            binding.recyclerView.isVisible = isChecked
        }

        if (regionalItem.regional!!.isNotEmpty()) {
            binding.regional.text = regionalItem.regional.toString()
        }

        if (regionalItem.nscluster!!.isNotEmpty()) {
            val adapter = object : Adapter<BaseViewHolder<ItemClusterBinding>>() {
                override fun onCreateViewHolder(
                    parent: ViewGroup,
                    viewType: Int
                ): BaseViewHolder<ItemClusterBinding> {
                    val binding = ItemClusterBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                    return BaseViewHolder(binding)
                }

                override fun getItemCount() = regionalItem.nscluster?.size ?: 0

                override fun onBindViewHolder(
                    holder: BaseViewHolder<ItemClusterBinding>,
                    clusterPosition: Int
                ) {
                    val childBinding = holder.binding
                    val childItem = regionalItem.nscluster?.get(clusterPosition)
                    childBinding.cluster.text = childItem

                    val selectedRegional: Regional? = selectedItem.firstOrNull {
                        it.regional == regionalItem.regional
                    }
                    val isSelectedCluster = selectedRegional?.nscluster?.contains(childItem)
                    childBinding.cluster.isChecked = isSelectedCluster == true

                    childBinding.cluster.setOnCheckedChangeListener { buttonView, isChecked ->
                        val selectedRegional: Regional? = selectedItem.firstOrNull {
                            it.regional == regionalItem.regional
                        }
                        val isSelectedCluster = selectedRegional?.nscluster?.contains(childItem)
                        if (isSelectedCluster == false && isChecked) {
                            selectedRegional.nscluster?.add(childItem!!)
                        } else if (isSelectedCluster == true && !isChecked) {
                            selectedRegional.nscluster?.remove(childItem!!)
                        }
                        selectedRegional?.nscluster?.sort()
                    }
                }

            }
            binding.recyclerView.adapter = adapter
        }
    }

}