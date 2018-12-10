package org.zubbl.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class NearBy {
    @SerializedName("merchantDetail")
    @Expose
    var merchantDetail: List<MerchantDetails>? = null
}