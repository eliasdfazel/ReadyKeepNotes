package net.geeksempire.ready.keep.notes.ContentContexts.NetworkOperations

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.*
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import net.geeksempire.ready.keep.notes.ContentContexts.Endpoints.NaturalLanguageProcessingEndpoints
import net.geeksempire.ready.keep.notes.Utils.Network.Extensions.JsonRequestResponseInterface
import org.json.JSONArray
import org.json.JSONObject


object EnqueueEndPointQuery {
    const val JSON_REQUEST_TIMEOUT = (1000 * 3)
    const val JSON_REQUEST_RETRIES = (3)
}

object TextRazorParameters {
    const val Response = "response"
    const val ExtractorsEntities = "entities"
    const val AnalyzedText = "matchedText"
}

class NaturalLanguageProcessNetworkOperation(private val context: AppCompatActivity) {

    fun start(
        textContent: String,
        jsonRequestResponseInterface: JsonRequestResponseInterface?
    ) = CoroutineScope(Dispatchers.IO).async {

        val jsonObjectRequest = object :  JsonObjectRequest(
            Request.Method.POST,
            NaturalLanguageProcessingEndpoints.TextRazorEndpoint,
            requestBodyTextRazor(textContent),
            { response ->
                Log.d(
                    "JsonObjectRequest ${this@NaturalLanguageProcessNetworkOperation.javaClass.simpleName}",
                    response.toString()
                )

                if (response != null) {

                    jsonRequestResponseInterface?.jsonRequestResponseSuccessHandler(response)

                    val allJson: JSONArray =
                        response.getJSONObject(TextRazorParameters.Response).getJSONArray(
                            TextRazorParameters.ExtractorsEntities
                        )

                    for (i in 0..allJson.length()) {

                        val detectedJsonObject = allJson[i] as JSONObject
                        println(">>> >> > " + detectedJsonObject[TextRazorParameters.ExtractorsEntities])
                    }

                }

            }, {
                Log.d("JsonObjectRequestError", it?.networkResponse?.statusCode.toString())

                jsonRequestResponseInterface?.jsonRequestResponseFailureHandler(it?.networkResponse?.statusCode)

            }) {

            override fun getHeaders(): Map<String, String> {
                super.getHeaders()

                val headersMap = HashMap<String, String>()

//                headersMap["x-textrazor-key"] = context.getString(R.string.textRazorKey)
//                headersMap["extractors"] = TextRazorParameters.ExtractorsEntities
//                headersMap["text"] = textContent

                return headersMap
            }

            override fun getBodyContentType(): String {
                super.getBodyContentType()



                return ("x-textrazor-key:" + "4f69908551abda223f747cda44b8ab5009637071d1e6be6b3dbbc6de" +
                        "extractors=" + "entities"
                        + "&"
                        + "text=" + "Do you like patterns? It’s enough to look at your wardrobe, surely you’ll see printed clothes or accessories. Using patterns is popular among many people because they add some diversities and a spirit to your style. On the other side, every fashion designer uses iconic patterns in their collections, as we can see prints are a fashion trend in these years according to all fashion weeks. So, you need to know how to use patterns in your style or how to coordinate different patterns together.")
            }
        }

        requestBodyTextRazor(textContent)


        jsonObjectRequest.retryPolicy = DefaultRetryPolicy(
            EnqueueEndPointQuery.JSON_REQUEST_TIMEOUT,
            EnqueueEndPointQuery.JSON_REQUEST_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT
        )

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(jsonObjectRequest)
    }

    private fun requestBodyTextRazor(textContent: String) : JSONObject? {

        val jsonObjectRequest = JSONObject()

//        jsonObjectRequest.put("accept", "application/json")
//        jsonObjectRequest.put("x-textrazor-key", context.getString(R.string.textRazorKey))
//        jsonObjectRequest.put("extractors=", TextRazorParameters.ExtractorsEntities)
//        jsonObjectRequest.put("text=", textContent)

        return jsonObjectRequest
    }

}