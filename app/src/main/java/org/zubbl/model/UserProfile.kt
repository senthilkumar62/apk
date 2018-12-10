package org.zubbl.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class UserProfile {

    @SerializedName("userProfile")
    @Expose
    var userProfile: String? = null

}