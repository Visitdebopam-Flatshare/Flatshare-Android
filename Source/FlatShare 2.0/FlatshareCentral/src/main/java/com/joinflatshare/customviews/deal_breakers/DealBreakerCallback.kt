package com.joinflatshare.customviews.deal_breakers

/**
 * Created by debopam on 17/12/22
 */
interface DealBreakerCallback {
    fun onSmokingSelected(constant: Int)
    fun onNonVegSelected(constant: Int)
    fun onPartySelected(constant: Int)
    fun onEggsSelected(constant: Int)
    fun onWorkoutSelected(constant: Int)
    fun onPetsSelected(constant: Int)
}