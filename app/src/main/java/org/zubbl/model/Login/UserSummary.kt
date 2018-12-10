package org.zubbl.model.Login

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

class UserSummary {
    @SerializedName("userInformation")
    @Expose
    var userInformation: List<UserInformation>? = null
}