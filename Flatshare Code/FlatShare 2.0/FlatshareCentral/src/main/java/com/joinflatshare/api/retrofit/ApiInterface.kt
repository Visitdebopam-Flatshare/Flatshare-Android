package com.joinflatshare.api.retrofit

import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.flat.CreateFlatRequest
import com.joinflatshare.pojo.flat.FlatResponse
import com.joinflatshare.pojo.flat.MyFlatData
import com.joinflatshare.pojo.invite.InvitedRequest
import com.joinflatshare.pojo.invite.InvitedResponse
import com.joinflatshare.pojo.invite.RequestSavedContacts
import io.reactivex.Observable
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiInterface {


    @POST("users/online/heartbeat")
    fun heartbeat(): Observable<BaseResponse?>

    /*------------------------------ INVITE ------------------------------*/
    @POST("users/app/invite/{phone}")
    fun inviteToApp(@Path("phone") phone: String): Observable<BaseResponse>

    @POST("users/friends/add/{phone}")
    fun addFriends(@Path("phone") phone: String): Observable<BaseResponse>

    @POST("flats/add/{id}")
    fun addFlatmate(
        @Path("id") id: String
    ): Observable<BaseResponse>

    @POST("users/status/{type}")
    fun getInvitedStatus(
        @Path("type") type: String,
        @Body request: InvitedRequest
    ): Observable<InvitedResponse>

    @POST("users/contacts/")
    fun sendContacts(
        @Body request: RequestSavedContacts
    ): Observable<BaseResponse>

    /*------------------------------ Notification & Requests ------------------------------*/

    @DELETE()
    fun removeConnection(
        @Url url: String
    ): Observable<BaseResponse>

    @DELETE("flats/reject/{id}")
    fun rejectFlatRequest(
        @Path("id") id: String
    ): Observable<BaseResponse>

    /*------------------------------ Flat ------------------------------*/
    @POST("flats/create")
    fun createFlat(@Body request: CreateFlatRequest): Observable<FlatResponse?>

    @PATCH("flats/{id}")
    fun updateFlat(
        @Path("id") id: String?,
        @Body dataResponse: MyFlatData?
    ): Observable<FlatResponse?>

    @DELETE("flats/remove/{id}")
    fun removeFlatmate(
        @Path("id") id: String
    ): Observable<BaseResponse>

    /*------------------------------ FEED ------------------------------*/
    @POST("users/likes/reveal/{type}/{phone}")
    fun revealLikes(
        @Path("type") type: String,
        @Path("phone") phone: String
    ): Observable<BaseResponse>


    /*------------------------------ REPORT ------------------------------*/
    @POST()
    fun report(
        @Url url: String
    ): Observable<BaseResponse>
}