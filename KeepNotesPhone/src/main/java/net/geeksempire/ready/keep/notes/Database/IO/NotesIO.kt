package net.geeksempire.ready.keep.notes.Database.IO

import android.content.Context
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations.NaturalLanguageProcessNetworkOperation
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructure
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabaseModel
import net.geeksempire.ready.keep.notes.Database.IO.ServicesIO.BackgroundSavingWork
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.OverviewLayoutBinding
import net.geeksempire.ready.keep.notes.databinding.TakeNoteLayoutBinding
import java.nio.charset.Charset

class NotesIO (private val keepNoteApplication: KeepNoteApplication) {

    private val databaseEndpoints = DatabaseEndpoints()

    private val paintingIO = PaintingIO(keepNoteApplication.applicationContext)

    private val jsonIO = JsonIO()

    lateinit var saveQuickNotesOfflineRetry: Deferred<Any?>

    lateinit var saveNotesAndPaintingOfflineRetry: Deferred<Any?>

    /* Retrieve Notes */
    fun retrieveAllNotes(context: AppCompatActivity, firebaseUser: FirebaseUser?) {

        firebaseUser?.let {

            (keepNoteApplication).firestoreDatabase
                .collection(databaseEndpoints.generalEndpoints(firebaseUser.uid))
                .orderBy(Notes.NoteIndex, Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener { querySnapshot ->

                    if (querySnapshot.documents.isNullOrEmpty()) {

                        insertAllNotesIntoCloudDatabase(context, firebaseUser)

                    } else {

                        insertAllNotesIntoLocalDatabase(context, querySnapshot.documents, firebaseUser)

                    }


                }.addOnFailureListener {



                }

        }

    }

    fun insertAllNotesIntoLocalDatabase(context: AppCompatActivity, allNotes: List<DocumentSnapshot>, firebaseUser: FirebaseUser) = CoroutineScope(Dispatchers.IO).launch {

        allNotes.asFlow()
            .collect { documentSnapshot ->

                val uniqueNoteId = documentSnapshot.id.toLong()

                val noteTitle = documentSnapshot[Notes.NoteTile].toString()
                val noteTextContent = documentSnapshot[Notes.NoteTextContent].toString()

                val noteHandwritingSnapshotLink = documentSnapshot[Notes.NoteHandwritingSnapshotLink].toString()

                val noteTakenTime = (documentSnapshot[Notes.NoteTakenTime] as Timestamp).toDate().time
                val noteEditTime = documentSnapshot[Notes.NoteEditTime]?.let {
                    (documentSnapshot[Notes.NoteEditTime] as Timestamp).toDate().time
                }

                val noteTags = documentSnapshot[Notes.NotesTags].toString()

                keepNoteApplication.firestoreDatabase
                    .collection(databaseEndpoints.paintPathsCollectionEndpoints(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + documentSnapshot.id))
                    .get()
                    .addOnSuccessListener { documentSnapshotPath ->

                        CoroutineScope(Dispatchers.IO).launch {

                            (keepNoteApplication).notesRoomDatabaseConfiguration
                                .updateHandwritingPathsData(uniqueNoteId.toString(), paintingIO.preparePaintingPathsOnline(documentSnapshotPath.documents))

                            insertAllNotesIntoCloudDatabase(context, firebaseUser)

                        }

                    }

                val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentSnapshot.id.toLong(),
                    noteTile = noteTitle,
                    noteTextContent = noteTextContent,
                    noteHandwritingPaintingPaths = null,
                    noteHandwritingSnapshotLink = noteHandwritingSnapshotLink,
                    noteTakenTime = uniqueNoteId,
                    noteEditTime = noteEditTime,
                    noteIndex = documentSnapshot.id.toLong(),
                    noteTags = noteTags,
                    dataSelected = 0
                )

                (keepNoteApplication).notesRoomDatabaseConfiguration
                    .insertNewNoteData(notesDatabaseModel)

            }

    }

    fun insertAllNotesIntoCloudDatabase(context: AppCompatActivity, firebaseUser: FirebaseUser) = CoroutineScope(Dispatchers.IO).async {

        (keepNoteApplication).notesRoomDatabaseConfiguration
            .getAllNotesData().forEach { notesDatabaseModel ->

                    val uniqueNoteId = notesDatabaseModel.uniqueNoteId

                    val noteTitle = notesDatabaseModel.noteTile?:"Untitled Note"
                    val noteTextContent = notesDatabaseModel.noteTextContent?:"No Content"

                    val noteHandwritingPaintingPaths = notesDatabaseModel.noteHandwritingPaintingPaths
                    val noteHandwritingSnapshotLink = notesDatabaseModel.noteHandwritingSnapshotLink

                    val noteTakenTime = notesDatabaseModel.noteTakenTime
                    val noteEditTime = notesDatabaseModel.noteEditTime?:0

                    val noteIndex = notesDatabaseModel.noteIndex

                    val notesDataStructure = NotesDataStructure(
                        uniqueNoteId = uniqueNoteId,
                        noteTile = noteTitle,
                        noteTextContent = noteTextContent,
                        noteHandwritingSnapshotLink = null,
                        noteTakenTime = Timestamp((noteTakenTime / 1000), 0),
                        noteEditTime = Timestamp((noteEditTime / 1000), 0),
                        noteIndex = noteIndex
                    )

                    val databasePath = databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${uniqueNoteId}"

                    /* Save Notes & Snapshot Of Handwriting */
                    (keepNoteApplication).firestoreDatabase
                        .document(databasePath)
                        .set(notesDataStructure)
                        .addOnSuccessListener {
                            Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                            val handwritingSnapshot = context.getFileStreamPath("${uniqueNoteId}.PNG")

                            if (handwritingSnapshot.exists()) {

                                (keepNoteApplication).firebaseStorage
                                    .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid) + "/${uniqueNoteId}.PNG")
                                    .putBytes(handwritingSnapshot.readBytes())
                                    .addOnSuccessListener { uploadTaskSnapshot ->
                                        Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

                                        (keepNoteApplication).firebaseStorage
                                            .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid) + "/${uniqueNoteId}.PNG")
                                            .downloadUrl
                                            .addOnSuccessListener { downloadUrl ->

                                                (keepNoteApplication).firestoreDatabase
                                                    .document(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + uniqueNoteId)
                                                    .update(
                                                        "noteHandwritingSnapshotLink", downloadUrl.toString(),
                                                    ).addOnSuccessListener {
                                                        Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Saved Successfully")



                                                    }.addOnFailureListener {
                                                        Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Did Not Saved")


                                                    }

                                            }.addOnFailureListener {



                                            }

                                        if (noteTextContent.isNotBlank()
                                            || noteTextContent != "No Content") {

                                            NaturalLanguageProcessNetworkOperation(context)
                                                .start(
                                                    firebaseUserId = firebaseUser.uid,
                                                    documentId = uniqueNoteId.toString(),
                                                    textContent = noteTextContent.toString()
                                                )

                                        }

                                    }.addOnFailureListener {
                                        Log.d(this@NotesIO.javaClass.simpleName, "Paint Did Note Saved")


                                    }

                            }

                        }


                /* Save Paths Of Handwriting Notes */
                noteHandwritingPaintingPaths?.let {

                    (keepNoteApplication).firestoreDatabase
                        .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                        .get().addOnSuccessListener { querySnapshot ->

                            querySnapshot.documents.forEach { documentSnapshot ->

                                documentSnapshot.reference.delete()

                            }

                            CoroutineScope(Dispatchers.IO).async {

                                paintingIO.convertJsonArrayPathsToArrayList(noteHandwritingPaintingPaths).collect { overallRedrawPaintingData ->

                                    (keepNoteApplication).firestoreDatabase
                                        .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                                        .add(
                                            hashMapOf(
                                                "paintPath" to jsonIO.writePaintingPathData(overallRedrawPaintingData)
                                            )
                                        )
                                        .addOnSuccessListener {
                                            Log.d(this@NotesIO.javaClass.simpleName, "Handwriting Paths Saved Successfully")



                                        }.addOnFailureListener {


                                        }

                                }

                            }

                        }

                }

            }

    }

    /* Offline Database */
    fun saveNotesAndPaintingOffline(context: TakeNote,
                                    firebaseUser: FirebaseUser?,
                                    takeNoteLayoutBinding: TakeNoteLayoutBinding,
                                    paintingIO: PaintingIO,
                                    paintingCanvasView: PaintingCanvasView,
                                    contentEncryption: ContentEncryption,
                                    documentId: Long) = CoroutineScope(Dispatchers.IO).async {

        firebaseUser?.let {

            try {

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = false
                    takeNoteLayoutBinding.savingView.isEnabled = false

                }

                val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:"Untitled Note"
                val contentText = takeNoteLayoutBinding.editTextContentView.text?:"No Content"

                val noteHandwritingSnapshot = context.openFileOutput("${documentId}.PNG", Context.MODE_PRIVATE)

                noteHandwritingSnapshot.write(paintingIO.takeScreenshot(paintingCanvasView))

                val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                    noteTile = contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid).asList().toString(),
                    noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                    noteHandwritingPaintingPaths = jsonIO.writeAllPaintingPathData(paintingCanvasView.overallRedrawPaintingData),
                    noteHandwritingSnapshotLink = context.getFileStreamPath("${documentId}.PNG").absolutePath,
                    noteTakenTime = documentId,
                    noteEditTime = null,
                    noteIndex = documentId,
                    noteTags = null
                )

                if (context.intent.getBooleanExtra(TakeNote.NoteConfigurations.UpdateExistingNote, false)) {

                    (keepNoteApplication).notesRoomDatabaseConfiguration
                        .updateNoteData(notesDatabaseModel)

                } else {

                    (keepNoteApplication).notesRoomDatabaseConfiguration
                        .insertNewNoteData(notesDatabaseModel)

                }

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = true
                    takeNoteLayoutBinding.savingView.isEnabled = true

                }

                context.noteDatabaseConfigurations.lastTimeDatabaseUpdate(System.currentTimeMillis())

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(SupervisorJob() + Dispatchers.Main) {

                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")

                    SnackbarBuilder(context).show (
                        rootView = takeNoteLayoutBinding.rootView,
                        messageText= context.getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = R.string.retryText,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                if (::saveNotesAndPaintingOfflineRetry.isInitialized) {

                                    saveNotesAndPaintingOfflineRetry.start()

                                }

                                snackbar.dismiss()

                            }

                        }
                    )

                }

            }

        }

    }

    fun saveQuickNotesOffline(context: KeepNoteOverview,
                              documentId: Long,
                              firebaseUser: FirebaseUser?,
                              contentEncryption: ContentEncryption) = CoroutineScope(Dispatchers.IO).async {

        firebaseUser?.let {

            try {

                if (context.overviewLayoutBinding.quickTakeNote.text?.isNotBlank() == true) {

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        if (context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled) {

                            context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                            context.overviewLayoutBinding.textInputQuickTakeNote.error = null

                        }

                        context.overviewLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                        context.overviewLayoutBinding.quickTakeNote.clearFocus()

                        hideKeyboard(context, context.overviewLayoutBinding.quickTakeNote)

                        context.overviewLayoutBinding.savingView.isEnabled = false

                    }

                    val contentText = context.overviewLayoutBinding.quickTakeNote.text?:"No Content"

                    val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                        noteTile = null,
                        noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                        noteHandwritingPaintingPaths = null,
                        noteHandwritingSnapshotLink = null,
                        noteTakenTime = documentId,
                        noteEditTime = null,
                        noteIndex = documentId,
                        noteTags = null
                    )

                    (keepNoteApplication).notesRoomDatabaseConfiguration
                        .insertNewNoteData(notesDatabaseModel)

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        context.overviewLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                        context.overviewLayoutBinding.savingView.isEnabled = true

                        context.overviewLayoutBinding.quickTakeNote.text = null

                        context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                        context.overviewLayoutBinding.textInputQuickTakeNote.error = null

                        showKeyboard(context, context.overviewLayoutBinding.quickTakeNote)

                        context.overviewLayoutBinding.quickTakeNote.requestFocus()

                    }

                    context.notesOverviewViewModel.notesDatabaseQuerySnapshots.postValue(arrayListOf(notesDatabaseModel))

                } else {

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                        context.overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(SupervisorJob() + Dispatchers.Main) {

                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")

                    SnackbarBuilder(context).show (
                        rootView = context.overviewLayoutBinding.rootView,
                        messageText= context.getString(R.string.emptyNotesCollection),
                        messageDuration = Snackbar.LENGTH_INDEFINITE,
                        actionButtonText = R.string.retryText,
                        snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                            override fun onActionButtonClicked(snackbar: Snackbar) {
                                super.onActionButtonClicked(snackbar)

                                if (::saveQuickNotesOfflineRetry.isInitialized) {

                                    saveQuickNotesOfflineRetry.start()

                                }

                                snackbar.dismiss()

                            }

                        }
                    )

                }

            }

        }

    }

    /* Online Database */
    fun saveNotesAndPaintingOnline(context: TakeNote,
                                   firebaseUser: FirebaseUser?,
                                   takeNoteLayoutBinding: TakeNoteLayoutBinding,
                                   databaseEndpoints: DatabaseEndpoints,
                                   paintingIO: PaintingIO,
                                   paintingCanvasView: PaintingCanvasView,
                                   contentEncryption: ContentEncryption,
                                   documentId: Long) {

        firebaseUser?.let {

            if (!firebaseUser.isAnonymous) {

                val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:"Untitled Note"
                val contentText = takeNoteLayoutBinding.editTextContentView.text?:"No Content"

                val notesDataStructure = NotesDataStructure(
                    uniqueNoteId = documentId,
                    noteTile = contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid).asList().toString(),
                    noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                    noteHandwritingSnapshotLink = null,
                    noteIndex = documentId
                )

                val databasePath = databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${documentId}"

                /* Save Notes & Snapshot Of Handwriting */
                (keepNoteApplication).firestoreDatabase
                    .document(databasePath)
                    .set(notesDataStructure)
                    .addOnSuccessListener {
                        Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                        (keepNoteApplication).firebaseStorage
                            .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid) + "/${documentId}.PNG")
                            .putBytes(paintingIO.takeScreenshot(paintingCanvasView))
                            .addOnSuccessListener { uploadTaskSnapshot ->
                                Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

                                (keepNoteApplication).firebaseStorage
                                    .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid) + "/${documentId}.PNG")
                                    .downloadUrl
                                    .addOnSuccessListener { downloadUrl ->

                                        (keepNoteApplication).firestoreDatabase
                                            .document(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + documentId)
                                            .update(
                                                "noteHandwritingSnapshotLink", downloadUrl.toString(),
                                            ).addOnSuccessListener {
                                                Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Saved Successfully")



                                            }.addOnFailureListener {
                                                Log.d(this@NotesIO.javaClass.simpleName, "Paint Link Did Not Saved")


                                            }

                                    }.addOnFailureListener {



                                    }

                            }.addOnFailureListener {
                                Log.d(this@NotesIO.javaClass.simpleName, "Paint Did Note Saved")


                            }

                        if (contentText.isNotBlank()
                            || contentText != "No Content") {

                            NaturalLanguageProcessNetworkOperation(context)
                                .start(
                                    firebaseUserId = firebaseUser.uid,
                                    documentId = documentId.toString(),
                                    textContent = contentText.toString()
                                )

                        }

                    }.addOnFailureListener {
                        Log.d(this@NotesIO.javaClass.simpleName, "Note Did Note Saved")

                        SnackbarBuilder(context).show (
                            rootView = takeNoteLayoutBinding.rootView,
                            messageText= context.getString(R.string.emptyNotesCollection),
                            messageDuration = Snackbar.LENGTH_INDEFINITE,
                            actionButtonText = R.string.retryText,
                            snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                                override fun onActionButtonClicked(snackbar: Snackbar) {
                                    super.onActionButtonClicked(snackbar)

                                    saveNotesAndPaintingOnline(
                                        context,
                                        firebaseUser,
                                        takeNoteLayoutBinding,
                                        databaseEndpoints,
                                        paintingIO,
                                        paintingCanvasView,
                                        contentEncryption,
                                        documentId
                                    )

                                    snackbar.dismiss()

                                }

                            }
                        )

                    }

                /* Save Text Content Archive */
                (keepNoteApplication).firestoreDatabase
                    .collection(databaseEndpoints.noteTextsEndpoints(databasePath))
                    .add(hashMapOf(
                        "noteTile" to notesDataStructure.noteTile,
                        "noteTextContent" to notesDataStructure.noteTextContent
                    ))
                    .addOnSuccessListener {
                        Log.d(this@NotesIO.javaClass.simpleName, "Note Archive Saved Successfully")


                    }.addOnFailureListener {



                    }

                /* Save Paths Of Handwriting Notes */
                (keepNoteApplication).firestoreDatabase
                    .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                    .get().addOnSuccessListener {

                        it.documents.forEach { documentSnapshot ->

                            documentSnapshot.reference.delete()

                        }

                        paintingCanvasView.overallRedrawPaintingData.forEach { aPathXY ->

                            (keepNoteApplication).firestoreDatabase
                                .collection(databaseEndpoints.paintPathsCollectionEndpoints(databasePath))
                                .add(
                                    hashMapOf(
                                        "paintPath" to jsonIO.writePaintingPathData(aPathXY)
                                    )
                                )
                                .addOnSuccessListener {
                                    Log.d(this@NotesIO.javaClass.simpleName, "Handwriting Paths Saved Successfully")



                                }.addOnFailureListener {


                                }

                        }

                    }

            }

        }

    }

    fun saveQuickNotesOnline(context: AppCompatActivity,
                             documentId: Long,
                             firebaseUser: FirebaseUser?,
                             overviewLayoutBinding: OverviewLayoutBinding,
                             contentEncryption: ContentEncryption,
                             databaseEndpoints: DatabaseEndpoints) {

        firebaseUser?.let {

            if (!firebaseUser.isAnonymous) {

                if (overviewLayoutBinding.quickTakeNote.text?.isNotBlank() == true) {

                    if (overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled) {

                        overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                        overviewLayoutBinding.textInputQuickTakeNote.error = null

                    }

                    val contentText = overviewLayoutBinding.quickTakeNote.text?:"No Content"

                    val notesDataStructure = NotesDataStructure(
                        uniqueNoteId = documentId,
                        noteTile = contentEncryption.encryptEncodedData("Untitled Note", firebaseUser.uid).asList().toString(),
                        noteTextContent = contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid).asList().toString(),
                        noteIndex = documentId
                    )

                    (keepNoteApplication).firestoreDatabase
                        .document(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${documentId}")
                        .set(notesDataStructure)
                        .addOnSuccessListener {

                            if (contentText.isNotBlank()
                                || contentText != "No Content") {

                                NaturalLanguageProcessNetworkOperation(context)
                                    .start(
                                        firebaseUserId = firebaseUser.uid,
                                        documentId = documentId.toString(),
                                        textContent =  contentText.toString()
                                    )

                            }

                        }.addOnFailureListener {

                            overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                            overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.onlineSavingError)

                        }

                } else {

                    overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                    overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

                }


            }

        }

    }

    /* Save Data In Complete Background */
    fun startBackgroundSavingProcess(documentId: String) {

        val workRequest = OneTimeWorkRequestBuilder<BackgroundSavingWork>()
            .setInputData(
                workDataOf(
                    "FirebaseDocumentId" to documentId.toByteArray(Charset.defaultCharset())
                )
            )
            .build()

        val tagsWorkManager = WorkManager.getInstance(keepNoteApplication.applicationContext)
        tagsWorkManager.enqueue(workRequest)

    }

}