package id.co.dif.base_project.presentation.adapter

import android.util.Log
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.databinding.ItemSparePartBinding
import id.co.dif.base_project.utils.orDefault

class SparePartAdapter(override val layoutResId: Int = R.layout.item_spare_part) :
    BaseAdapter<BaseViewModel, ItemSparePartBinding, SparePart>() {

    var onMoreVertClicked: (sparePart: SparePart) -> Unit = { _ -> }
    override fun onLoadItem(
        binding: ItemSparePartBinding,
        data: MutableList<SparePart>,
        position: Int
    ) = with(binding) {
        val item = data[position]
        tvId.text = context?.getString(R.string.id, item.spreqId.toString())
        tvValueLastUpdate.text = item.spreqUpdate.orDefault("")
        tvValueStatus.text = item.spreqStatus.orDefault("")
        tvValueArticleName.text = item.spreqName.orDefault("")
        tvValueArticleCode.text = item.spreqCode.orDefault("")
        tvValueLocation.text = item.spreqNscluster.orDefault("")
        tvValueVendor.text = item.spreqVendor.orDefault("")
        tvValueFollowUp.text = item.spreqFollowup.orDefault("")
        tvValueCreated.text = item.spreqRequestTime.orDefault("")
        tvValueSerialQuantity.text = item.spreqQuantity.toString().orDefault("")
        tvValueFaulty.text = item.spreqSnf.orDefault("")
        tvValueSystem.text = item.spreqSystem.orDefault("")
        tvValueTreatment.text = item.spreqTreatment.orDefault("")
        tvValueTicket.text = item.spreqTicket.orDefault("")
        tvValueSerialNumber.text = item.spreqSn.orDefault("")
        Log.d("TAG", "onLoadItem sn: $item")
        delete.setOnClickListener {
            onMoreVertClicked(item)
        }
    }
}