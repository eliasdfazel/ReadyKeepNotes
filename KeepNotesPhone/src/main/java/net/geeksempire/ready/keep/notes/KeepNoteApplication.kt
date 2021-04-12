/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.Database.Configurations.Offline.NotesRoomDatabaseConfiguration
import net.geeksempire.ready.keep.notes.Database.Configurations.Online.FirestoreConfiguration
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.DependencyInjections.DaggerDependencyGraph
import net.geeksempire.ready.keep.notes.DependencyInjections.DependencyGraph

class KeepNoteApplication : Application() {

    val notesRoomDatabaseConfiguration: NotesRoomDatabaseConfiguration by lazy {
        NotesRoomDatabaseConfiguration(applicationContext)
    }

    val firestoreConfiguration: FirestoreConfiguration by lazy {
        FirestoreConfiguration(applicationContext)
    }

    /**
     * Define After FirebaseApp.initializeApp(applicationContext)
     **/
    lateinit var firebaseStorage: FirebaseStorage

    /**
     * Define After FirebaseApp.initializeApp(applicationContext)
     **/
    lateinit var firestoreDatabase: FirebaseFirestore

    val jsonIO = JsonIO()

    val dependencyGraph: DependencyGraph by lazy {
        DaggerDependencyGraph.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(applicationContext)

        val firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

        firebaseAnalytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        firestoreDatabase = firestoreConfiguration.initialize()

        firebaseStorage = Firebase.storage

    }

}