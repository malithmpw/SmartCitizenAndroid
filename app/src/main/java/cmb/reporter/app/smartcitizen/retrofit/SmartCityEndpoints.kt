package cmb.reporter.app.smartcitizen.retrofit

import cmb.reporter.app.smartcitizen.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartCityEndpoints {
    @POST("user/login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @POST("user/register")
    fun register(@Body register: RegisterUser): Call<RegisterUser>

    @POST("user/password/reset")
    fun changePassword(@Body changePassword: ChangePassword): Call<RegisterUser>

    @GET("area/all")
    fun getAreas(): Call<List<Area>>

    @GET("category/all")
    fun getCategories(): Call<List<Category>>

    @POST("issue/add")
    fun addIssue(@Body issue: Issue): Call<IssueResponse>

    @POST("issue/all")
    fun getIssues(@Body issueRequest: IssueRequest): Call<AllIssueResponse>

}