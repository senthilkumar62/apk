package org.zubbl.model.myfeed

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import org.zubbl.model.UserProfile



class OffersList {
    @SerializedName("remainingUsers")
    @Expose
    var remainingUsers: Int = 0
    @SerializedName("offerId")
    @Expose
    var offerId: String? = null
    @SerializedName("userProfile")
    @Expose
    var userProfile: List<UserProfile>? = null
    @SerializedName("offerTitle")
    @Expose
    var offerTitle: String? = null
    @SerializedName("totalUsers")
    @Expose
    var totalUsers: String? = null
    @SerializedName("joinedUsers")
    @Expose
    var joinedUsers: Int = 0
    @SerializedName("fromDate")
    @Expose
    var fromDate: String? = null
    @SerializedName("validityHours")
    @Expose
    var validityHours: String? = null
    @SerializedName("merchantAddress")
    @Expose
    var merchantAddress: String? = null
    @SerializedName("merchantImage")
    @Expose
    var merchantImage: String? = null
    @SerializedName("zubblActiveStatus")
    @Expose
    var zubblActiveStatus: String? = null
}