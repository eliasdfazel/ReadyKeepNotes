/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.ContentContexts.DataStructure.TagsData
import net.geeksempire.ready.keep.notes.ContentContexts.DataStructure.TextRazorParameters
import net.geeksempire.ready.keep.notes.ContentContexts.Endpoints.NaturalLanguageProcessingEndpoints
import net.geeksempire.ready.keep.notes.Database.DataStructure.Notes
import net.geeksempire.ready.keep.notes.Database.Json.JsonIO
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.R
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

class ContentContextProcess(appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {

    private val databaseEndpoints = DatabaseEndpoints()

    private val jsonIO = JsonIO()

    private var workerResult: Result = Result.failure()

    override suspend fun doWork() : Result {

        val requestBody = workerParams.inputData.getByteArray("Request_Body")?.let { String(it) }

        val firebaseUserId = workerParams.inputData.getByteArray("FirebaseUserId")?.let { String(it) }
        val firebaseDocumentId = workerParams.inputData.getByteArray("FirebaseDocumentId")?.let { String(it) }

        val urlConnection = URL(NaturalLanguageProcessingEndpoints.TextRazorEndpoint)

        if (requestBody != null
            && firebaseUserId != null
            && firebaseDocumentId != null) {

            val httpsConnection = urlConnection.openConnection() as HttpsURLConnection
            httpsConnection.requestMethod = "POST"
            httpsConnection.setRequestProperty("accept", "application/json")
            httpsConnection.setRequestProperty("x-textrazor-key", applicationContext.getString(R.string.textRazorKey))

            httpsConnection.doOutput = true

            httpsConnection.connect()

            val dataOutputStream = DataOutputStream(httpsConnection.outputStream)
            dataOutputStream.writeBytes(requestBody)

            dataOutputStream.flush()
            dataOutputStream.close()

            val bufferReader = BufferedReader(InputStreamReader(httpsConnection.inputStream))

            var inputLine: String?

            val response = StringBuffer()

            while (bufferReader.readLine().also { inputLine = it } != null) {
                response.append(inputLine)
            }

            bufferReader.close()

            try {

                val completeJsonObject = JSONObject(response.toString())

                val allJsonResponseArray = (completeJsonObject.get(TextRazorParameters.Response) as JSONObject)[TextRazorParameters.ExtractorsEntities] as JSONArray

                val allTags = ArrayList<TagsData>()

                for (i in 0 until allJsonResponseArray.length()) {

                    val jsonObject = allJsonResponseArray[i] as JSONObject

                    val aWikiDataId = jsonObject[TextRazorParameters.WikiDataId].toString()
                    val aWikiLink = jsonObject[TextRazorParameters.WikiLink].toString()
                    val aTag = jsonObject[TextRazorParameters.AnalyzedText].toString()

                    allTags.add(TagsData(
                        wikiDataId = aWikiDataId,
                        wikiLink = aWikiLink,
                        aTag = aTag
                    ))

                }

                Firebase.firestore
                    .document(databaseEndpoints.generalEndpoints(firebaseUserId) + "/" + firebaseDocumentId)
                    .update(
                        Notes.NotesTags, jsonIO.writeTagsData(allTags)
                    ).addOnSuccessListener {


                    }.addOnFailureListener {

                    }

                val workOutputData = workDataOf(
                    "Document_Id" to firebaseDocumentId.toByteArray(Charset.defaultCharset()),
                    "Note_Tags" to allTags.toString().toByteArray(Charset.defaultCharset()))

                workerResult = Result.success(workOutputData)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

        } else {

            workerResult = Result.failure()

        }

        return workerResult
    }

}