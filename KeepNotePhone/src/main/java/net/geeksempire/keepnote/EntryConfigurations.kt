package net.geeksempire.keepnote

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnote.databinding.EntryConfigurationLayoutBinding

class EntryConfigurations : AppCompatActivity() {

    private val firebaseAuth: FirebaseAuth = Firebase.auth
    private val firebaseUser = firebaseAuth.currentUser

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)

        if (firebaseUser == null) {

            entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

            entryConfigurationLayoutBinding.waitingView.visibility = View.VISIBLE
            entryConfigurationLayoutBinding.waitingInformationView.visibility = View.VISIBLE

            firebaseAuth.signInAnonymously().addOnSuccessListener {

                entryConfigurationLayoutBinding.waitingView.visibility = View.GONE



            }.addOnCanceledListener {



            }

        } else {



        }

    }

}