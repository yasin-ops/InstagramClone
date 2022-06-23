package com.example.instagramclonefiver

import android.icu.util.TimeZone.SystemTimeZoneType
import android.net.Uri
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.instagramclonefiver.data.Event
import com.example.instagramclonefiver.data.PostData
import com.example.instagramclonefiver.data.UserData
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID
import javax.inject.Inject

const val USERS = "users"
const val POST = "post"

class InstagramViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val storage: FirebaseStorage,
    val db: FirebaseFirestore

) : ViewModel() {
    // Set Flag Here
    val signedIn = mutableStateOf(false)
    val inProgress = mutableStateOf(false)

    // PopUp Notification
    val popupNotification = mutableStateOf<Event<String>?>(null)

    //data Class userData
    val userData = mutableStateOf<UserData?>(null)

    val refreshPostProgress = mutableStateOf(false)
    val post = mutableStateOf<List<PostData>>(listOf())

    val searchedPost = mutableStateOf<List<PostData>>(listOf())
    val serachedPostsProgress = mutableStateOf(false)

    val postFeed = mutableStateOf<List<PostData>>(listOf())
    val postsFeedProgress = mutableStateOf(false)

    // check User Sign in the start of the app
    init {
        //auth.signOut()
        val currentUser = auth.currentUser
        signedIn.value = currentUser != null
        currentUser?.uid?.let { uid ->
            getUserData(uid)
        }

    }

    fun onSignup(username: String, email: String, pass: String) {
        if (username.isEmpty() or email.isEmpty() or pass.isEmpty()) {
            handleException(customMessage = "Please Fill all the fields ")
            return
        }


        inProgress.value = true
        db.collection(USERS).whereEqualTo("username", username).get()
            .addOnSuccessListener { documents ->
                if (documents.size() > 0) {
                    handleException(customMessage = "User already exits ")
                    inProgress.value = false
                } else {
                    auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            signedIn.value = true
                            // Create User Profile
                            createorUpdateProfile(username = username)

                        } else {
                            handleException(task.exception, "SignUpFailed")
                        }
                        inProgress.value = false
                    }
                }
            }
            .addOnFailureListener {


            }

    }

    fun onLogin(email: String, pass: String) {
        if (email.isEmpty() or pass.isEmpty()) {
            handleException(customMessage = "Please Fill all Fields ")
            return
        }
        inProgress.value = true
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                signedIn.value = true
                inProgress.value = false
                auth.currentUser?.uid?.let { uid ->
                    getUserData(uid)
                }
            } else {
                handleException(task.exception, "Login failed")
                inProgress.value = false
            }

        }.addOnFailureListener { exc ->
            handleException(exc, "User can not Sign Up ")
            inProgress.value = false
        }

    }

    private fun createorUpdateProfile(
        name: String? = null,
        username: String? = null,
        bio: String? = null,
        imageUrl: String? = null,

        ) {
        val uid = auth.currentUser?.uid
        val userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            userName = username ?: userData.value?.userName,
            bio = bio ?: userData.value?.bio,
            imageUrl = imageUrl ?: userData.value?.imageUrl,
            following = userData?.value?.following

        )
        uid?.let { uid ->
            inProgress.value = true
            db.collection(USERS).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    it.reference.update(userData.toMap())
                        .addOnSuccessListener {
                            this.userData.value = userData
                            inProgress.value = false
                        }
                        .addOnFailureListener {
                            handleException(it, "can not Update the Users")
                            inProgress.value = false
                        }


                } else {
                    db.collection(USERS).document(uid).set(userData)
                    getUserData(uid)
                    inProgress.value = false
                }
            }.addOnFailureListener { exc ->
                handleException(exc, "can not create user")
                inProgress.value = false
            }

        }
    }

    private fun getUserData(uid: String) {
        inProgress.value = true
        db.collection(USERS).document(uid).get().addOnSuccessListener {
            val user = it.toObject<UserData>()
            userData.value = user
            inProgress.value = false
            refreshPost()
            getPersonalizeFeed()
            //          popupNotification.value = Event("User Data retrieve successfully")

        }
            .addOnFailureListener { exc ->
                handleException(exc, "can not retrieve User Data ")

            }

    }

    fun handleException(exception: Exception? = null, customMessage: String = "") {
        exception?.printStackTrace()
        val errorMsg = exception?.localizedMessage ?: ""
        val message = if (customMessage.isEmpty()) errorMsg else "$customMessage: $errorMsg"
        popupNotification.value = Event(message)

    }

    fun updateProfileData(name: String, username: String, bio: String) {
        createorUpdateProfile(name, username, bio)

    }

    private fun uploadImages(uri: Uri, onSuccess: (Uri) -> Unit) {
        inProgress.value = true
        val storageRef = storage.reference
        val uuid = UUID.randomUUID()
        val imageRef = storageRef.child("images/$uuid")
        val uploadTask = imageRef.putFile(uri)
        uploadTask.addOnSuccessListener {

            val result = it.metadata?.reference?.downloadUrl
            result?.addOnSuccessListener { onSuccess }
        }.addOnFailureListener { exc ->
            handleException(exc)
            inProgress.value = false

        }

    }

    fun uploadProfileImage(uri: Uri) {
        uploadImages(uri) {
            createorUpdateProfile(imageUrl = it.toString())
            updatePostUserImageData(it.toString())
        }
    }

    private fun updatePostUserImageData(imageUrl: String) {
        val currentUid = auth.currentUser?.uid
        db.collection(POST).whereEqualTo("userId", currentUid).get()
            .addOnSuccessListener {
                val post = mutableStateOf<List<PostData>>(arrayListOf())
                convertPost(it, post)
                val refs = arrayListOf<DocumentReference>()
                for (post in post.value) {
                    post.postId?.let { id ->
                        refs.add(db.collection(POST).document(id))
                    }

                }
                if (refs.isNotEmpty()) {
                    db.runBatch() { batch ->
                        for (ref in refs) {
                            batch.update(ref, "userImage", imageUrl)
                        }
                    }
                        .addOnSuccessListener {
                            refreshPost()
                        }
                }
            }
            .addOnFailureListener {}
    }

    fun onLogout() {
        auth.signOut()
        signedIn.value = false
        userData.value = null
        popupNotification.value = Event("Log Out")
        searchedPost.value = listOf()
        postFeed.value = listOf()

    }

    fun newPost(uri: Uri, description: String, onPostSuccess: () -> Unit) {
        uploadImages(uri) {
            onCreatePost(it, description, onPostSuccess)
        }
    }

    private fun onCreatePost(imageUri: Uri, description: String, onPostSuccess: () -> Unit) {
        inProgress.value = true
        val currentUid = auth.currentUser?.uid
        val currentUsername = userData.value?.userName
        val currentUserImage = userData.value?.imageUrl
        if (currentUid != null) {
            val postUuid = UUID.randomUUID().toString()
            val filterWords = listOf("the", "be", "to", "is", "of", "and", "or", "a", "in", "it")
            val searchTerms = description
                .split("", ".", "@", "$", "#", "!", "?")
                .map { it.lowercase() }
                .filter { it.isNotEmpty() and !filterWords.contains(it) }


            val post = PostData(
                postId = postUuid,
                userId = currentUid,
                username = currentUsername,
                userImage = currentUserImage,
                postImage = imageUri.toString(),
                postDescription = description,
                time = System.currentTimeMillis(),
                likes = listOf<String>(),
                searchTerms = searchTerms

            )
            db.collection(POST).document(postUuid).set(post)
                .addOnSuccessListener {
                    popupNotification.value = Event("Post Successfully Created")
                    inProgress.value = false
                    refreshPost()
                    onPostSuccess.invoke()
                }
                .addOnFailureListener { exc ->
                    handleException(customMessage = "unable to create post")
                    inProgress.value = false

                }


        } else {
            handleException(customMessage = "Error user anme is unavaiable , unvaible to create post ")
            onLogout()
            inProgress.value = false
        }

    }

    private fun refreshPost() {
        val currentUid = auth.currentUser?.uid
        if (currentUid != null) {
            refreshPostProgress.value = true
            db.collection(POST).whereEqualTo("userId", currentUid).get()
                .addOnSuccessListener { documents ->
                    convertPost(documents, post)
                    refreshPostProgress.value = false
                }
                .addOnFailureListener { exc ->
                    handleException(exc, "can not the fetch")
                    refreshPostProgress.value = false

                }
        } else {
            handleException(customMessage = "Error : user name unavaible. unable to refresh the post ")
            onLogout()

        }

    }

    private fun convertPost(documents: QuerySnapshot, outState: MutableState<List<PostData>>) {
        val newPosts = mutableListOf<PostData>()
        documents.forEach { docs ->
            val post = docs.toObject<PostData>()
            newPosts.add(post)


        }
        val sortedPost = newPosts.sortedByDescending { it.time }
        outState.value = sortedPost


    }

    fun searchedPost(searchTerms: String) {
        if (searchTerms.isNotEmpty()) {
            serachedPostsProgress.value = true
            // "searchterms" same as in the Firebase dataBase
            db.collection(POST).whereArrayContains("searchterms", searchTerms.trim().lowercase())
                .get().addOnSuccessListener {
                    convertPost(it, searchedPost)
                    serachedPostsProgress.value = false

                }
                .addOnFailureListener { exc -> handleException(exc, "can not Search Post") }
        }

    }

    fun onFollowClick(userId: String) {
        auth.currentUser?.uid?.let { currentUser ->
            val following = arrayListOf<String>()
            userData?.value?.following?.let {
                following.addAll(it)

            }
            if (following.contains(userId)) {
                following.remove(userId)
            } else {
                following.add(userId)
            }

            db.collection(USERS).document(currentUser).update("following", following)
                .addOnSuccessListener {
                    getUserData(currentUser)

                }


        }
    }

    private fun getPersonalizeFeed() {
        val following = userData?.value?.following

        if (!following.isNullOrEmpty()) {
            postsFeedProgress.value = true
            db.collection(POST).whereIn("userId", following).get()
                .addOnSuccessListener {
                    convertPost(documents = it, outState = postFeed)
                    if (postFeed.value.isEmpty()) {
                        getGereralFeed()
                    } else {
                        postsFeedProgress.value = false
                    }

                }
                .addOnFailureListener { exc ->
                    handleException(exc, "can not get Personalize Feed")
                    postsFeedProgress.value = false
                }
        } else {
            getGereralFeed()
        }
    }

    private fun getGereralFeed() {
        postsFeedProgress.value = true
        val currentTime = System.currentTimeMillis()
        val difference = 24 * 60 * 60 * 1000// 1 day in milli sec
        db.collection(POST).whereGreaterThan("time", currentTime - difference)
            .get()
            .addOnSuccessListener {
                convertPost(documents = it, outState = postFeed)
                postsFeedProgress.value = false
            }.addOnFailureListener {

            }


    }
}