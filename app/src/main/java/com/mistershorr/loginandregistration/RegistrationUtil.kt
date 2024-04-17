package com.mistershorr.loginandregistration

// object keyword makes it so all the functions are
// static functions
object RegistrationUtil {
    // use this in the test class for the is username taken test
    // make another similar list for some taken emails
    var existingUsers = mutableListOf<String>()
    var existingEmails = mutableListOf<String>()

//    you can use listOf<type>() instead of making the list & adding individually
//    List<String> blah = new ArrayList<String>();
//    blah.add("hi")
//    blah.add("hello")
//

    // isn't empty
    // isn't already taken
    // minimum number of characters is 3
    fun validateUsername(username: String) : Boolean {

            if (!existingUsers.contains(username)){
                return true
            }
        return false


    }

    // make sure meets security requirements (deprecated ones that are still used everywhere)
    // min length 8 chars
    // at least one digit
    // at least one capital letter
    // both passwords match
    // not empty
    fun validatePassword(password : String, confirmPassword: String) : Boolean {
        if(password.length < 8 || confirmPassword.length < 8) {
            return false
        }

        if (password.count { it.isDigit() } <= 0){
            return false
        }
        if (password.count { it.isUpperCase() } <= 0){
            return false
        }
        if (password != confirmPassword && password.isEmpty()){
            return false
        }


        return true
    }

    // isn't empty
    fun validateName(name: String) : Boolean {
        if (name.isNotEmpty()) {
            return true
        }
        return false

    }

    // isn't empty
    // make sure the email isn't used
    // make sure it's in the proper email format user@domain.tld
    fun validateEmail(email: String) : Boolean {
        if (email.isNotEmpty() && !existingEmails.contains(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            return true
        }
        return false
    }
}