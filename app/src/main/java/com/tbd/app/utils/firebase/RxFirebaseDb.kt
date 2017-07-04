package com.tbd.app.utils.firebase

import com.google.android.gms.tasks.Task
import com.google.firebase.database.*
import io.reactivex.*

/**
 * RxJava wrapper for [FirebaseDatabase]
 */

val firebaseDb: FirebaseDatabase = FirebaseDatabase.getInstance()

class RxFirebaseDb(private val db: FirebaseDatabase = firebaseDb) {

    fun <T> fetch(path: String,
                  parser: (DataSnapshot) -> T?,
                  options: Query.() -> Query = { this }): Single<T> =
            Single.create { e ->
                val listener = buildSingleValueEventListener(e, parser)

                val dbRef = db.getReference("$path").let(options)
                dbRef.addListenerForSingleValueEvent(listener)
                e.setCancellable { dbRef.removeEventListener(listener) }
            }

    fun <T> observeChildEvents(path: String,
                      parser: (DataSnapshot) -> T?,
                      options: Query.() -> Query = { this }): Observable<ChildEvent<T>> =
                Observable.create { e ->
                    val listener = buildChildEventListener(e, parser)

                    val dbRef = db.getReference("$path").let(options)
                    dbRef.addChildEventListener(listener)
                    e.setCancellable { dbRef.removeEventListener(listener) }
                }

    private fun <T> buildChildEventListener(observableEmitter: ObservableEmitter<ChildEvent<T>>,
                                                parser: (DataSnapshot) -> T?): ChildEventListener =
            object : ChildEventListener {
                override fun onChildMoved(snapshot: DataSnapshot, p1: String?) {
                    childEventListenerResult(snapshot, ChildEvent.Type.MOVED, observableEmitter, parser)
                }

                override fun onChildChanged(snapshot: DataSnapshot, p1: String?) {
                    childEventListenerResult(snapshot, ChildEvent.Type.CHANGED, observableEmitter, parser)
                }

                override fun onChildAdded(snapshot: DataSnapshot, p1: String?) {
                    childEventListenerResult(snapshot, ChildEvent.Type.ADDED, observableEmitter, parser)
                }

                override fun onChildRemoved(snapshot: DataSnapshot) {
                    childEventListenerResult(snapshot, ChildEvent.Type.REMOVED, observableEmitter, parser)
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!observableEmitter.isDisposed) {
                        observableEmitter.onError(Exception(error.message))
                    }
                }
            }

    private fun <T> childEventListenerResult(dataSnapshot: DataSnapshot,
                                             type: ChildEvent.Type,
                                             observableEmitter: ObservableEmitter<ChildEvent<T>>,
                                             parser: (DataSnapshot) -> T?) {
        if (observableEmitter.isDisposed) {
            return
        }

        parser(dataSnapshot)?.let {
            observableEmitter.onNext(ChildEvent(it, type))
        } ?: observableEmitter.onError(Exception(IllegalArgumentException("Failed to parse $dataSnapshot")))
    }

    data class ChildEvent<T>(val value: T, val type: Type) {
        enum class Type {
            MOVED, CHANGED, ADDED, REMOVED
        }
    }

    fun setValue(path: String, value: Any): Completable =
            Completable.defer {
                db.getReference("$path")
                        .setValue(value)
                        .toCompletable()
            }

    fun updateChildren(path: String, value: Map<String, Any>): Completable =
            Completable.defer {
                db.getReference("$path")
                        .updateChildren(value)
                        .toCompletable()
            }

    fun getKey(path: String): String =
            db.getReference("$path").push().key

    fun removeValue(path: String): Completable =
            Completable.defer {
                db.getReference("$path")
                        .removeValue()
                        .toCompletable()
            }

    fun runTransaction(path: String, handler: Transaction.Handler): Completable =
            Completable.create { e ->
                db.getReference("$path")
                        .runTransaction(object : Transaction.Handler {
                            override fun doTransaction(mutableData: MutableData): Transaction.Result =
                                    handler.doTransaction(mutableData)

                            override fun onComplete(error: DatabaseError?,
                                                    p1: Boolean,
                                                    p2: DataSnapshot?) {
                                handler.onComplete(error, p1, p2)
                                if (error != null) {
                                    if (!e.isDisposed) {
                                        e.onError(Exception(error.message))
                                    }
                                } else {
                                    e.onComplete()
                                }
                            }
                        })
            }

    private fun Task<Void>.toCompletable(): Completable =
            Completable.create { e ->
                this
                        .addOnSuccessListener {
                            if (!e.isDisposed) {
                                e.onComplete()
                            }
                        }
                        .addOnFailureListener { exception ->
                            if (!e.isDisposed) {
                                e.onError(exception)
                            }
                        }
            }

    fun <T> buildSingleValueEventListener(singleEmitter: SingleEmitter<T>,
                                              parser: (DataSnapshot) -> T): ValueEventListener =
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (singleEmitter.isDisposed) {
                        return
                    }

                    val result = parser(snapshot)
                    if (result != null) {
                        singleEmitter.onSuccess(result)
                    } else {
                        singleEmitter.onError(Exception(IllegalArgumentException("Failed to parse $snapshot")))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    if (!singleEmitter.isDisposed) {
                        singleEmitter.onError(Exception(error.message))
                    }
                }
            }

    fun valueExists(path: String): Single<Boolean> =
            fetch(path, { dataSnapshot -> dataSnapshot.value != null })
}
