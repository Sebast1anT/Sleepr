package com.mistershorr.loginandregistration

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.backendless.Backendless
import com.backendless.BackendlessUser
import com.backendless.async.callback.AsyncCallback
import com.backendless.exceptions.BackendlessFault
import com.mistershorr.loginandregistration.RegistrationUtil.existingEmails
import com.mistershorr.loginandregistration.RegistrationUtil.existingUsers
import com.mistershorr.loginandregistration.RegistrationUtil.validateName
import com.mistershorr.loginandregistration.databinding.ActivityRegistrationBinding


class RegistrationActivity : AppCompatActivity()  {

    private lateinit var binding: ActivityRegistrationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // retrieve any information from the intent using the extras keys
        val username = intent.getStringExtra(LoginActivity.EXTRA_USERNAME) ?: ""
        val password = intent.getStringExtra(LoginActivity.EXTRA_PASSWORD) ?: ""

        // prefill the username & password fields
        // for EditTexts, you actually have to use the setText functions
        binding.editTextRegistrationUsername.setText(username)
        binding.editTextTextPassword.setText(password)

        // register an account and send back the username & password
        // to the login activity to prefill those fields
        binding.buttonRegistrationRegister.setOnClickListener {
            val password = binding.editTextTextPassword.text.toString()
            val confirm = binding.editTextRegistrationConfirmPassword.text.toString()
            val username = binding.editTextRegistrationUsername.text.toString()
            val email = binding.editTextRegistrationEmail.text.toString()
            val name = binding.editTextRegistrationName.text.toString()
            if(RegistrationUtil.validatePassword(password, confirm) &&
                RegistrationUtil.validateUsername(username) && RegistrationUtil.validateEmail(email) && validateName(name))  {  // && do the rest of the validations
                // apply lambda will call the functions inside it on the object
                // that apply is called on


                // setting properties using the variables for email, username etc.

                val user = BackendlessUser().apply {
                    setProperty("email", email)
                    setProperty("username", username)
                    setProperty("name", name)
                    setProperty("password", password)

                }
                Backendless.UserService.register(user, object : AsyncCallback<BackendlessUser?> {
                    override fun handleResponse(registeredUser: BackendlessUser?) {
                        existingUsers.add(username)
                        existingEmails.add(email)
                        val resultIntent = Intent().apply {
                            // apply { putExtra() } is doing the same thing as resultIntent.putExtra()
                            putExtra(
                                LoginActivity.EXTRA_USERNAME,
                                binding.editTextRegistrationUsername.text.toString()
                            )
                            putExtra(LoginActivity.EXTRA_PASSWORD, password)
                        }
                        setResult(Activity.RESULT_OK, resultIntent)
                        finish()
                    }

                    override fun handleFault(fault: BackendlessFault) {
                        // an error has occurred, the error code can be retrieved with fault.getCode()
                        Log.d("RegistrationActivity", "handleFault: ${fault.message}")

                        Toast.makeText(this@RegistrationActivity, "yep", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            else {
                Toast.makeText(this@RegistrationActivity, "Double check your stuff", Toast.LENGTH_SHORT).show()
            }
        }

    }
}