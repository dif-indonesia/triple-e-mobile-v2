package id.co.dif.base_project.data

class ChatNotification (

    var chat_content: String,
    var chat_receiver: String,
    var chat_sender: String,
    var chat_show: Boolean,
    var chat_time: String,
    var id: Int

) : BaseData()