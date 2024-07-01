package com.joinflatshare.webservice.api.interfaces

import com.joinflatshare.pojo.BaseResponse
import com.joinflatshare.pojo.likes.LikeRequest
import com.joinflatshare.pojo.user.AdhaarOtp
import com.joinflatshare.pojo.user.AdhaarRequest
import com.joinflatshare.pojo.user.User
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Url

interface ApiInterface {

    // App Constants
    @GET("admin/config")
    fun getConfig(): Observable<Response<ResponseBody>>

    @GET("admin/inBoundary/{latitude}/{longitude}")
    fun getCityNameForLocation(
        @Path("latitude") latitude: String?,
        @Path("longitude") longitude: String?
    ): Observable<Response<ResponseBody>>

    // Registration & Login
    @POST("admin/sendOtp/{phone}")
    fun sendOtp(
        @Path("phone") phone: String?
    ): Observable<Response<ResponseBody>>

    @POST("login/auth")
    fun login(@Body request: com.joinflatshare.pojo.user.OtpRequest): Observable<Response<ResponseBody>>

    // Verification
    @POST("users/verification/request")
    fun requestAadharOtp(@Body request: AdhaarRequest): Observable<Response<ResponseBody>>

    @POST("users/verification/verify")
    fun verifiyAdhaarOtp(@Body request: AdhaarOtp): Observable<Response<ResponseBody>>

    // User Related Information
    @GET("users/{phone}")
    fun getProfile(
        @Path("phone") phone: String?
    ): Observable<Response<ResponseBody>>

    @PATCH("users/me")
    fun updateProfile(
        @Body user: User?
    ): Observable<Response<ResponseBody>>

    /*------------------------------ Notification & Requests ------------------------------*/
    @GET("flats/flatmate/requests")
    fun getFlatRequests(): Observable<Response<ResponseBody>>

    @GET("users/connections/{type}/requests")
    fun getChatRequests(
        @Path("type") type: String?
    ): Observable<Response<ResponseBody>>

    @POST()
    fun sendChatRequest(
        @Url url: String
    ): Observable<Response<ResponseBody>>


    @PUT()
    fun sendChatRequestResponse(
        @Url url: String
    ): Observable<Response<ResponseBody>>

    @PUT("flats/accept/{id}")
    fun acceptFlatRequest(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    @PUT("users/friends/accept/{id}")
    fun acceptFriendRequest(
        @Path("id") id: String
    ): Observable<com.joinflatshare.pojo.BaseResponse>

    // Contacts
    @GET("users/suggestions/{type}")
    fun getMutualContacts(
        @Path("type") type: String?
    ): Observable<Response<ResponseBody>>

    /*------------------------------ Flat ------------------------------*/
    @GET("flats/{id}")
    fun getFlat(@Path("id") id: String?): Observable<Response<ResponseBody>>

    /*------------------------------ RECOMMENDATION ------------------------------*/
    @GET("recommendations/flats/{pageNo}")
    fun getFlatRecommendations(
        @Path("pageNo") pageNo: String?
    ): Observable<Response<ResponseBody>>

    @GET("recommendations/users/{pageNo}")
    fun getUserRecommendations(
        @Path("pageNo") pageNo: String?
    ): Observable<Response<ResponseBody>>

    @GET("recommendations/fht/{pageNo}")
    fun getFhtRecommendations(
        @Path("pageNo") pageNo: String?
    ): Observable<Response<ResponseBody>>

    @GET("recommendations/date/{pageNo}")
    fun getDateRecommendations(
        @Path("pageNo") pageNo: String?
    ): Observable<Response<ResponseBody>>

    /*------------------------------ FEED ------------------------------*/
    @GET("recommendations/likes/{type}/{pageNo}")
    fun getLikesSent(
        @Path("type") type: String?,
        @Path("pageNo") pageNo: Int?
    ): Observable<Response<ResponseBody>>

    @GET("users/likes/received/me/{type}/{pageNo}")
    fun getLikesReceived(
        @Path("type") type: String?,
        @Path("pageNo") pageNo: Int?
    ): Observable<Response<ResponseBody>>

    @PUT()
    fun addLike(
        @Url url: String?,
        @Body request: LikeRequest?
    ): Observable<Response<ResponseBody>>
}