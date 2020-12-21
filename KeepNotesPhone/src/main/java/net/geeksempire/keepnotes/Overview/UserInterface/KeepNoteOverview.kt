package net.geeksempire.keepnotes.Overview.UserInterface

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import net.geeksempire.keepnotes.Database.DataStructure.Notes
import net.geeksempire.keepnotes.Database.GeneralEndpoints.DatabaseEndpoints
import net.geeksempire.keepnotes.Database.IO.NotesIO
import net.geeksempire.keepnotes.KeepNoteApplication
import net.geeksempire.keepnotes.Notes.Taking.TakeNote
import net.geeksempire.keepnotes.Overview.NotesLiveData.NotesOverviewViewModel
import net.geeksempire.keepnotes.Overview.UserInterface.Adapter.OverviewAdapter
import net.geeksempire.keepnotes.Overview.UserInterface.Extensions.setupActions
import net.geeksempire.keepnotes.Overview.UserInterface.Extensions.setupColors
import net.geeksempire.keepnotes.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.keepnotes.Utils.UI.Display.columnCount
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.keepnotes.databinding.OverviewLayoutBinding

class KeepNoteOverview : AppCompatActivity() {

    val firebaseUser = Firebase.auth.currentUser

    val databaseEndpoints = DatabaseEndpoints()

    val notesIO: NotesIO by lazy {
        NotesIO(application as KeepNoteApplication)
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val contentEncryption: ContentEncryption = ContentEncryption()

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    lateinit var overviewLayoutBinding: OverviewLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overviewLayoutBinding = OverviewLayoutBinding.inflate(layoutInflater)
        setContentView(overviewLayoutBinding.root)

        setupColors()

        setupActions()

        overviewLayoutBinding.root.post {

            overviewLayoutBinding.overviewRecyclerView.layoutManager = GridLayoutManager(applicationContext, columnCount(applicationContext, 313), RecyclerView.VERTICAL, false)

            val overviewAdapter = OverviewAdapter(this@KeepNoteOverview)

            overviewLayoutBinding.quickTakeNote.post {

                overviewLayoutBinding.quickTakeNote.requestFocus()

                inputMethodManager.showSoftInput(
                    overviewLayoutBinding.quickTakeNote,
                    InputMethodManager.SHOW_FORCED
                )

            }

            val notesOverviewViewModel = ViewModelProvider(this@KeepNoteOverview).get(NotesOverviewViewModel::class.java)

            notesOverviewViewModel.notesQuerySnapshots.observe(this@KeepNoteOverview, Observer {

                if (it.isNotEmpty()) {


                    overviewLayoutBinding.overviewRecyclerView.visibility = View.VISIBLE

                    overviewAdapter.notesDataStructureList.clear()
                    overviewAdapter.notesDataStructureList.addAll(it)

                    overviewLayoutBinding.overviewRecyclerView.adapter = overviewAdapter

                    overviewLayoutBinding.waitingViewDownload.visibility = View.INVISIBLE

                } else {

                    overviewAdapter.notesDataStructureList.clear()

                    overviewLayoutBinding.overviewRecyclerView.removeAllViews()

                    overviewLayoutBinding.waitingViewDownload.visibility = View.VISIBLE

                    SnackbarBuilder(applicationContext).show (
                        rootView = overviewLayoutBinding.rootView,
                        messageText= getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = android.R.string.ok,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                startActivity(Intent(applicationContext, TakeNote::class.java).apply {
                                    putExtra(TakeNote.NoteTakingWritingType.ExtraConfigurations, TakeNote.NoteTakingWritingType.Keyboard)
                                    putExtra(Intent.EXTRA_TEXT, overviewLayoutBinding.quickTakeNote.text.toString())
                                }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

                            }

                        }
                    )

                }

            })

            firebaseUser?.let {

                (application as KeepNoteApplication)
                    .firestoreDatabase.collection(databaseEndpoints.generalEndpoints(firebaseUser.uid))
                    .orderBy(Notes.NoteTakenTime, Query.Direction.DESCENDING)
                    .addSnapshotListener { querySnapshot, firestoreException ->

                        querySnapshot?.let {

                            notesOverviewViewModel.processDocumentSnapshots(querySnapshot.documents)

                        }

                        firestoreException?.printStackTrace()
                    }

                Glide.with(applicationContext)
                    .load(firebaseUser.photoUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .transform(CircleCrop())
                    .into(overviewLayoutBinding.profileImageView)

            }

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