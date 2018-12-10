package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class UserInformation {
    @SerializedName("User_Id")
    @Expose
    var userId: String? = null
    @SerializedName("User_name")
    @Expose
    var userName: String? = null
    @SerializedName("Firstname")
    @Expose
    var firstname: String? = null
    @SerializedName("Lastname")
    @Expose
    var lastname: String? = null
    @SerializedName("Email")
    @Expose
    var email: String? = null
    @SerializedName("loginToken")
    @Expose
    var loginToken: String? = null
    @SerializedName("message")
    @Expose
    var message: String? = null

}