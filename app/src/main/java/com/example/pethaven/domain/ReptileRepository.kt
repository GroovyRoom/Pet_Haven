package com.example.pethaven.domain

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import java.lang.Exception

class ReptileRepository(private val reptileDao: ReptileDao) {
    private var firebaseStorageReference = FirebaseStorage.getInstance().reference

    @Volatile private var INSTANCE: ReptileRepository? = null
    fun getInstance(): ReptileRepository {
        return INSTANCE?: synchronized(this) {
            val instance = ReptileRepository(reptileDao)
            INSTANCE = instance
            instance
        }
    }

    ///-------------------------- Operations for Users-------------------------///
    fun getCurrentUserObject() = reptileDao.getCurrentUserObject()
    fun updateUser(user: User) = reptileDao.updateUser(user)
    ///-------------------------- Operations for Post Objects-------------------------///
    fun addPost(post: Post) = reptileDao.addPost(post)
    fun getAllPost() = reptileDao.getAllPost()
    fun getPost(key: String) = reptileDao.getPost(key)
    fun editTradePost(key: String, post: Post) = reptileDao.editTradePost(key, post)
    fun getPostsByReptileId(rid: String) = reptileDao.getPostByReptileID(rid)
    fun getPostsByUserId(uid: String) = reptileDao.getPostByUserID(uid)
    fun deletePost(key: String) = reptileDao.deletePost(key)

    ///-------------------------- Operations for Reptile Objects-------------------------///
    fun addReptile(reptile: Reptile) = reptileDao.addReptile(reptile)
    fun updateReptile(key: String, reptile: Reptile) = reptileDao.updateReptile(key, reptile)

    fun deleteReptile(key: String) = reptileDao.deleteReptile(key)
    fun deleteImage(imgUri: String) = reptileDao.deleteImageFromStorage(imgUri)

    fun getReptileFromCurrentUser(key: String) = reptileDao.getReptileFromCurrentUser(key)
    fun getAllUserReptile() = reptileDao.getAllUserReptiles()

    ///------------------------- Operations for Uploading heavy data -------------------------///
    fun uploadImage(uri: Uri) =
        uri.let {
            val fileReference = firebaseStorageReference.child("images/" + System.currentTimeMillis())
            fileReference.putFile(it)
        }

    fun loadPosts(postList: MutableLiveData<List<Post>>) {
        val postReference = reptileDao.getAllPost()
        val _postList = ArrayList<Post>()
        postReference.addValueEventListener(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                try {
                    for (postSnapshot in snapshot.children) {
                        val post = postSnapshot.getValue(Post::class.java)
                        post?.let {
                            it.pid = postSnapshot.key
                            _postList.add(it)
                        }
                    }

                    postList.postValue(_postList)
                    } catch (e: Exception) {

                    }
                }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



}