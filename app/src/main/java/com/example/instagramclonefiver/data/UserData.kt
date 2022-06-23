package com.example.instagramclonefiver.data

data class UserData(
    var userId: String? = null,
    var userName: String? = null,
    var name: String? = null,
    var imageUrl: String? = null,
    var bio: String? = null,
    var following: List<String>? = null


) {
    // convert this value to map so easily store to FireStore
    fun toMap() = mapOf(
        "userId" to userId,
        "userName" to userName,
        "name" to name,
        "imageUrl" to imageUrl,
        "bio" to bio,
        "following" to following


    )
}
