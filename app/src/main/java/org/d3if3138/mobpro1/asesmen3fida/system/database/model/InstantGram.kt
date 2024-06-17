package org.d3if3138.mobpro1.asesmen3fida.system.database.model

data class InstantGram(
    val id: String = "", // userId
    val email: String = "", // userEmail
    val name: String = "", // userName
    val photoUrl: String = "", // photoUrl
    val caption: String = "", // postCaption
    val like: String = "", // like count
    val postUrl: String = "" // image post
)

data class InstantLike(
    val postId: String = "", // userId
    val userId: String = "", //♦
)
