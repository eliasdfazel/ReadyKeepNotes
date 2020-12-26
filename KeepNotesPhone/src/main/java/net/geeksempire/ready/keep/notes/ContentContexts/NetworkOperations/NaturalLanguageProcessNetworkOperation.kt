package net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations

import androidx.lifecycle.Observer
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.workDataOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Utils.Network.Extensions.JsonRequestResponseInterface


object EnqueueEndPointQuery {
    const val JSON_REQUEST_TIMEOUT = (1000 * 3)
    const val JSON_REQUEST_RETRIES = (3)
}

object TextRazorParameters {
    const val Response = "response"
    const val ExtractorsEntities = "entities"
    const val AnalyzedText = "matchedText"
}

class NaturalLanguageProcessNetworkOperation(private val context: TakeNote) {

    fun start(
        textContent: String,
        jsonRequestResponseInterface: JsonRequestResponseInterface?
    ) = CoroutineScope(Dispatchers.IO).async {

        val workRequest = OneTimeWorkRequestBuilder<WorkBackgroundProcess>()
            .setInputData(workDataOf(
                "Request_Body" to requestBodyTextRazor(TextRazorParameters.ExtractorsEntities, textContent)
            ))
            .build()


        val tagsWorkManager = WorkManager.getInstance(context)
        tagsWorkManager.enqueue(workRequest)

        tagsWorkManager.getWorkInfoByIdLiveData(workRequest.id)
            .observe(context, Observer { workInfo ->


                if (workInfo != null && workInfo.state == WorkInfo.State.SUCCEEDED) {

                    workInfo.outputData.getByteArray("Note_Tags")?.let {

                        val allTags = String(it)

                        context.jsonIO.writeTagsLineSeparated(allTags)


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