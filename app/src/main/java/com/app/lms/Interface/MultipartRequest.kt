import com.android.volley.*
import com.android.volley.toolbox.HttpHeaderParser
import com.android.volley.toolbox.Volley
import java.io.ByteArrayOutputStream
import java.io.IOException

open class MultipartRequestes(
    method: Int,
    url: String,
    private val listener: Response.Listener<NetworkResponse>,
    private val errorListener: Response.ErrorListener
) : Request<NetworkResponse>(method, url, errorListener) {

    override fun parseNetworkResponse(response: NetworkResponse): Response<NetworkResponse> {
        return try {
            Response.success(response, HttpHeaderParser.parseCacheHeaders(response))
        } catch (e: Exception) {
            Response.error(VolleyError(e))
        }
    }

    override fun deliverResponse(response: NetworkResponse) {
        listener.onResponse(response)
    }

    override fun getBodyContentType(): String {
        return "multipart/form-data; boundary=$boundary"
    }

    override fun getBody(): ByteArray {
        val bos = ByteArrayOutputStream()
        try {
            val params = getParams()
            val data = getByteData()
            if (params.isNotEmpty()) {
                for ((key, value) in params) {
                    writeTextPart(bos, key, value)
                }
            }
            if (data.isNotEmpty()) {
                for ((key, dataPart) in data) {
                    writeFilePart(bos, key, dataPart)
                }
            }
            bos.write(("--$boundary--\r\n").toByteArray())
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return bos.toByteArray()
    }

    override fun getParams(): Map<String, String> {
        return emptyMap()
    }

    open fun getByteData(): Map<String, DataPart> {
        return emptyMap()
    }

    private fun writeTextPart(bos: ByteArrayOutputStream, name: String, value: String) {
        bos.write(("--$boundary\r\n").toByteArray())
        bos.write("Content-Disposition: form-data; name=\"$name\"\r\n\r\n".toByteArray())
        bos.write("$value\r\n".toByteArray())
    }

    private fun writeFilePart(bos: ByteArrayOutputStream, name: String, dataPart: DataPart) {
        bos.write(("--$boundary\r\n").toByteArray())
        bos.write("Content-Disposition: form-data; name=\"$name\"; filename=\"${dataPart.fileName}\"\r\n".toByteArray())
        bos.write("Content-Type: ${dataPart.type}\r\n\r\n".toByteArray())
        bos.write(dataPart.content)
        bos.write("\r\n".toByteArray())
    }

    data class DataPart(val fileName: String, val content: ByteArray, val type: String)

    companion object {
        var boundary = "apiclient-${System.currentTimeMillis()}"
    }
}
