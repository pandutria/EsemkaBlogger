package com.example.esemkablogger.data

import com.example.esemkablogger.data.model.Http
import com.example.esemkablogger.utils.Helper
import java.io.DataOutputStream
import java.net.HttpURLConnection
import java.net.URL

class HttpHandler() {
    fun request(
        endpoint: String,
        method: String? = "GET",
        token: String? = null,
        rBody: String? = null
    ): Http {
        return try {
            val conn = URL(Helper.url + endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = method
            conn.setRequestProperty("Content-Type", "application/json")

            if (token != null) {
                conn.setRequestProperty("Authorization", "Bearer ${token}")
            }

            if (rBody != null) {
                conn.doOutput = true
                conn.outputStream.use { it.write(rBody.toByteArray()) }
            }

            val code = conn.responseCode
            val body = try {
                conn.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
                conn.errorStream.bufferedReader().use { it.readText() }
            }

            Http(code, body)
        } catch (e: Exception) {
            e.printStackTrace()
            Http(500, e.message ?: "error")
        }
    }

//    fun requestImage(endpoint: String,  binaryData: ByteArray, token: String): Http {
//        return try {
//            val conn = URL(Helper.url + endpoint).openConnection() as HttpURLConnection
//            conn.requestMethod = "POST"
//            conn.doOutput = true
//            conn.setRequestProperty("Content-Type", "application/octet-stream")
//            conn.setRequestProperty("Content-Length", binaryData.size.toString())
//            conn.setRequestProperty("Authorization", "Bearer $token")
//
//            conn.outputStream.use {
//                it.write(binaryData)
//            }
//
//            val code = conn.responseCode
//            val body = try {
//                conn.inputStream.bufferedReader().use { it.readText() }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                conn.errorStream.bufferedReader().use { it.readText() }
//            }
//
//            Http(code, body)
//        } catch (e: Exception) {
//            e.printStackTrace()
//            Http(500, e.message ?: "error")
//        }
//    }

    fun requestImage(endpoint: String,  binaryData: ByteArray, token: String): Http {
        return try {
            val boundary = "123"
            val conn = URL(Helper.url + endpoint).openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.doOutput = true
            conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            conn.setRequestProperty("Authorization", "Bearer $token")

            val output = DataOutputStream(conn.outputStream)
            output.writeBytes("--$boundary\r\n")
            output.writeBytes("Content-Disposition: form-data; name=\"photo\"; filename=\"image.jpg\"\r\n")
            output.writeBytes("Content-Type: application/octet-stream\r\n\r\n")

            output.write(binaryData)
            output.writeBytes("\r\n--$boundary--\r\n")
            output.flush()
            output.close()

            val code = conn.responseCode
            val body = try {
                conn.inputStream.bufferedReader().use { it.readText() }
            } catch (e: Exception) {
                e.printStackTrace()
                conn.errorStream.bufferedReader().use { it.readText() }
            }

            Http(code, body)
        } catch (e: Exception) {
            e.printStackTrace()
            Http(500, e.message ?: "error")
        }
    }
}