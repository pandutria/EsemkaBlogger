package com.example.esemkablogger.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.esemkablogger.R
import com.example.esemkablogger.data.HttpHandler
import com.example.esemkablogger.data.local.ExpTokenManager
import com.example.esemkablogger.data.local.TokenManager
import com.example.esemkablogger.databinding.ActivityLoginScreenBinding
import com.example.esemkablogger.utils.Helper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

class LoginScreen : AppCompatActivity() {
    private var _binding: ActivityLoginScreenBinding? = null
    private val binding get() = _binding!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        _binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.etUsername.setText("string")
        binding.etPassword.setText("string")

        binding.tvSIgn.setOnClickListener {
            startActivity(Intent(this, RegisterScreen::class.java))
        }

        binding.layoutPassword.setEndIconOnClickListener {
            if (binding.etPassword.inputType == (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)) {
                binding.etPassword.inputType =
                    (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD)
            } else {
                binding.etPassword.inputType =
                    (EditorInfo.TYPE_CLASS_TEXT or EditorInfo.TYPE_TEXT_VARIATION_PASSWORD)
            }
        }

        binding.btn.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty() || binding.etPassword.text.toString()
                    .isEmpty()
            ) {
                Helper.toast(this, "All fields must be filled")
                return@setOnClickListener
            }

            login()
        }
    }

    fun login() {
        lifecycleScope.launch {
            val rBody = JSONObject().apply {
                put("username", binding.etUsername.text.toString())
                put("password", binding.etPassword.text.toString())
            }

            val result = withContext(Dispatchers.IO) {
                HttpHandler().request(
                    "auth/login",
                    "POST",
                    rBody = rBody.toString()
                )
            }

            if (result.code in 200..300) {
                val data = JSONObject(result.body)

                TokenManager(this@LoginScreen).save(data.getString("token"))
                Log.d("token", TokenManager(this@LoginScreen).get().toString())
                ExpTokenManager(this@LoginScreen).save(data.getString("expiredAt"))
                Helper.toast(this@LoginScreen, "Login success")
                startActivity(Intent(this@LoginScreen, MainScreen::class.java))
            } else {
                Helper.toast(this@LoginScreen, "Please correct the error and try again")
            }
        }
    }

}