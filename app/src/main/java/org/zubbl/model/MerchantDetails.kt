package org.zubbl.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class MerchantDetails {
    @SerializedName("Id")
    @Expose
    var id: String? = null
    @SerializedName("merchantId")
    @Expose
    var merchantId: String? = null

    @SerializedName("remainingUsers")
    @Expose
    var remainingUsers: Int = 0

    @SerializedName("offerId")
    @Expose
    var offerId: String? = null

    @SerializedName("lat")
    @Expose
    var lat: String? = null
    @SerializedName("lng")
    @Expose
    var lng: String? = null
    @SerializedName("userProfile")
    @Expose
    var userProfile: List<UserProfile>? = null
    @SerializedName("profileImage")
    @Expose
    var profileImage: String? = null
    @SerializedName("name")
    @Expose
    var name: String? = null
    @SerializedName("emailId")
    @Expose
    var emailId: String? = null
    @SerializedName("venueAddress1")
    @Expose
    var venueAddress1: String? = null
    @SerializedName("totalPoints")
    @Expose
    var totalPoints: String? = null
    @SerializedName("tapInPoints")
    @Expose
    var tapInPoints: String? = null
    @SerializedName("telephone")
    @Expose
    var telephone: String? = null
    @SerializedName("lastCheckin")
    @Expose
    var lastCheckin: String? = null
    @SerializedName("Message")
    @Expose
    var message: String? = null

    @SerializedName("venueName")
    @Expose
    var venueName: String? = null

    @SerializedName("venueAddress2")
    @Expose
    var venueAddress2: String? = null
    @SerializedName("joinedMembers")
    @Expose
    var joinedMembers: Int = 0
    @SerializedName("totalMembers")
    @Expose
    var totalMembers: String? = null
    @SerializedName("validity")
    @Expose
    var validity: Any? = null
    @SerializedName("joinedStatus")
    @Expose
    var joinedStatus: Boolean = false
    @SerializedName("lockedStatus")
    @Expose
    var lockedStatus: Boolean = false
    @SerializedName("zubblPoints")
    @Expose
    var zubblPoints: Int = 0

}