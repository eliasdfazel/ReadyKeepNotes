/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/20/20 9:00 AM
 * Last modified 11/20/20 8:54 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnote.Database.Configurations

import android.content.Context
import com.abanabsalan.aban.magazine.Utils.System.SystemInformation
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.firestoreSettings
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnote.Utils.Network.NetworkCheckpoint

class FirestoreConfiguration (private val context: Context) {

    private val firebaseFirestore = Firebase.firestore

    private val networkCheckpoint: NetworkCheckpoint = NetworkCheckpoint(context)

    private val systemInformation: SystemInformation = SystemInformation(context)

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