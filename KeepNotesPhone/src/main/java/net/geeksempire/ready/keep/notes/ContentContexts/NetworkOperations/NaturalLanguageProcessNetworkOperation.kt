package net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.ContentContexts.DataStructure.TextRazorParameters
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import java.nio.charset.Charset

class NaturalLanguageProcessNetworkOperation(private val context: AppCompatActivity) {

    fun start(firebaseUserId: String, documentId: String, textContent: String) = CoroutineScope(Dispatchers.IO).async {

        val workRequest = OneTimeWorkRequestBuilder<WorkBackgroundProcess>()
            .setInputData(
                workDataOf(
                    "Request_Body" to requestBodyTextRazor(TextRazorParameters.ExtractorsEntities, textContent).toByteArray(Charset.defaultCharset()),
                    "FirebaseUserId" to firebaseUserId.toByteArray(Charset.defaultCharset()),
                    "FirebaseDocumentId" to documentId.toByteArray(Charset.defaultCharset())
                ))
            .build()

        val tagsWorkManager = WorkManager.getInstance(context)
        tagsWorkManager.enqueue(workRequest)

        tagsWorkManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(context, Observer { workInfo ->

                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {

                    workInfo.outputData.getByteArray("Note_Tags")?.let { notesTags ->

                        val allTags = String(notesTags)

                        workInfo.outputData.getByteArray("Document_Id")?.let { documentId ->

                            CoroutineScope(Dispatchers.IO).async {

                                val notesRoomDatabaseConfiguration = (context.application as KeepNoteApplication)
                                    .notesRoomDatabaseConfiguration

                                notesRoomDatabaseConfiguration
                                    .prepareRead()
                                    .updateNoteTagsData(String(documentId).toLong(), allTags)

                                notesRoomDatabaseConfiguration.closeDatabase()

                            }

                        }

                    }

                }

            })

    }

    private fun requestBodyTextRazor(extractorsMethod: String, textContext: String) : String {

        return ("extractors=${extractorsMethod}"
                + "&"
                + "text=${textContext}")
    }

}