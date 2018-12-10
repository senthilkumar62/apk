package org.zubbl.model.leaderboardModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class LBSummary {
    @SerializedName("leaderBoardData")
    @Expose
    var leaderBoardData: LeaderBoardData? = null
}