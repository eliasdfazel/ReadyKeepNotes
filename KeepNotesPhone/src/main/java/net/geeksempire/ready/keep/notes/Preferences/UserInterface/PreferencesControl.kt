package net.geeksempire.ready.keep.notes.Preferences.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.Preferences.DataStructure.PreferencesLiveData
import net.geeksempire.ready.keep.notes.Preferences.Extensions.preferencesControlSetupUserInterface
import net.geeksempire.ready.keep.notes.Preferences.Extensions.toggleLightDark
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.InApplicationReview.InApplicationReviewProcess
import net.geeksempire.ready.keep.notes.databinding.PreferencesControlLayoutBinding

class PreferencesControl : AppCompatActivity() {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val firebaseUser: FirebaseUser = Firebase.auth.currentUser!!

    val preferencesLiveData: PreferencesLiveData by lazy {
        ViewModelProvider(this@PreferencesControl).get(PreferencesLiveData::class.java)
    }

    lateinit var preferencesControlLayoutBinding: PreferencesControlLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        preferencesControlLayoutBinding = PreferencesControlLayoutBinding.inflate(layoutInflater)
        setContentView(preferencesControlLayoutBinding.root)

        preferencesControlSetupUserInterface()

        preferencesLiveData.toggleTheme.observe(this@PreferencesControl, Observer {

            var delayTheme: Long = 3333

            when(themePreferences.checkThemeLightDark()) {
                ThemeType.ThemeLight -> {
                    delayTheme = 3000
                }
                ThemeType.ThemeDark -> {
                    delayTheme = 1133
                }
            }

            if (it) {

                Handler(Looper.getMainLooper()).postDelayed({

                    toggleLightDark()

                }, delayTheme)

            } else {

                toggleLightDark()

            }

        })

        preferencesControlLayoutBinding.userDisplayName.text = firebaseUser.displayName

        Glide.with(this@PreferencesControl)
            .asDrawable()
            .load(firebaseUser.photoUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(preferencesControlLayoutBinding.userProfileImage)

        preferencesControlLayoutBinding.accountManagerView.setOnClickListener {

            startActivity(Intent(applicationContext, AccountInformation::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, R.anim.fade_out).toBundle())

        }

        preferencesControlLayoutBinding.sharingView.setOnClickListener {

            val shareText: String = "Ready Keep Notes" +
                    "\n" +
                    "Always Ready To Keep Your Notes" +
                    "\n" +
                    "Use Both Keyboard Typing and Handwriting Quickly To Take Your Idea." +
                    "\n" + "\n" +
                    "⬇ Install Our Application ⬇" +
                    "\n" +
                    "${getString(R.string.playStoreLink)}" +
                    "\n" + "\n" +
                    "https://www.GeeksEmpire.net" +
                    "\n" +
                    "#ReadyKeepNotes" +
                    "\n" +
                    "#KeyboardTyping" + " " + "#Handwriting" +
                    ""

            val shareIntent: Intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(shareIntent)

        }

        preferencesControlLayoutBinding.rateReviewView.setOnClickListener {

            InApplicationReviewProcess(this@PreferencesControl)
                .start(true)

        }

        preferencesControlLayoutBinding.facebookView.setOnClickListener {

            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.facebookLink))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_in_right, R.anim.slide_out_left).toBundle())

        }

        preferencesControlLayoutBinding.twitterView.setOnClickListener {

            startActivity(Intent().apply {
                action = Intent.ACTION_VIEW
                data = Uri.parse(getString(R.string.twitterLink))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.slide_in_right, R.anim.slide_out_left).toBundle())

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    }

}