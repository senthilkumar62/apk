package org.zubbl.model.common


data class JsonResp(var url: String, var method: String, var strRequest: String, var strResponse: String, var responseCode: Int, var requestCode: Int, var errorMsg: String, var requestData: String, var isOnline: Boolean, var headers: MutableMap<String, MutableList<String>>?) {

    fun clearAll() {
        this.url = ""
        this.method = ""
        this.errorMsg = ""
        this.strRequest = ""
        this.strResponse = ""
        this.requestData = ""
        this.requestCode = 0
        this.responseCode = 0
        this.isOnline = false
        this.headers = null
    }
}
