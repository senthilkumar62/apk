package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class Forgotpassword {
    @SerializedName("Message")
    @Expose
    var message: String? = null
    @SerializedName("OTP")
    @Expose
    var oTP: String? = null
    @SerializedName("activationPassword")
    @Expose
    var activationPassword: String? = null
}