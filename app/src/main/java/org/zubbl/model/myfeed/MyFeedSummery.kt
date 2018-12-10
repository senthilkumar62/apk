package org.zubbl.model.myfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class MyFeedSummery {
    @SerializedName("offersList")
    @Expose
    var offersList: List<OffersList>? = null
}