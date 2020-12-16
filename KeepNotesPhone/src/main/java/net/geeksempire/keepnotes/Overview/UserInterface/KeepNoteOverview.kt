package net.geeksempire.keepnotes.Overview.UserInterface

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnotes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.keepnotes.Database.IO.NotesIO
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Overview.UserInterface.Extensions.setupColors
import net.geeksempire.keepnotes.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnotes.databinding.OverviewLayoutBinding

class KeepNoteOverview : AppCompatActivity() {

    private val firebaseUser = Firebase.auth.currentUser

    private val databaseEndpoints = DatabaseEndpoints()

    private val notesIO: NotesIO by lazy {
        NotesIO(application as KeepNoteApplication)
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    lateinit var overviewLayoutBinding: OverviewLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overviewLayoutBinding = OverviewLayoutBinding.inflate(layoutInflater)
        setContentView(overviewLayoutBinding.root)

        setupColors()

        overviewLayoutBinding.root.post {

            overviewLayoutBinding.quickTakeNote.post {

                overviewLayoutBinding.quickTakeNote.requestFocus()

                inputMethodManager.showSoftInput(
                    overviewLayoutBinding.quickTakeNote,
                    InputMethodManager.SHOW_FORCED
                )

            }

        }

        overviewLayoutBinding.savingView.setOnClickListener {

            notesIO.saveQuickNotes(firebaseUser = firebaseUser,
                overviewLayoutBinding = overviewLayoutBinding,
                databaseEndpoints = databaseEndpoints)

        }

    }

    override fun onResume() {
        super.onResume()



    }

    override fun onPause() {
        super.onPause()



    }

    override fun onBackPressed() {

        startActivity(Intent(Intent.ACTION_MAIN).apply {
            this.addCategory(Intent.CATEGORY_HOME)
            this.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }, ActivityOptions.makeCustomAnimation(applicationContext, android.R.anim.fade_in, android.R.anim.fade_out).toBundle())

    }

}