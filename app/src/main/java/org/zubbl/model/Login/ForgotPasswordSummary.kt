package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class ForgotPasswordSummary {

    @SerializedName("Forgotpassword")
    @Expose
    var forgotpassword: List<Forgotpassword>? = null
}