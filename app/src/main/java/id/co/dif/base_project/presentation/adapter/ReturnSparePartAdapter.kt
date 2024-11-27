package id.co.dif.base_project.presentation.adapter

import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseAdapter
import id.co.dif.base_project.base.BaseViewModel
import id.co.dif.base_project.data.Note
import id.co.dif.base_project.databinding.ItemReturnSparePartBinding


class ReturnSparePartAdapter() : BaseAdapter<BaseViewModel, ItemReturnSparePartBinding, Note>() {



    override val layoutResId = R.layout.item_return_spare_part

    override fun onLoadItem(
        binding: ItemReturnSparePartBinding,
        data: MutableList<Note>,
        position: Int
    )  {
//        val item = data[position]
//        binding.idLastUpdate.text = item.time
//        binding.idFollowUp.text = item.date
//        binding.idTicket.text = item.tic_status
//        binding.idCreated.text = item.username
//        binding.idArticleName.text = item.username
//        binding.idArticleCode.text = item.note
//        binding.idSerialNumber.text = item.time
//        binding.idQuantity.text = item.tic_status
//        binding.idCity.text = item.username
//        binding.idPic.text = item.username
//        binding.idRequest.text = item.note
//        binding.idSystem.text = item.username
//        binding.idVendor.text = item.username
//        binding.idTreatment.text = item.note


    }

    override fun getItemCount(): Int {
        return 5
    }



}