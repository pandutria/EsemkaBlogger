package com.example.esemkablogger.ui

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.widget.ArrayAdapter
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.data.model.Category
import com.example.esemkablogger.databinding.ActivityAddPostScreenBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

class AddPostScreen : AppCompatActivity() {
    private var _binding: ActivityAddPostScreenBinding? = null
    private val binding get() = _binding!!
    var thumbnailByte: ByteArray? = null
    var imageByte: ByteArray? = null
    var type = "thumbnail"

    val pickMediaThummbnail =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val binaryData = getByteArrayFromUri(uri)
                thumbnailByte = binaryData
                binding.etFileThumbnail.setText(getFileNameFromUri(this, uri))
            } else {

            }
        }

    val pickMediaImage =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                val binaryData = getByteArrayFromUri(uri)
                imageByte = binaryData
                binding.etFileImage.setText(getFileNameFromUri(this, uri))

            } else {

            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityAddPostScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnThumbnail.setOnClickListener {
            type = "thumbnail"
            pickMediaThummbnail.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btnImage.setOnClickListener {
            type = "image"
            pickMediaImage.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        binding.btn.setOnClickListener {
            if (binding.etTitle.text.toString().isEmpty() || binding.etText.text.toString()
                    .isEmpty() ||
                (binding.spinnerCategory.selectedItem as Category).name == "Select Category"
            ) {
                Helper.toast(this, "All field must be filled")
                return@setOnClickListener
            }
            createPost()
        }

        showDataCategory()
    }

    fun createPost() {
        lifecycleScope.launch {
            val rBody = JSONObject().apply {
                put("id", UUID.randomUUID().toString())
                put("title", binding.etTitle.text.toString())
                put("content", binding.etText.text.toString())
                put("categoryId", (binding.spinnerCategory.selectedItem as Category).id)
            }

            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "posts",
                    "POST",
                    token = TokenManager(this@AddPostScreen).get(),
                    rBody = rBody.toString()
                )
            }

            if (result.code in 200..300) {
                val data = JSONObject(result.body)

                if (binding.etFileThumbnail.text.toString().isNotEmpty()) {
                    createPostThumbnail(data.getString("id"))
                }

                if (binding.etFileImage.text.toString().isNotEmpty()) {
                    createPostImage(data.getString("id"))
                }

                Helper.toast(this@AddPostScreen, "Create post success")
            } else {
                Helper.toast(this@AddPostScreen, result.body)
            }
        }
    }

    fun createPostThumbnail(id: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().requestImage(
                    "posts/$id/thumbnail",
                    thumbnailByte!!,
                    TokenManager(this@AddPostScreen).get().toString()
                )
            }

            if (result.code in 200..300) {
                Helper.toast(this@AddPostScreen, "Create thumbnail success")
                finish()
            } else {
                Helper.toast(this@AddPostScreen, result.body)
            }
        }
    }

    fun createPostImage(id: String) {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().requestImage(
                    "posts/$id/image",
                    imageByte!!,
                    TokenManager(this@AddPostScreen).get().toString()
                )
            }

            if (result.code in 200..300) {
                Helper.toast(this@AddPostScreen, "Create image success")
                finish()
            } else {
                Helper.toast(this@AddPostScreen, result.body)
            }
        }
    }

    fun getByteArrayFromUri(uri: Uri): ByteArray {
        return contentResolver.openInputStream(uri).use {
            it!!.readBytes()
        }
    }

    fun getFileNameFromUri(context: Context, uri: Uri): String? {
        var fileName: String? = null
        if (uri.scheme == "content") {
            context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        fileName = cursor.getString(nameIndex)
                    }
                }
            }
        }
        return fileName
    }

    fun showDataCategory() {
        val list: MutableList<Category> = mutableListOf()
        list.add(
            Category(
                "0",
                "Select Category"
            )
        )
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "categories"
                )
            }

            if (result.code in 200..300) {
                val array = JSONArray(result.body)

                for (i in 0 until array.length()) {
                    val data = array.getJSONObject(i)

                    list.add(
                        Category(
                            id = data.getString("id"),
                            name = data.getString("name")
                        )
                    )
                }
                val adapter = ArrayAdapter(
                    this@AddPostScreen,
                    android.R.layout.simple_spinner_dropdown_item,
                    list
                )

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerCategory.adapter = adapter
            }
        }
    }
}