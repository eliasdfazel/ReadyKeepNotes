package net.geeksempire.ready.keep.notes.Preferences.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.abanabsalan.aban.magazine.Utils.System.SystemInformation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.Invitations.Utils.ShareIt
import net.geeksempire.ready.keep.notes.Preferences.DataStructure.PreferencesLiveData
import net.geeksempire.ready.keep.notes.Preferences.Extensions.preferencesControlSetupUserInterface
import net.geeksempire.ready.keep.notes.Preferences.Extensions.toggleLightDark
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.InApplicationReview.InApplicationReviewProcess
import net.geeksempire.ready.keep.notes.Utils.InApplicationUpdate.InApplicationUpdateProcess
import net.geeksempire.ready.keep.notes.Utils.InApplicationUpdate.UpdateResponse
import net.geeksempire.ready.keep.notes.Utils.UI.Dialogue.ChangeLogDialogue
import net.geeksempire.ready.keep.notes.databinding.PreferencesControlLayoutBinding
import java.util.*

class PreferencesControl : AppCompatActivity() {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val systemInformation: SystemInformation by lazy {
        SystemInformation(applicationContext)
    }

    val firebaseUser = Firebase.auth.currentUser

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

            when (themePreferences.checkThemeLightDark()) {
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

        preferencesControlLayoutBinding.accountManagerView.setOnClickListener {

            startActivity(
                Intent(applicationContext, AccountInformation::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }, ActivityOptions.makeSceneTransitionAnimation(this@PreferencesControl, preferencesControlLayoutBinding.userProfileImage, getString(R.string.profileImageTransitionName)).toBundle()
            )

        }

        preferencesControlLayoutBinding.sharingView.setOnClickListener {

            val shareText: String = "Ready Keep Notes" +
                    "\n" +
                    "Always Ready To Keep Your Notes" +
                    "\n" +
                    "Use Keyboard Typing, Handwriting, Voice Recording To Quickly Take Your Ideas." +
                    "\n" + "\n" +
                    "⬇ Install Our Application ⬇" +
                    "\n" +
                    getString(R.string.playStoreLink) +
                    "\n" + "\n" +
                    "https://www.GeeksEmpire.net" +
                    "\n" +
                    "#ReadyKeepNotes" +
                    "\n" +
                    "#KeyboardTyping" + " " + "#Handwriting" +
                    ""

            ShareIt(this@PreferencesControl)
                .invokeTextSharing(shareText)

        }

        preferencesControlLayoutBinding.rateReviewView.setOnClickListener {

            InApplicationReviewProcess(this@PreferencesControl)
                .start(true)

        }

        preferencesControlLayoutBinding.facebookView.setOnClickListener {

            startActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(getString(R.string.facebookLink))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }, ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                ).toBundle()
            )

        }

        preferencesControlLayoutBinding.twitterView.setOnClickListener {

            startActivity(
                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(getString(R.string.twitterLink))
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }, ActivityOptions.makeCustomAnimation(
                    applicationContext,
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
                ).toBundle()
            )

        }

        preferencesControlLayoutBinding.preferencesView.setOnClickListener {

            InApplicationUpdateProcess(this@PreferencesControl, preferencesControlLayoutBinding.rootView)
                .initialize (object : UpdateResponse {

                    override fun latestVersionAlreadyInstalled() {
                        super.latestVersionAlreadyInstalled()

                        Toast.makeText(applicationContext, "Latest Version Already Installed", Toast.LENGTH_LONG).show()

                    }

                })

        }

        preferencesControlLayoutBinding.whatsNewView.setOnClickListener {

            ChangeLogDialogue(this@PreferencesControl)
                .initializeShow(forceShow = true)

        }

        preferencesControlLayoutBinding.supportView.setOnClickListener {

            val textMessage = "\n\n\n\n\n" +
                    "[Essential Information] \n" +
                    "${systemInformation.getDeviceName()} | API ${Build.VERSION.SDK_INT} | ${systemInformation.getCountryIso().toUpperCase(Locale.getDefault())}"

            val emailIntent = Intent(Intent.ACTION_SEND).apply {
                putExtra(Intent.EXTRA_EMAIL, getString(R.string.supportEmail))
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.feedbackTag) + " [${BuildConfig.VERSION_NAME}]")
                putExtra(Intent.EXTRA_TEXT, textMessage)
                type = "text/*"
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(Intent.createChooser(emailIntent, getString(R.string.feedbackTag)))

        }

        preferencesControlLayoutBinding.goBackView.setOnClickListener {

            this@PreferencesControl.finish()
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

        }

    }

    override fun onResume() {
        super.onResume()

        firebaseUser?.let {

            if (firebaseUser.isAnonymous) {

                preferencesControlLayoutBinding.accountManagerView.visibility = View.VISIBLE

                preferencesControlLayoutBinding.userDisplayName.setLines(2)
                preferencesControlLayoutBinding.userDisplayName.text = Html.fromHtml("${getString(R.string.notLogin)}", Html.FROM_HTML_MODE_COMPACT)

            } else {

                preferencesControlLayoutBinding.accountManagerView.visibility = View.VISIBLE

                preferencesControlLayoutBinding.userDisplayName.setLines(1)
                preferencesControlLayoutBinding.userDisplayName.text = firebaseUser.displayName

                Glide.with(this@PreferencesControl)
                    .asDrawable()
                    .load(firebaseUser.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(preferencesControlLayoutBinding.userProfileImage)

            }

        }

    }

    override fun onBackPressed() {

        this@PreferencesControl.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)

    }

}