package cmb.reporter.app.smartcitizenapp.retrofit

import cmb.reporter.app.smartcitizenapp.models.*
import retrofit2.Call
import retrofit2.http.*

interface SmartCityEndpoints {
    @POST("user/login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("user/register")
    fun register(@Body register: RegisterUser): Call<RegisterUser>

    @POST("user/password/reset")
    fun changePassword(@Body changePassword: ChangePassword): Call<RegisterUser>

    @POST("user/forgotPassword")
    fun forgotPassword(@Body forgotPassword: ForgotPassword): Call<RegisterUser>

    @GET("area/all")
    fun getAreas(): Call<List<Area>>

    @GET("category/all")
    fun getCategories(): Call<List<Category>>

    @POST("issue/add")
    fun addIssue(@Body issue: Issue): Call<IssueResponse>

    @POST("issue/all")
    fun getIssues(@Body issueRequest: IssueRequest): Call<AllIssueResponse>

    @POST("issue/update/details")
    fun updateIssues(@Body issueUpdateList: List<IssueUpdate>): Call<List<IssueResponse>>

    @GET("user/check")
    fun isRegisteredUser(@Query("isRegisteredUser") isRegisteredUser:String): Call<Boolean>

    @GET("util/appData/{userId}")
    fun getAppData(@Path("userId") userId:Int): Call<AllAppData>

}