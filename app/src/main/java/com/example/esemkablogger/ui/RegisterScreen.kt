package com.example.esemkablogger.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.ExpTokenManager
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.databinding.ActivityRegisterScreenBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class RegisterScreen : AppCompatActivity() {
    private var _binding: ActivityRegisterScreenBinding? = null
    private val binding get() = _binding!!
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityRegisterScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.layoutPassword.setEndIconOnClickListener {
            if (binding.etPassword.inputType ==  (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.etPassword.inputType = (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            } else {
                binding.etPassword.inputType = (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            }
        }

        binding.layoutConnPassword.setEndIconOnClickListener {
            if (binding.etConnPassword.inputType ==  (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.etConnPassword.inputType = (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            } else {
                binding.etConnPassword.inputType = (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
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

            try {
                val formater = DateTimeFormatter.ofPattern("yyyy-MM-dd")
                LocalDate.parse(binding.etDate.text.toString(), formater)
            } catch (e: Exception) {
                Helper.toast(this, "Date must be format yyyy-MM-dd")
                return@setOnClickListener
            }

            register()
        }
    }

    fun register() {
        lifecycleScope.launch {
            val rBody = JSONObject().apply {
                put("username", binding.etUsername.text.toString())
                put("password", binding.etPassword.text.toString())
                put("firstName", binding.etFirstname.text.toString())
                put("lastName", binding.etLastname.text.toString())
                put("dateOfBirth", binding.etDate.text.toString())
            }

            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "auth/register",
                    "POST",
                    rBody = rBody.toString()
                )
            }

            if (result.code in 200..300) {
                startActivity(Intent(this@RegisterScreen, LoginScreen::class.java))
                Helper.toast(this@RegisterScreen, "Register success")
            } else {
                Helper.toast(this@RegisterScreen, JSONObject(result.body).toString())
            }
        }
    }
}