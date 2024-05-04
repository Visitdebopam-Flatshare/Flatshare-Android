package com.joinflatshare.api.retrofit

import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.flat.CreateFlatRequest
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.invite.*
import com.joinflatshare.pojo.purchase.PurchaseRequest
import com.joinflatshare.pojo.user.*
import io.reactivex.Observable
import retrofit2.http.*

interface ApiInterface {

    /*------------------------------ USER ------------------------------*/
    @PATCH("users/me")
    fun updateProfile(
        @Body user: User?
    ): Observable<UserResponse?>

    // Verification
    @POST("users/verification/request")
    fun verifiyAdhaar(@Body request: AdhaarRequest): Observable<com.joinflatshare.pojo.BaseResponse?>

    @POST("users/verification/verify")
    fun verifiyAdhaarOtp(@Body request: AdhaarOtp): Observable<com.joinflatshare.pojo.BaseResponse?>

    @POST("users/online/heartbeat")
    fun heartbeat(): Observable<BaseResponse?>

    /*------------------------------ INVITE ------------------------------*/
    @POST("users/app/invite/{phone}")
    fun inviteToApp(@Path("phone") phone: String): Observable<com.joinflatshare.pojo.BaseResponse>

    @POST("users/friends/add/{phone}")
    fun addFriends(@Path("phone") phone: String): Observable<com.joinflatshare.pojo.BaseResponse>

    @POST("flats/add/{id}")
    fun addFlatmate(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @POST("users/status/{type}")
    fun getInvitedStatus(
        @Path("type") type: String,
        @Body request: InvitedRequest
    ): Observable<InvitedResponse>

    @POST("users/app/reqInvite/")
    fun requestInvite(
        @Body request: RequestInvite
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @POST("users/contacts/")
    fun sendContacts(
        @Body request: RequestSavedContacts
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    /*------------------------------ Notification & Requests ------------------------------*/

    @POST()
    fun requestConnection(
        @Url url: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @DELETE()
    fun rejectConnection(
        @Url url: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @DELETE()
    fun removeConnection(
        @Url url: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @DELETE("flats/reject/{id}")
    fun rejectFlatRequest(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @DELETE("users/friends/reject/{id}")
    fun rejectFriendRequest(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @DELETE("users/friends/remove/{id}")
    fun removeFriend(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    /*------------------------------ Flat ------------------------------*/
    @POST("flats/create")
    fun createFlat(@Body request: CreateFlatRequest): Observable<FlatResponse?>

    @PATCH("flats/{id}")
    fun updateFlat(
        @Path("id") id: String,
        @Body dataResponse: MyFlatData?
    ): Observable<FlatResponse?>

    @DELETE("flats/remove/{id}")
    fun removeFlatmate(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    /*------------------------------ FEED ------------------------------*/
    @DELETE()
    fun exploreDisLike(
        @Url url: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @POST("users/likes/reveal/{type}/{phone}")
    fun revealLikes(
        @Path("type") type: String,
        @Path("phone") phone: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>


    /*------------------------------ REPORT ------------------------------*/
    @POST()
    fun report(
        @Url url: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>


    /*------------------------------ PURCHASE ------------------------------*/
    @POST("users/purchase/product")
    fun purchaseOrder(
        @Body request: PurchaseRequest
    ): Observable<com.joinflatshare.pojo.BaseResponse>

}