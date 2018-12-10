package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class ChangePassword {

    @SerializedName("Forgotpassword")
    @Expose
    var forgotpassword: String? = null
}