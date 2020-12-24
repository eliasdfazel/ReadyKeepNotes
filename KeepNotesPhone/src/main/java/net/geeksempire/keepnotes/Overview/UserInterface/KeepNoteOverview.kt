package net.geeksempire.keepnotes.Overview.UserInterface

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.Utils.System.doVibrate
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ListenerRegistration
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
import net.geeksempire.keepnotes.Overview.UserInterface.Extensions.startNetworkOperation
import net.geeksempire.keepnotes.Preferences.Theme.ThemePreferences
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.InApplicationUpdate.InApplicationUpdateProcess
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListener
import net.geeksempire.keepnotes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.keepnotes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.keepnotes.Utils.UI.Display.columnCount
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.keepnotes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.keepnotes.databinding.OverviewLayoutBinding
import javax.inject.Inject

class KeepNoteOverview : AppCompatActivity(), NetworkConnectionListenerInterface {

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

    var databaseListener: ListenerRegistration? = null

    val notesOverviewViewModel: NotesOverviewViewModel by lazy {
        ViewModelProvider(this@KeepNoteOverview).get(NotesOverviewViewModel::class.java)
    }

    private val itemTouchHelper: ItemTouchHelper by lazy {

        var initialPosition = -1
        var targetPosition = -1

        val simpleItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP or
                    ItemTouchHelper.DOWN or
                    ItemTouchHelper.START or
                    ItemTouchHelper.END,
            0) {

            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {



                return true
            }

            override fun onMoved(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, fromPosition: Int, target: RecyclerView.ViewHolder, toPosition: Int, x: Int, y: Int) {
                super.onMoved(recyclerView, viewHolder, fromPosition, target, toPosition, x, y)


                val overviewAdapter = (recyclerView.adapter as OverviewAdapter)

                if (initialPosition == -1) {
                    initialPosition = fromPosition
                }
                targetPosition = toPosition

                overviewAdapter.notifyItemMoved(initialPosition, targetPosition)

            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {



            }

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                doVibrate(applicationContext, 157)

                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {


                    }
                    ItemTouchHelper.ACTION_STATE_SWIPE -> {


                    }
                    ItemTouchHelper.ACTION_STATE_IDLE -> {


                    }
                }

            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                doVibrate(applicationContext, 57)

                if (initialPosition != -1 && targetPosition != -1) {

                    val overviewAdapter = (recyclerView.adapter as OverviewAdapter)

                    val oldIndex = overviewAdapter.notesDataStructureList[initialPosition][Notes.NoteIndex].toString().toLong()
                    val newIndex = overviewAdapter.notesDataStructureList[targetPosition][Notes.NoteIndex].toString().toLong()



                    Handler(Looper.getMainLooper()).postDelayed({

                        (application as KeepNoteApplication)
                            .firestoreDatabase.document(overviewAdapter.notesDataStructureList[initialPosition].reference.path)
                            .update(
                                Notes.NoteIndex, newIndex,
                            ).addOnSuccessListener {
                                Log.d(this@KeepNoteOverview.javaClass.simpleName, "Database Rearrange Process Completed Successfully | Initial Position")


                            }

                        (application as KeepNoteApplication)
                            .firestoreDatabase.document(overviewAdapter.notesDataStructureList[targetPosition].reference.path)
                            .update(
                                Notes.NoteIndex, oldIndex,
                            ).addOnSuccessListener {
                                Log.d(this@KeepNoteOverview.javaClass.simpleName, "Database Rearrange Process Completed Successfully | Target Positionb")


                            }

                        initialPosition = -1
                        targetPosition = -1

                    }, 555)

                }

            }

        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    @Inject
    lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var overviewLayoutBinding: OverviewLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overviewLayoutBinding = OverviewLayoutBinding.inflate(layoutInflater)
        setContentView(overviewLayoutBinding.root)

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@KeepNoteOverview, overviewLayoutBinding.rootView)
            .inject(this@KeepNoteOverview)

        networkConnectionListener.networkConnectionListenerInterface = this@KeepNoteOverview

        setupColors()

        setupActions()

        overviewLayoutBinding.root.post {

            overviewLayoutBinding.quickTakeNote.post {

                overviewLayoutBinding.quickTakeNote.requestFocus()

                inputMethodManager.showSoftInput(
                    overviewLayoutBinding.quickTakeNote,
                    InputMethodManager.SHOW_FORCED
                )

            }

            overviewLayoutBinding.overviewRecyclerView.layoutManager = GridLayoutManager(applicationContext, columnCount(applicationContext, 313), RecyclerView.VERTICAL, false)

            val overviewAdapter = OverviewAdapter(this@KeepNoteOverview)

            itemTouchHelper.attachToRecyclerView(overviewLayoutBinding.overviewRecyclerView)

            notesOverviewViewModel.notesQuerySnapshots.observe(this@KeepNoteOverview, Observer {

                if (it.isNotEmpty()) {


                    overviewLayoutBinding.overviewRecyclerView.visibility = View.VISIBLE

                    if (overviewAdapter.notesDataStructureList.isNotEmpty()) {

                        overviewAdapter.notesDataStructureList.clear()
                        overviewAdapter.notesDataStructureList.addAll(it)

                        overviewAdapter.notifyDataSetChanged()

                    } else {

                        overviewAdapter.notesDataStructureList.clear()
                        overviewAdapter.notesDataStructureList.addAll(it)

                        overviewLayoutBinding.overviewRecyclerView.adapter = overviewAdapter

                    }

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
                                    putExtra(TakeNote.NoteTakingWritingType.ContentText, overviewLayoutBinding.quickTakeNote.text.toString())
                                }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

                            }

                        }
                    )

                }

            })

            /*Invoke In Application Update*/
            InApplicationUpdateProcess(this@KeepNoteOverview, overviewLayoutBinding.rootView)
                .initialize()

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

    override fun networkAvailable() {

        startNetworkOperation()

    }

    override fun networkLost() {



    }

}