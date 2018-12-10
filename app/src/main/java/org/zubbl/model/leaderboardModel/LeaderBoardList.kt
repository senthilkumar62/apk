package org.zubbl.model.leaderboardModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class LeaderBoardList {
    @SerializedName("firstName")
    @Expose
    var firstName: String? = null
    @SerializedName("lastName")
    @Expose
    var lastName: String? = null
    @SerializedName("profileImage")
    @Expose
    var profileImage: String? = null
    @SerializedName("rank")
    @Expose
    var rank: Int = 0
    @SerializedName("points")
    @Expose
    var points: String? = null
}