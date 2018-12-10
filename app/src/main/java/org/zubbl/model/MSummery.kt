package org.zubbl.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class MSummery {
    @SerializedName("merchantDetail")
    @Expose
    var merchantDetail: MerchantDetails? = null
}