package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class MessageModel {


    @SerializedName("Message")
    @Expose
    var message: String? = null

}