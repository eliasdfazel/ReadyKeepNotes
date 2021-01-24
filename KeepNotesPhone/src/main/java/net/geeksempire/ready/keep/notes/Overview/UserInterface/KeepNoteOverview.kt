package net.geeksempire.ready.keep.notes.Overview.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.abanabsalan.aban.magazine.Utils.System.doVibrate
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.inappmessaging.FirebaseInAppMessagingClickListener
import com.google.firebase.inappmessaging.model.Action
import com.google.firebase.inappmessaging.model.InAppMessage
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.Database.IO.DeletingProcess
import net.geeksempire.ready.keep.notes.Database.IO.NoteDatabaseConfigurations
import net.geeksempire.ready.keep.notes.Database.IO.NotesIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.NotesLiveData.NotesOverviewViewModel
import net.geeksempire.ready.keep.notes.Overview.UserInterface.Adapter.*
import net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions.*
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Extensions.checkSpecialCharacters
import net.geeksempire.ready.keep.notes.Utils.InApplicationUpdate.InApplicationUpdateProcess
import net.geeksempire.ready.keep.notes.Utils.InApplicationUpdate.UpdateResponse
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.ready.keep.notes.Utils.PopupShortcuts.PopupShortcutsCreator
import net.geeksempire.ready.keep.notes.Utils.RemoteTasks.Notifications.RemoteMessageHandler
import net.geeksempire.ready.keep.notes.Utils.RemoteTasks.Notifications.RemoteSubscriptions
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.Dialogue.ChangeLogDialogue
import net.geeksempire.ready.keep.notes.Utils.UI.Display.columnCount
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.RecyclerViewItemSwipeHelper
import net.geeksempire.ready.keep.notes.Utils.UI.Gesture.SwipeActions
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.OverviewLayoutBinding
import java.util.*
import javax.inject.Inject


class KeepNoteOverview : AppCompatActivity(),
    NetworkConnectionListenerInterface,
    FirebaseInAppMessagingClickListener {

    val databaseEndpoints = DatabaseEndpoints()

    val notesIO: NotesIO by lazy {
        NotesIO(application as KeepNoteApplication)
    }

    val noteDatabaseConfigurations: NoteDatabaseConfigurations by lazy {
        NoteDatabaseConfigurations(applicationContext)
    }

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val contentEncryption: ContentEncryption = ContentEncryption()

    val notesOverviewViewModel: NotesOverviewViewModel by lazy {
        ViewModelProvider(this@KeepNoteOverview).get(NotesOverviewViewModel::class.java)
    }

    val overviewAdapter: OverviewAdapter by lazy {
        OverviewAdapter(this@KeepNoteOverview)
    }

    private val itemTouchHelper: ItemTouchHelper by lazy {

        var initialPosition = -1
        var targetPosition = -1

        val swipeActions = object : SwipeActions {

            override fun onSwipeToEnd(context: KeepNoteOverview, position: Int) = CoroutineScope(Dispatchers.Main).launch {
                super.onSwipeToEnd(context, position)

                val dataToDelete = context.overviewAdapter.notesDataStructureList[position]

                DeletingProcess(this@KeepNoteOverview)
                    .start(dataToDelete, position)

            }

        }

        val simpleItemTouchCallback = object : RecyclerViewItemSwipeHelper(this@KeepNoteOverview, swipeActions) {

            override fun instantiateUnderlayButton(position: Int): List<RecyclerViewItemSwipeHelper.UnderlayButton> {

                return listOf(
                    overviewAdapter.setupDeleteView(position),
                    overviewAdapter.setupEditView(position),
                    overviewAdapter.setupShareView(position)
                )
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

            override fun onSelectedChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {
                super.onSelectedChanged(viewHolder, actionState)

                doVibrate(applicationContext, 77)

                when (actionState) {
                    ItemTouchHelper.ACTION_STATE_DRAG -> {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "Drag & Drop Process")


                    }
                    ItemTouchHelper.ACTION_STATE_SWIPE -> {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "Swipe Process")


                    }
                    ItemTouchHelper.ACTION_STATE_IDLE -> {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "No Process. It Is Idle")


                    }
                }

            }

            override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                super.clearView(recyclerView, viewHolder)

                doVibrate(applicationContext, 57)

                if (initialPosition != -1 && targetPosition != -1) {

                    val overviewAdapter = (recyclerView.adapter as OverviewAdapter)

                    val oldIndex =
                        overviewAdapter.notesDataStructureList[initialPosition].noteIndex.toString()
                            .toLong()
                    val newIndex =
                        overviewAdapter.notesDataStructureList[targetPosition].noteIndex.toString()
                            .toLong()

                    lifecycleScope.launch {

                        val notesRoomDatabaseConfiguration = (application as KeepNoteApplication)
                            .notesRoomDatabaseConfiguration

                        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration.prepareRead()

                        notesDatabaseDataAccessObject
                            .updateNoteIndex(overviewAdapter.notesDataStructureList[initialPosition].uniqueNoteId.toLong(), newIndex)

                        notesDatabaseDataAccessObject
                            .updateNoteIndex(overviewAdapter.notesDataStructureList[targetPosition].uniqueNoteId.toLong(), oldIndex)

                        overviewAdapter.rearrangeItemsData(initialPosition, targetPosition)

                        notesRoomDatabaseConfiguration.closeDatabase()

                        initialPosition = -1
                        targetPosition = -1

                        Firebase.auth.currentUser?.let { firebaseUser ->

                            (application as KeepNoteApplication)
                                .firestoreDatabase.document(
                                    databaseEndpoints.generalEndpoints(
                                        firebaseUser.uid
                                    ) + "/" + "${initialPosition}"
                                )
                                .update(
                                    Notes.NoteIndex, newIndex,
                                ).addOnSuccessListener {
                                    Log.d(
                                        this@KeepNoteOverview.javaClass.simpleName,
                                        "Database Rearrange Process Completed Successfully | Initial Position"
                                    )

                                    (application as KeepNoteApplication)
                                        .firestoreDatabase.document(
                                            databaseEndpoints.generalEndpoints(
                                                firebaseUser.uid
                                            ) + "/" + "${targetPosition}"
                                        )
                                        .update(
                                            Notes.NoteIndex, oldIndex,
                                        ).addOnSuccessListener {
                                            Log.d(
                                                this@KeepNoteOverview.javaClass.simpleName,
                                                "Database Rearrange Process Completed Successfully | Target Positionb"
                                            )


                                        }

                                }

                        }

                    }

                }

            }

            override fun getSwipeThreshold(viewHolder: RecyclerView.ViewHolder): Float {
                super.getSwipeThreshold(viewHolder)

                return (0.79).toFloat()
            }

        }

        ItemTouchHelper(simpleItemTouchCallback)
    }

    var autoEnterPlaced = false

    var databaseSize: Long = System.currentTimeMillis()
    var databaseTime: Long = 0

    var documentId: Long = System.currentTimeMillis()

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

        setupActions()

        overviewLayoutBinding.root.post {

            ChangeLogDialogue(this@KeepNoteOverview)
                .initializeShow()

            overviewLayoutBinding.quickTakeNote.post {

                overviewLayoutBinding.quickTakeNote.requestFocus()

                showKeyboard(applicationContext, overviewLayoutBinding.quickTakeNote)

                overviewLayoutBinding.quickTakeNote.addTextChangedListener(object : TextWatcher {

                    override fun beforeTextChanged(charSequence: CharSequence?, start: Int, count: Int, after: Int) {

                    }

                    override fun onTextChanged(charSequence: CharSequence?, start: Int, before: Int, count: Int) {

                    }

                    override fun afterTextChanged(editable: Editable?) {

                        editable?.let {

                            try {

                                if (editable[editable.length - 1] == '\n') {

                                    val allLines = editable.toString().split("\n")

                                    val lastLine = allLines[allLines.size - 2]

                                    val specialCharacterData =
                                        lastLine.substring(IntRange(0, 0)).checkSpecialCharacters()

                                    if (specialCharacterData.detected) {

                                        if (lastLine.length == 2) {

                                            autoEnterPlaced = true

                                            overviewLayoutBinding.quickTakeNote.editableText.replace(
                                                editable.length - 4,
                                                editable.length,
                                                ""
                                            )
                                            overviewLayoutBinding.quickTakeNote.append("\n")

                                        } else {

                                            if (!autoEnterPlaced) {

                                                overviewLayoutBinding.quickTakeNote.append(
                                                    specialCharacterData.specialCharacter
                                                )
                                                overviewLayoutBinding.quickTakeNote.setSelection(
                                                    editable.length
                                                )

                                            }

                                            autoEnterPlaced = false

                                        }

                                    }

                                }

                            } catch (e: IndexOutOfBoundsException) {
                                e.printStackTrace()
                            }

                        }

                    }

                })

            }

            overviewLayoutBinding.overviewRecyclerView.layoutManager = GridLayoutManager(
                applicationContext,
                columnCount(applicationContext, 313),
                RecyclerView.VERTICAL,
                false
            )

            overviewLayoutBinding.overviewRecyclerView.adapter = overviewAdapter

            itemTouchHelper.attachToRecyclerView(overviewLayoutBinding.overviewRecyclerView)

            notesOverviewViewModel.notesDatabaseQuerySnapshots.observe(this@KeepNoteOverview, Observer {

                if (it.isNotEmpty()) {

                    if (!overviewLayoutBinding.overviewRecyclerView.isShown) {

                        overviewLayoutBinding.overviewRecyclerView.visibility = View.VISIBLE

                    }

                    if (it.size == 1 && overviewAdapter.notesDataStructureList.size == 0) {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "First Note")

                        overviewAdapter.notesDataStructureList.clear()
                        overviewAdapter.notesDataStructureList.addAll(it)

                        databaseSize++

                        overviewAdapter.notifyDataSetChanged()

                        overviewLayoutBinding.overviewRecyclerView.smoothScrollToPosition(0)

                    } else if (it.size == 1) {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "One Quick Note")

                        overviewAdapter.addItemToFirst(it.first()).invokeOnCompletion {

                            databaseSize++

                            overviewLayoutBinding.overviewRecyclerView.smoothScrollToPosition(0)

                        }

                    } else {
                        Log.d(this@KeepNoteOverview.javaClass.simpleName, "All Notes Loading")

                        overviewAdapter.notesDataStructureList.clear()
                        overviewAdapter.notesDataStructureList.addAll(it)

                        overviewAdapter.notifyDataSetChanged()

                        overviewLayoutBinding.overviewRecyclerView.smoothScrollToPosition(0)

                    }

                    overviewLayoutBinding.waitingViewDownload.visibility = View.INVISIBLE

                } else {

                    overviewAdapter.notesDataStructureList.clear()

                    overviewLayoutBinding.overviewRecyclerView.removeAllViews()

                    overviewLayoutBinding.waitingViewDownload.visibility = View.VISIBLE

                    SnackbarBuilder(applicationContext).show(
                        rootView = overviewLayoutBinding.rootView,
                        messageText = getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = android.R.string.ok,
                        snackbarActionHandlerInterface = object :
                            SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                snackbar.dismiss()

                                overviewLayoutBinding.waitingViewDownload.visibility = View.INVISIBLE

                            }

                        }
                    )

                }

            })

            overviewLayoutBinding.overviewRecyclerView.addOnScrollListener(object :
                RecyclerView.OnScrollListener() {

                override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                    super.onScrollStateChanged(recyclerView, newState)

                    when (newState) {
                        RecyclerView.SCROLL_STATE_IDLE -> {


                        }
                        RecyclerView.SCROLL_STATE_SETTLING -> {


                        }
                        RecyclerView.SCROLL_STATE_DRAGGING -> {


                        }
                    }

                }

            })

            /*Invoke In Application Update*/
            InApplicationUpdateProcess(this@KeepNoteOverview, overviewLayoutBinding.rootView)
                .initialize(object : UpdateResponse {

                    override fun latestVersionAlreadyInstalled() {
                        super.latestVersionAlreadyInstalled()


                    }

                })

        }

        Firebase.auth.currentUser?.let {

            RemoteSubscriptions()
                .subscribe(it.uid)

        }

        if (BuildConfig.VERSION_NAME.toUpperCase(Locale.getDefault()).contains("Beta".toUpperCase(Locale.getDefault()))) {

            RemoteSubscriptions()
                .subscribe("Beta")

        }

    }

    override fun onStart() {
        super.onStart()

        val workRequest = OneTimeWorkRequestBuilder<PopupShortcutsCreator>().build()

        val popupShortcutsWorker = WorkManager.getInstance(applicationContext)
        popupShortcutsWorker.enqueue(workRequest)

    }

    override fun onResume() {
        super.onResume()

        setupOverviewColors()

        loadUserAccountInformation()

        databaseOperationsCheckpoint()

        Firebase.auth.currentUser?.reload()

    }

    override fun onPause() {
        super.onPause()

        if (!overviewLayoutBinding.quickTakeNote.text.isNullOrBlank()) {

            notesIO.saveQuickNotesOnline(context = this@KeepNoteOverview,
                documentId = documentId,
                firebaseUser = Firebase.auth.currentUser,
                overviewLayoutBinding = overviewLayoutBinding,
                contentEncryption = contentEncryption,
                databaseEndpoints = databaseEndpoints)

            notesIO.saveQuickNotesOfflineRetry = notesIO.saveQuickNotesOffline(context = this@KeepNoteOverview,
                documentId = documentId,
                firebaseUser = Firebase.auth.currentUser,
                contentEncryption = contentEncryption)

            documentId = System.currentTimeMillis()

        }

        hideKeyboard(applicationContext, overviewLayoutBinding.quickTakeNote)

    }

    override fun onBackPressed() {

        startActivity(
            Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_HOME)
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            },
            ActivityOptions.makeCustomAnimation(applicationContext, 0, android.R.anim.fade_out)
                .toBundle()
        )

    }

    override fun networkAvailable() {
        Log.d(this@KeepNoteOverview.javaClass.simpleName, "Network Available")

        startNetworkOperation()

    }

    override fun networkLost() {
        Log.d(this@KeepNoteOverview.javaClass.simpleName, "Network Not Available")


    }

    override fun messageClicked(inAppMessage: InAppMessage, action: Action) {

        RemoteMessageHandler()
            .extractData(inAppMessage, action)

    }

}