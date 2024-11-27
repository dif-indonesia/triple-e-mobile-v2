package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class Chat(
    var chat_id: Int,
    var chat_receiver: Int,
    var chat_sender: Int,
    var date: String

): BaseData()
