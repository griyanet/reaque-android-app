package io.github.horaciocome1.reaque.data.ratings

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import io.github.horaciocome1.reaque.data.posts.Post
import io.github.horaciocome1.reaque.util.addSimpleAuthStateListener
import io.github.horaciocome1.reaque.util.addSimpleSnapshotListener
import io.github.horaciocome1.reaque.util.map
import io.github.horaciocome1.reaque.util.user

class RatingsService : RatingsServiceInterface {

    private val tag: String by lazy { "RatingsService" }

    private val ref: CollectionReference by lazy { FirebaseFirestore.getInstance().collection("ratings") }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val rating: MutableLiveData<String> by lazy {
        MutableLiveData<String>().apply { value = "1" }
    }

    override fun rate(post: Post, value: Int, onSuccessListener: (Void?) -> Unit) {
        auth.addSimpleAuthStateListener {
            val rating = Rating("").apply {
                this.post = post
                user = it.user
                this.value = value
            }
            ref.document("${it.uid}_${post.id}").set(rating.map).addOnSuccessListener(onSuccessListener)
        }
    }

    override fun get(post: Post): LiveData<String> {
        rating.value = "1"
        auth.addSimpleAuthStateListener { user ->
            ref.document("${user.uid}_${post.id}").addSimpleSnapshotListener(tag) {
                val value = it["value"]
                if (value != null)
                    rating.value = value.toString()
                else
                    rating.value = "1"
            }
        }
        return rating
    }

}