package net.geeksempire.keepnote

import android.app.Application
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.firestore.FirebaseFirestore
import net.geeksempire.keepnote.Database.Configurations.FirestoreConfiguration
import net.geeksempire.keepnote.DependencyInjections.DaggerDependencyGraph
import net.geeksempire.keepnote.DependencyInjections.DependencyGraph

class KeepNoteApplication : Application() {

    val firestoreConfiguration: FirestoreConfiguration by lazy {
        FirestoreConfiguration(applicationContext)
    }

    lateinit var firestoreDatabase: FirebaseFirestore

    val dependencyGraph: DependencyGraph by lazy {
        DaggerDependencyGraph.factory().create(applicationContext)
    }

    override fun onCreate() {
        super.onCreate()

        val firebaseAnalytics = FirebaseAnalytics.getInstance(applicationContext)

        firebaseAnalytics.setAnalyticsCollectionEnabled(!BuildConfig.DEBUG)

        firestoreDatabase = firestoreConfiguration.initialize()

    }

}