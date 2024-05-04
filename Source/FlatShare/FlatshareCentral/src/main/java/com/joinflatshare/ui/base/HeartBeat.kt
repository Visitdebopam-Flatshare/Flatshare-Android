package com.joinflatshare.ui.base

import com.joinflatshare.api.retrofit.ApiManager
import com.joinflatshare.utils.helper.CommonMethods
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit


/**
 * Created by debopam on 05/09/23
 */
class HeartBeat {
    private val TAG = HeartBeat::class.java.simpleName

    private fun callHeartBeat() {
        CommonMethods.makeLog(TAG, "Calling Heart Beat")
        val apiManager = ApiManager()
        apiManager.heartBeat { }
    }

    fun scheduleRecursiveTask() {
        val scheduler: ScheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
        scheduler.scheduleAtFixedRate({
            callHeartBeat()
        }, 0, 30, TimeUnit.SECONDS)
    }
}