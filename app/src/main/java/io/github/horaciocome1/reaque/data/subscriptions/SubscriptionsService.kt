package io.github.horaciocome1.reaque.data.subscriptions

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.SetOptions
import io.github.horaciocome1.reaque.data.users.User
import io.github.horaciocome1.reaque.util.*

class SubscriptionsService : SubscriptionsInterface {

    private val tag: String by lazy { "SubscriptionsService" }

    private val db: FirebaseFirestore by lazy { FirebaseFirestore.getInstance() }

    private val auth: FirebaseAuth by lazy { FirebaseAuth.getInstance() }

    private val incrementSubscriptions: Map<String, FieldValue> by lazy {
        val increment = FieldValue.increment(1)
        mapOf("subscriptions" to increment)
    }

    private val incrementSubscribers: Map<String, FieldValue> by lazy {
        val increment = FieldValue.increment(1)
        mapOf("subscribers" to increment)
    }

    private val decrementSubscriptions: Map<String, FieldValue> by lazy {
        val increment = FieldValue.increment(-1)
        mapOf("subscriptions" to increment)
    }

    private val decrementSubscribers: Map<String, FieldValue> by lazy {
        val increment = FieldValue.increment(-1)
        mapOf("subscribers" to increment)
    }

    private val subscriptions: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().apply { value = mutableListOf() }
    }

    private val subscribers: MutableLiveData<List<User>> by lazy {
        MutableLiveData<List<User>>().apply { value = mutableListOf() }
    }

    private val amSubscribedTo: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>().apply { value = false }
    }

    private var subscriptionsOf = ""

    private var subscribersOf = ""

    override fun subscribe(user: User, onCompleteListener: (Task<Void?>?) -> Unit) {
        auth.addSimpleAuthStateListener { firebaseUser ->
            val subscriptionRef = db.document("users/${firebaseUser.uid}/subscriptions/${user.id}")
            val subscriberRef = db.document("users/${user.id}/subscribers/${firebaseUser.uid}")
            val myRef = db.document("users/${firebaseUser.uid}")
            val hisRef = db.document("users/${user.id}")
            db.runBatch {
                it.set(subscriptionRef, user.map)
                it.set(subscriberRef, firebaseUser.user.map)
                it.set(myRef, incrementSubscriptions, SetOptions.merge())
                it.set(hisRef, incrementSubscribers, SetOptions.merge())
            }.addOnCompleteListener(onCompleteListener)
        }
    }

    override fun unSubscribe(user: User, onCompleteListener: (Task<Void?>?) -> Unit) {
        auth.addSimpleAuthStateListener { firebaseUser ->
            val subscriptionRef = db.document("users/${firebaseUser.uid}/subscriptions/${user.id}")
            val subscriberRef = db.document("users/${user.id}/subscribers/${firebaseUser.uid}")
            val myRef = db.document("users/${firebaseUser.uid}")
            val hisRef = db.document("users/${user.id}")
            db.runBatch {
                it.delete(subscriptionRef)
                it.delete(subscriberRef)
                it.set(myRef, decrementSubscriptions, SetOptions.merge())
                it.set(hisRef, decrementSubscribers, SetOptions.merge())
            }.addOnCompleteListener(onCompleteListener)
        }
    }

    override fun getSubscriptions(user: User): LiveData<List<User>> {
        if (user.id != subscriptionsOf) {
            val ref = db.collection("users/${user.id}/subscriptions")
            ref.orderBy("timestamp", Query.Direction.DESCENDING).addSimpleSnapshotListener(tag) {
                subscriptions.value = it.users
            }
            subscriptionsOf = user.id
        }
        return subscriptions
    }

    override fun getSubscribers(user: User): LiveData<List<User>> {
        if (user.id != subscribersOf) {
            val ref = db.collection("users/${user.id}/subscribers")
            ref.orderBy("timestamp", Query.Direction.DESCENDING).addSimpleSnapshotListener(tag) {
                subscribers.value = it.users
            }
            subscribersOf = user.id
        }
        return subscribers
    }

    override fun amSubscribedTo(user: User): LiveData<Boolean> {
        amSubscribedTo.value = false
        auth.addSimpleAuthStateListener { firebaseUser ->
            val ref = db.document("users/${firebaseUser.uid}/subscriptions/${user.id}")
            ref.addSimpleSnapshotListener(tag) {
                amSubscribedTo.value = it["timestamp"] != null
            }
        }
        return amSubscribedTo
    }

}