package com.example.esemkablogger.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.databinding.ActivityUpdateProfileScreenBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class UpdateProfileScreen : AppCompatActivity() {
    private var _binding: ActivityUpdateProfileScreenBinding? = null
    private val binding get() = _binding!!
    var photoByte: ByteArray? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val openMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            photoByte = Helper.getByteArrayFromUri(this, uri)
            binding.etFileThumbnail.setText(Helper.getFileNameFromUri(this, uri))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityUpdateProfileScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.btnPhoto.setOnClickListener {
            openMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        lifecycleScope.launch {
            val user = Helper.me(this@UpdateProfileScreen)
            binding.etFirstname.setText(user?.firstName)
            binding.etLastname.setText(user?.lastName)
            binding.etUsername.setText(user?.username)

            if (user?.photo != "null") {
                binding.etFileThumbnail.setText(user?.photo.toString())
            }
        }


        binding.btn.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()
                || binding.etFirstname.text.toString().isEmpty() || binding.etLastname.textColors.toString().isEmpty()
                || binding.etConnPassword.text.toString().isEmpty()) {
                Helper.toast(this, "All fields must be filled")
                return@setOnClickListener
            }

            if (binding.etPassword.text.toString() != binding.etConnPassword.text.toString()) {
                Helper.toast(this, "Password and confirm password must be same")
                return@setOnClickListener
            }

            update()
        }
    }

    fun update() {
        lifecycleScope.launch {
            val rBody = JSONObject().apply {
                put("username", binding.etUsername.text.toString())
                put("password", binding.etPassword.text.toString())
                put("firstName", binding.etFirstname.text.toString())
                put("lastName", binding.etLastname.text.toString())
            }

            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "me",
                    "PUT",
                    token = TokenManager(this@UpdateProfileScreen).get(),
                    rBody = rBody.toString()
                )
            }

            if (result.code in 200..300) {
                updatePhoto()
            } else {
                Helper.toast(this@UpdateProfileScreen, JSONObject(result.body).toString())
            }
        }
    }

    fun updatePhoto() {
        lifecycleScope.launch {
            val result = withContext(Dispatchers.IO) {
                HttpHandler().requestImage(
                    "me/photo",
                    photoByte!!,
                    TokenManager(this@UpdateProfileScreen).get().toString()
                )
            }

            if (result.code in 200..300) {
                finish()
            } else {
                Helper.toast(this@UpdateProfileScreen, result.body)
            }
        }
    }
}