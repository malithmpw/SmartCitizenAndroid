package cmb.reporter.smartcitizen.retrofit

import cmb.reporter.smartcitizen.models.*
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartCityEndpoints {
    @POST("user/login")
    fun login(@Body login: LoginRequest): Call<LoginResponse>

    @GET("area/all")
    fun getAreas(): Call<List<Area>>

    @GET("category/all")
    fun getCategories(): Call<List<Category>>

    @POST("issue/add")
    fun addIssue(@Body issue: Issue): Call<IssueResponse>

    @POST("issue/all")
    fun getIssues(@Body issueRequest: IssueRequest): Call<AllIssueResponse>

}