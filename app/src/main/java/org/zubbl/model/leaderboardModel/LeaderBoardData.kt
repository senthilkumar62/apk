package org.zubbl.model.leaderboardModel

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName



class LeaderBoardData {
    @SerializedName("leaderBoardList")
    @Expose
    var leaderBoardList: List<LeaderBoardList>? = null
    @SerializedName("progressData")
    @Expose
    var progressData: ProgressData? = null
}