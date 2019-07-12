package io.github.horaciocome1.reaque.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import io.github.horaciocome1.reaque.data.posts.Post
import io.github.horaciocome1.reaque.data.topics.Topic
import io.github.horaciocome1.reaque.data.users.User

val QuerySnapshot.topics: MutableList<Topic>
    get() {
        val list = mutableListOf<Topic>()
        forEach { list.add(it.topic) }
        return list
    }

val QuerySnapshot.users: MutableList<User>
    get() {
        val list = mutableListOf<User>()
        forEach { list.add(it.user) }
        return list
    }

val QuerySnapshot.posts: MutableList<Post>
    get() {
        val list = mutableListOf<Post>()
        forEach { list.add(it.post) }
        return list
    }

fun Query.addSimpleSnapshotListener(TAG: String, listener: (QuerySnapshot) -> Unit) {
    addSnapshotListener { snapshot, exception ->
        when {
            exception != null -> onListeningFailed(TAG, exception)
            snapshot != null -> listener(snapshot)
            else -> onSnapshotNull(TAG)
        }
    }
}

fun Query.addSimpleAndSafeSnapshotListener(
    TAG: String,
    auth: FirebaseAuth,
    listener: (QuerySnapshot, FirebaseUser) -> Unit
) {
    auth.addSimpleAuthStateListener { user ->
        addSimpleSnapshotListener(TAG) {
            listener(it, user)
        }
    }
}