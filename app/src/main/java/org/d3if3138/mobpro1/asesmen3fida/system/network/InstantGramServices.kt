package org.d3if3138.mobpro1.asesmen3fida.system.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.InstantGram
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.InstantLike
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

private const val BASE_URL = "https://unique-apex-app.000webhostapp.com/files/Fida/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl(BASE_URL)
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

data class instantResponse(
    val results: List<InstantGram>
)

data class InstantLikeResponse(
    val results: List<InstantLike>
)

interface InstantGramService {
    @Multipart
    @POST("doPost.php")
    suspend fun doPost(
        @Part("userId") userId: RequestBody,
        @Part("email") email: RequestBody,
        @Part("name") name: RequestBody,
        @Part("photoUrl") photoUrl: RequestBody,
        @Part("caption") caption: RequestBody,
        @Part postUrl: MultipartBody.Part?
    ): instantResponse

    @GET("getPost.php")
    suspend fun getPosts(): instantResponse

    @FormUrlEncoded
    @POST("delPost.php")
    suspend fun delPost(
        @Field("id") id: String
    ): instantResponse

    @Multipart
    @POST("likePost.php")
    suspend fun likePost(
        @Part("postId") postId: RequestBody,
        @Part("userId") userId: RequestBody
    ): InstantLikeResponse

    @Multipart
    @POST("unlikePost.php")
    suspend fun unlikePost(
        @Part("postId") postId: RequestBody,
        @Part("userId") userId: RequestBody
    ): InstantLikeResponse

    @GET("getLikes.php")
    suspend fun getLikes(): InstantLikeResponse
}

object InstantGramAPI {
    val retrofitService: InstantGramService by lazy {
        retrofit.create(InstantGramService::class.java)
    }
    fun imgUrl(imageId: String): String {
        return "$BASE_URL$imageId"
    }
}
enum class InstantGramStatus { LOADING, SUCCESS, FAILED }