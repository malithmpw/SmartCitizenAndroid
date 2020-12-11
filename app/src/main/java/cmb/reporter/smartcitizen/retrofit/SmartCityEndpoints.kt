package cmb.reporter.smartcitizen.retrofit

import cmb.reporter.smartcitizen.models.Area
import cmb.reporter.smartcitizen.models.Category
import cmb.reporter.smartcitizen.models.LoginRequestDTO
import cmb.reporter.smartcitizen.models.LoginResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface SmartCityEndpoints {
    @POST("user/login")
    fun login(@Body login: LoginRequestDTO): Call<LoginResponse>

    @GET("area/all")
    fun getAreas(): Call<List<Area>>

    @GET("category/all")
    fun getCategories(): Call<List<Category>>

}