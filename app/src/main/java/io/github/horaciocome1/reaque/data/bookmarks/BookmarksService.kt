package io.github.horaciocome1.reaque.data.bookmarks

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import io.github.horaciocome1.reaque.data.posts.Post
import io.github.horaciocome1.reaque.util.addSimpleAuthStateListener
import io.github.horaciocome1.reaque.util.addSimpleSnapshotListener
import io.github.horaciocome1.reaque.util.mapSimple
import io.github.horaciocome1.reaque.util.posts

class BookmarksService : BookmarksInterface {

    private val tag: String by lazy { "BookmarksService" }

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val posts: MutableLiveData<List<Post>> by lazy {
        MutableLiveData<List<Post>>().apply { value = mutableListOf() }
    }

    private val isBookmarked: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = false }
    }

    override fun bookmark(post: Post, onSuccessListener: (Void?) -> Unit) {
        auth.addSimpleAuthStateListener {
            val ref = db.document("users/${it.uid}/bookmarks/${post.id}")
            ref.set(post.mapSimple).addOnSuccessListener(onSuccessListener)
        }
    }

    override fun unBookmark(post: Post, onSuccessListener: (Void?) -> Unit) {
        auth.addSimpleAuthStateListener {
            val ref = db.document("users/${it.uid}/bookmarks/${post.id}")
            ref.delete().addOnSuccessListener(onSuccessListener)
        }
    }

    override fun get(): LiveData<List<Post>> {
        posts.value?.let {
            if (it.isEmpty())
                auth.addSimpleAuthStateListener { user ->
                    val ref = db.collection("users/${user.uid}/bookmarks")
                    ref.orderBy("score", Query.Direction.DESCENDING).addSimpleSnapshotListener(tag) { snapshot ->
                        posts.value = snapshot.posts
                    }
                }
        }
        return posts
    }

    override fun isBookmarked(post: Post): LiveData<Boolean> {
        isBookmarked.value = false
        auth.addSimpleAuthStateListener { user ->
            val ref = db.document("users/${user.uid}/bookmarks/${post.id}")
            ref.addSimpleSnapshotListener(tag) {
                val postId = it["title"]
                isBookmarked.value = postId != null
            }
        }
        return isBookmarked
    }

}