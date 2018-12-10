package org.zubbl.interfaces

import org.zubbl.model.common.JsonResp

interface ServiceListener {


    fun onSuccess(jsonResp: JsonResp, data: String)

        fun onFailure(jsonResp: JsonResp, data: String)

}