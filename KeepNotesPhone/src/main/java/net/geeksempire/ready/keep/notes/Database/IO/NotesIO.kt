package net.geeksempire.ready.keep.notes.Database.IO

import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
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
import net.geeksempire.ready.keep.notes.Database.IO.ServicesIO.EncryptionMigratingWork
import net.geeksempire.ready.keep.notes.Database.IO.ServicesIO.RetrieveFiles
import net.geeksempire.ready.keep.notes.Database.IO.ServicesIO.TransferFiles
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.PaintingCanvasView
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Extensions.nullCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.OverviewLayoutBinding
import net.geeksempire.ready.keep.notes.databinding.TakeNoteLayoutBinding
import java.io.File
import java.io.FileOutputStream

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

    private fun insertAllNotesIntoLocalDatabase(context: AppCompatActivity, allNotes: List<DocumentSnapshot>, firebaseUser: FirebaseUser) = CoroutineScope(Dispatchers.IO).launch {

        val notesRoomDatabaseConfiguration = (keepNoteApplication).notesRoomDatabaseConfiguration

        val notesDatabaseDataAccessObject = notesRoomDatabaseConfiguration.prepareRead()

        allNotes.asFlow()
            .collect { documentSnapshot ->

                val uniqueNoteId = documentSnapshot.id.toLong()

                val noteTitle = documentSnapshot[Notes.NoteTile].nullCheckpoint()
                val noteTextContent = documentSnapshot[Notes.NoteTextContent].nullCheckpoint()

                val noteHandwritingPaintingPaths = documentSnapshot[Notes.NoteHandwritingPaintingPaths].nullCheckpoint()
                val noteHandwritingSnapshotLink = documentSnapshot[Notes.NoteHandwritingSnapshotLink].nullCheckpoint()

                val noteVoicePaths = documentSnapshot[Notes.NoteVoicePaths].nullCheckpoint()
                val noteImagePaths = documentSnapshot[Notes.NoteImagePaths].nullCheckpoint()
                val noteGifPaths = documentSnapshot[Notes.NoteGifPaths].nullCheckpoint()

                val noteTakenTime = (documentSnapshot[Notes.NoteTakenTime] as Timestamp).toDate().time
                val noteEditTime = documentSnapshot[Notes.NoteEditTime]?.let {
                    (documentSnapshot[Notes.NoteEditTime] as Timestamp).toDate().time
                }

                val noteTags = documentSnapshot[Notes.NotesTags].nullCheckpoint()
                val noteHashTags = documentSnapshot[Notes.NotesHashTags].nullCheckpoint()

                val noteTranscribeTags = documentSnapshot[Notes.NoteTranscribeTags].nullCheckpoint()

                val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentSnapshot.id.toLong(),
                    noteTile = noteTitle,
                    noteTextContent = noteTextContent,
                    noteHandwritingPaintingPaths = noteHandwritingPaintingPaths,
                    noteHandwritingSnapshotLink = noteHandwritingSnapshotLink,
                    noteVoicePaths = noteVoicePaths,
                    noteImagePaths = noteImagePaths,
                    noteGifPaths = noteGifPaths,
                    noteTakenTime = uniqueNoteId,
                    noteEditTime = noteEditTime,
                    noteIndex = documentSnapshot.id.toLong(),
                    noteTags = noteTags,
                    noteHashTags = noteHashTags,
                    noteTranscribeTags = noteTranscribeTags,
                    dataSelected = 0
                )

                notesDatabaseDataAccessObject.insertCompleteNewNoteData(notesDatabaseModel)

                keepNoteApplication.firestoreDatabase
                    .collection(databaseEndpoints.paintPathsCollectionEndpoints(databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + documentSnapshot.id))
                    .get()
                    .addOnSuccessListener { documentSnapshotPath ->

                        CoroutineScope(Dispatchers.IO).launch {

                            val notesRoomDatabaseConfiguration = (keepNoteApplication).notesRoomDatabaseConfiguration

                            notesRoomDatabaseConfiguration
                                .prepareRead()
                                .updateHandwritingPathsData(uniqueNoteId, paintingIO.preparePaintingPathsOnline(documentSnapshotPath.documents))

                            notesRoomDatabaseConfiguration.closeDatabase()

                            insertAllNotesIntoCloudDatabase(context, firebaseUser)

                        }

                    }

            }

        notesRoomDatabaseConfiguration.closeDatabase()

        RetrieveFiles.startProcess(context, context.externalMediaDirs[0].path)

    }

    fun insertAllNotesIntoCloudDatabase(context: AppCompatActivity, firebaseUser: FirebaseUser) = CoroutineScope(Dispatchers.IO).async {

        val notesRoomDatabaseConfiguration = (keepNoteApplication).notesRoomDatabaseConfiguration

        notesRoomDatabaseConfiguration
            .prepareRead()
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
                    noteHandwritingSnapshotLink = noteHandwritingSnapshotLink,
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
                                .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid, uniqueNoteId.toString()) + "/${uniqueNoteId}.PNG")
                                .putBytes(handwritingSnapshot.readBytes())
                                .addOnSuccessListener { uploadTaskSnapshot ->
                                    Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")

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

        notesRoomDatabaseConfiguration.closeDatabase()

        TransferFiles.startProcess(context, context.externalMediaDirs[0].path)

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

                val notesRoomDatabaseConfiguration = (keepNoteApplication).notesRoomDatabaseConfiguration

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.VISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = false
                    takeNoteLayoutBinding.savingView.isEnabled = false

                }

                val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:""
                val contentText = takeNoteLayoutBinding.editTextContentView.text?:""

                var noteHandwritingSnapshotPath: String? = null

                if (paintingCanvasView.overallRedrawPaintingData.isNotEmpty()) {

                    noteHandwritingSnapshotPath = context.handwritingSnapshotLocalFile.getHandwritingSnapshotFilePath(it.uid, documentId.toString())

                    val noteHandwritingSnapshot = File(noteHandwritingSnapshotPath)

                    if (!noteHandwritingSnapshot.exists()) {

                        File(context.handwritingSnapshotLocalFile.getHandwritingSnapshotDirectoryPath(it.uid, documentId.toString())).mkdirs()

                        noteHandwritingSnapshot.createNewFile()

                    }

                    val fileOutputStream = FileOutputStream(noteHandwritingSnapshot)

                    fileOutputStream.write(paintingIO.takeScreenshot(paintingCanvasView))

                } else {

                    with(File(context.handwritingSnapshotLocalFile.getHandwritingSnapshotFilePath(it.uid, documentId.toString()))) {
                        if (exists()) {
                            delete()
                        }
                    }

                }

                if (context.intent.getBooleanExtra(TakeNote.NoteConfigurations.UpdateExistingNote, false)) {

                    val noteTile = if (contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                        null
                    } else { contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().toString() }
                    val noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                        null
                    } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() }

                    val noteHandwritingPaintingPaths = jsonIO.writeAllPaintingPathData(paintingCanvasView.overallRedrawPaintingData)
                    val noteHandwritingSnapshotLink = noteHandwritingSnapshotPath

                    val noteEditTime = System.currentTimeMillis()

                    notesRoomDatabaseConfiguration
                        .prepareRead()
                        .updateNoteData(uniqueNoteId = documentId,
                            noteTitle = noteTile, noteTextContent = noteTextContent,
                            noteHandwritingPaintingPaths = noteHandwritingPaintingPaths,
                            noteHandwritingSnapshotLink = noteHandwritingSnapshotLink,
                            noteEditTime = noteEditTime)

                } else {

                    val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                        noteTile = if (contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            null
                        } else { contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().toString() },
                        noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            null
                        } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() },
                        noteHandwritingPaintingPaths = jsonIO.writeAllPaintingPathData(paintingCanvasView.overallRedrawPaintingData),
                        noteHandwritingSnapshotLink = noteHandwritingSnapshotPath,
                        noteVoicePaths = null,
                        noteImagePaths = null,
                        noteGifPaths = null,
                        noteTakenTime = documentId,
                        noteEditTime = null,
                        noteIndex = documentId,
                        noteTags = null,
                        noteHashTags = null,
                        noteTranscribeTags = null
                    )

                    notesRoomDatabaseConfiguration
                        .prepareRead()
                        .insertCompleteNewNoteData(notesDatabaseModel)

                }

                withContext(SupervisorJob() + Dispatchers.Main) {

                    takeNoteLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                    takeNoteLayoutBinding.toggleKeyboardHandwriting.isEnabled = true
                    takeNoteLayoutBinding.savingView.isEnabled = true

                }

                context.noteDatabaseConfigurations.lastTimeDatabaseUpdate(System.currentTimeMillis())

                notesRoomDatabaseConfiguration.closeDatabase()

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(SupervisorJob() + Dispatchers.Main) {
                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Not Saved")

                    SnackbarBuilder(context).show (
                        rootView = takeNoteLayoutBinding.rootView,
                        messageText= context.getString(R.string.offlineSavingError),
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

                val notesRoomDatabaseConfiguration = (keepNoteApplication).notesRoomDatabaseConfiguration

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

                    val contentText = context.overviewLayoutBinding.quickTakeNote.text?:""

                    val notesDatabaseModel = NotesDatabaseModel(uniqueNoteId = documentId,
                        noteTile = null,
                        noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            null
                        } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() },
                        noteHandwritingPaintingPaths = null,
                        noteHandwritingSnapshotLink = null,
                        noteVoicePaths = null,
                        noteImagePaths = null,
                        noteGifPaths = null,
                        noteTakenTime = documentId,
                        noteEditTime = null,
                        noteIndex = documentId,
                        noteTags = null,
                        noteHashTags = null,
                        noteTranscribeTags = null
                    )

                    notesRoomDatabaseConfiguration
                        .prepareRead()
                        .insertCompleteNewNoteData(notesDatabaseModel)

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        context.overviewLayoutBinding.waitingViewUpload.visibility = View.INVISIBLE

                        context.overviewLayoutBinding.savingView.isEnabled = true

                        context.overviewLayoutBinding.quickTakeNote.text = null

                        context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = false
                        context.overviewLayoutBinding.textInputQuickTakeNote.error = null

                        showKeyboard(context, context.overviewLayoutBinding.quickTakeNote)

                        context.overviewLayoutBinding.quickTakeNote.requestFocus()

                    }

                    context.notesOverviewViewModel.notesDatabaseUnpinned.postValue(arrayListOf(notesDatabaseModel))

                    notesRoomDatabaseConfiguration.closeDatabase()

                } else {

                    withContext(SupervisorJob() + Dispatchers.Main) {

                        context.overviewLayoutBinding.textInputQuickTakeNote.isErrorEnabled = true
                        context.overviewLayoutBinding.textInputQuickTakeNote.error = keepNoteApplication.getString(R.string.noNotesTyped)

                    }

                }

            } catch (e: Exception) {
                e.printStackTrace()

                withContext(SupervisorJob() + Dispatchers.Main) {

                    Log.d(this@NotesIO.javaClass.simpleName, "Note Did Not Saved")

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

                val noteTitle = takeNoteLayoutBinding.editTextTitleView.text?:""
                val contentText = takeNoteLayoutBinding.editTextContentView.text?:""

                var noteHandwritingSnapshotPath: String? = null

                if (paintingCanvasView.overallRedrawPaintingData.isNotEmpty()) {

                    noteHandwritingSnapshotPath = context.handwritingSnapshotLocalFile.getHandwritingSnapshotFilePath(it.uid, documentId.toString())

                }

                val notesDataStructure = if (context.intent.getBooleanExtra(TakeNote.NoteConfigurations.UpdateExistingNote, false)) {

                    NotesDataStructure(
                        uniqueNoteId = documentId,
                        noteTile = if (contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            ""
                        } else { contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().toString() },
                        noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            ""
                        } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() },
                        noteHandwritingSnapshotLink = noteHandwritingSnapshotPath,
                        noteEditTime = Timestamp.now(),
                        noteIndex = documentId
                    )

                } else {

                    NotesDataStructure(
                        uniqueNoteId = documentId,
                        noteTile = if (contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            ""
                        } else { contentEncryption.encryptEncodedData(noteTitle.toString(), firebaseUser.uid)?.asList().toString() },
                        noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            ""
                        } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() },
                        noteHandwritingSnapshotLink = noteHandwritingSnapshotPath,
                        noteIndex = documentId
                    )

                }

                val databasePath = databaseEndpoints.generalEndpoints(firebaseUser.uid) + "/" + "${documentId}"

                /* Save Notes & Snapshot Of Handwriting */
                (keepNoteApplication).firestoreDatabase
                    .document(databasePath)
                    .set(notesDataStructure)
                    .addOnSuccessListener {
                        Log.d(this@NotesIO.javaClass.simpleName, "Note Saved Successfully")

                        (keepNoteApplication).firebaseStorage
                            .getReference(databaseEndpoints.handwritingSnapshotEndpoint(firebaseUser.uid, documentId.toString()) + "/${documentId}.PNG")
                            .putBytes(paintingIO.takeScreenshot(paintingCanvasView))
                            .addOnSuccessListener { uploadTaskSnapshot ->
                                Log.d(this@NotesIO.javaClass.simpleName, "Paint Saved Successfully")


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
                        Log.d(this@NotesIO.javaClass.simpleName, "Note Did Not Saved")

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

                    val contentText = overviewLayoutBinding.quickTakeNote.text?:""

                    val notesDataStructure = NotesDataStructure(
                        uniqueNoteId = documentId,
                        noteTile = contentEncryption.encryptEncodedData("", firebaseUser.uid)?.asList().toString(),
                        noteTextContent = if (contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().isNullOrEmpty()) {
                            ""
                        } else { contentEncryption.encryptEncodedData(contentText.toString(), firebaseUser.uid)?.asList().toString() },
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
    fun startBackgroundMigrationProcess(activity: AppCompatActivity, firebaseUser: FirebaseUser) {

        val workRequest = OneTimeWorkRequestBuilder<EncryptionMigratingWork>()
            .addTag(EncryptionMigratingWork::class.java.simpleName)
            .build()

        val encryptionMigratingWorkManager = WorkManager.getInstance(keepNoteApplication.applicationContext)
        encryptionMigratingWorkManager.enqueue(workRequest)

        encryptionMigratingWorkManager.getWorkInfoByIdLiveData(workRequest.id).observe(activity, Observer {

            when (it.state) {
                WorkInfo.State.SUCCEEDED -> {
                    Log.d(this@NotesIO.javaClass.simpleName, "Data Migrated Successfully")

                    retrieveAllNotes(activity, firebaseUser)

                }
            }

        })

    }

}