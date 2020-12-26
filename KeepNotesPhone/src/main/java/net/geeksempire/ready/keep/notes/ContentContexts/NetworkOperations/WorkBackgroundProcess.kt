package net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import net.geeksempire.ready.keep.notes.ContentContexts.Endpoints.NaturalLanguageProcessingEndpoints
import net.geeksempire.ready.keep.notes.R
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection


class WorkBackgroundProcess(appContext: Context, val workerParams: WorkerParameters) : CoroutineWorker(
    appContext,
    workerParams
) {

    var processResult = false

    override suspend fun doWork() : Result {

        val urlConnection = URL(NaturalLanguageProcessingEndpoints.TextRazorEndpoint)

        val httpsConnection = urlConnection.openConnection() as HttpsURLConnection
        httpsConnection.requestMethod = "POST"
        httpsConnection.setRequestProperty("accept", "application/json")
        httpsConnection.setRequestProperty(
            "x-textrazor-key",
            applicationContext.getString(R.string.textRazorKey)
        )

        httpsConnection.setDoOutput(true)

        val dataOutputStream = DataOutputStream(httpsConnection.outputStream)
        dataOutputStream.writeBytes(workerParams.inputData.getString("Request_Body"))

        dataOutputStream.flush()
        dataOutputStream.close()

        val bufferReader = BufferedReader(InputStreamReader(httpsConnection.inputStream))

        var inputLine: String?

        val response = StringBuffer()

        while (bufferReader.readLine().also { inputLine = it } != null) {
            response.append(inputLine)
        }

        bufferReader.close()

        val completeJsonObject = JSONObject(response.toString())

        val allJsonResponseArray = (completeJsonObject.get(TextRazorParameters.Response) as JSONObject)[TextRazorParameters.ExtractorsEntities] as JSONArray

        val allTags = StringBuilder()

        for (i in 0 until allJsonResponseArray.length()) {

            val jsonObject = allJsonResponseArray[i] as JSONObject

            val aTag = jsonObject[TextRazorParameters.AnalyzedText].toString()

            allTags.append("${aTag}\n")

        }

        val workOutputData = workDataOf("Note_Tags" to allTags.toString().toByteArray(Charset.defaultCharset()))

        val workerResult: Result = Result.success(workOutputData)

        return if (processResult) {
            Result.failure()
        } else {
            workerResult
        }
    }

}