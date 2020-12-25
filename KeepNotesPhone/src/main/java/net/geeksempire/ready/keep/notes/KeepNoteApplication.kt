package net.geeksempire.ready.keep.notes

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.Database.Configurations.FirestoreConfiguration
import net.geeksempire.ready.keep.notes.DependencyInjections.DaggerDependencyGraph
import net.geeksempire.ready.keep.notes.DependencyInjections.DependencyGraph

class KeepNoteApplication : Application() {

    val firestoreConfiguration: FirestoreConfiguration by lazy {
        FirestoreConfiguration(applicationContext)
    }

    lateinit var firebaseStorage: FirebaseStorage

    lateinit var firestoreDatabase: FirebaseFirestore

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