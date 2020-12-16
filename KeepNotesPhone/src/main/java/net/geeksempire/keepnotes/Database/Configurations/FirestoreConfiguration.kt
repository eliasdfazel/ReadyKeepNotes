/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/20/20 9:00 AM
 * Last modified 11/20/20 8:54 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.Database.Configurations

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase

class FirestoreConfiguration (private val context: Context) {

    private val firebaseFirestore = Firebase.firestore

    fun initialize() : FirebaseFirestore {

        val firebaseFirestoreSettings = firestoreSettings {
            isPersistenceEnabled = true
            cacheSizeBytes = FirebaseFirestoreSettings.CACHE_SIZE_UNLIMITED
        }

        firebaseFirestore.firestoreSettings = firebaseFirestoreSettings

        return firebaseFirestore
    }

    fun forceReadCache() {

        firebaseFirestore.disableNetwork()

    }

    fun forceReadInternet() {

        firebaseFirestore.enableNetwork()

    }

}