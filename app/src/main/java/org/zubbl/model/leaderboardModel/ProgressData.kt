package org.zubbl.model.leaderboardModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class ProgressData {
    @SerializedName("loggedUserTotalPoints")
    @Expose
    var loggedUserTotalPoints: String? = null
    @SerializedName("loggedUserTotalTapIns")
    @Expose
    var loggedUserTotalTapIns: String? = null
    @SerializedName("rankingLevel")
    @Expose
    var rankingLevel: String? = null
    @SerializedName("rank")
    @Expose
    var rank: Int = 0
    @SerializedName("availableDays")
    @Expose
    var availableDays: String? = null
    @SerializedName("availableFromTime")
    @Expose
    var availableFromTime: String? = null
    @SerializedName("availableToTime")
    @Expose
    var availableToTime: String? = null
    @SerializedName("nextRankPoints")
    @Expose
    var nextRankPoints: String? = null
    @SerializedName("nextRankLevel")
    @Expose
    var nextRankLevel: String? = null
    @SerializedName("description")
    @Expose
    var description: String? = null
}