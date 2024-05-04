package com.joinflatshare.interfaces

import com.joinflatshare.pojo.flat.FlatResponse

/**
 * Created by debopam on 01/02/24
 */
fun interface OnFlatFetched {
    fun flatFetched(resp: FlatResponse?)
}