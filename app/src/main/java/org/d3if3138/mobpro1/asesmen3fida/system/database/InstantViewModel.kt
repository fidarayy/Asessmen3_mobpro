package org.d3if3138.mobpro1.asesmen3fida.system.database

import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.InstantGram
import org.d3if3138.mobpro1.asesmen3fida.system.database.model.InstantLike
import org.d3if3138.mobpro1.asesmen3fida.system.network.InstantGramAPI
import org.d3if3138.mobpro1.asesmen3fida.system.network.InstantGramStatus
import java.io.ByteArrayOutputStream

class InstantGramViewModel : ViewModel() {
    private val _posts = MutableLiveData<List<InstantGram>>()
    val posts: LiveData<List<InstantGram>> get() = _posts

    private val _likes = MutableLiveData<List<InstantLike>>()
    val likes: LiveData<List<InstantLike>> get() = _likes

    var InstantGram_status = MutableStateFlow(InstantGramStatus.LOADING)
        private set

    init {
        getPosts()
        getLikes()
    }

    fun getPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            InstantGram_status.value = InstantGramStatus.LOADING
            try {
                val response = InstantGramAPI.retrofitService.getPosts()
                _posts.postValue(response.results)
                getLikes()
                InstantGram_status.value = InstantGramStatus.SUCCESS
                Log.d("ViewModel", "getPosts: ${response.results}")
            } catch (e: Exception) {
                InstantGram_status.value = InstantGramStatus.FAILED
                Log.e("ViewModel", "getPosts: ${e.message}")
            }
        }
    }

    fun doPost(userId: String, email: String, name: String, photoUrl: String, caption: String, postUrl: Bitmap?) {
        viewModelScope.launch {
            InstantGram_status.value = InstantGramStatus.LOADING
            try {
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val emailPart = email.toRequestBody("text/plain".toMediaTypeOrNull())
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val photoUrlPart = photoUrl.toRequestBody("text/plain".toMediaTypeOrNull())
                val captionPart = caption.toRequestBody("text/plain".toMediaTypeOrNull())

                val postUrlPart: MultipartBody.Part? = postUrl?.let {
                    val byteArrayOutputStream = ByteArrayOutputStream()
                    it.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
                    val requestBody = byteArrayOutputStream.toByteArray().toRequestBody("image/jpeg".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("postUrl", "post.jpg", requestBody)
                }

                val response = InstantGramAPI.retrofitService.doPost(userIdPart, emailPart, namePart, photoUrlPart, captionPart, postUrlPart)
                _posts.value = response.results
                InstantGram_status.value = InstantGramStatus.SUCCESS
                Log.d("ViewModel", "doPost: {$response}")
                getPosts()
            } catch (e: Exception) {
                InstantGram_status.value = InstantGramStatus.FAILED
                Log.e("ViewModel", "doPost: ${e.message}")
            }
        }
    }


    fun delPost(id: String) {
        viewModelScope.launch {
            InstantGram_status.value = InstantGramStatus.LOADING
            try {
                val response = InstantGramAPI.retrofitService.delPost(id)
                _posts.value = response.results
                Log.d("ViewModel", "delPost: {$response}")
                getPosts()
            } catch (e: Exception) {
                InstantGram_status.value = InstantGramStatus.FAILED
                Log.e("ViewModel", "delPost: ${e.message}")
            }
        }
    }

    fun getLikes() {
        viewModelScope.launch {
            InstantGram_status.value = InstantGramStatus.LOADING
            try {
                val response = InstantGramAPI.retrofitService.getLikes()
                _likes.value = response.results
                InstantGram_status.value = InstantGramStatus.SUCCESS
                Log.d("ViewModel", "getLikes: ${response.results}")
            } catch (e: Exception) {
                InstantGram_status.value = InstantGramStatus.FAILED
                Log.e("ViewModel", "getLikes: ${e.message}")
            }
        }
    }

    fun likePost(postId: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            InstantGram_status.value = InstantGramStatus.LOADING
            try {
                val postIdPart = postId.toRequestBody("text/plain".toMediaTypeOrNull())
                val userIdPart = userId.toRequestBody("text/plain".toMediaTypeOrNull())
                val response = InstantGramAPI.retrofitService.likePost(postIdPart, userIdPart)
                Log.d("ViewModel", "likePost: {$response}")
                getPosts()
                getLikes()
            } catch (e: Exception) {
                InstantGram_status.value = InstantGramStatus.FAILED
                Log.e("ViewModel", "likePost: ${e.message}")
            }
        }
    }
}
