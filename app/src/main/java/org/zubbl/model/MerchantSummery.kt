package org.zubbl.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class MerchantSummery {
    @SerializedName("merchantDetail")
    @Expose
    var merchantDetails: MerchantDetails? = null
}