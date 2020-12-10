package net.geeksempire.keepnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnote.databinding.EntryConfigurationLayoutBinding

class EntryConfigurations : AppCompatActivity() {

    val firebaseAuth: FirebaseAuth = Firebase.auth
    val firebaseUser = firebaseAuth.currentUser

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)



    }

}