package org.zubbl.utils

import android.support.v7.widget.AppCompatEditText
import java.util.regex.Pattern

class Validator {
    private val EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@" + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$"
    private var USERNAME_PATTERN = "^[A-Za-z\\s]{1,15}$"
            //"^[A-Za-z][A-Za-z\\s]{1,15}$"

    fun passwordValidate(password: String): Boolean {
        val pwd = password.trim { it <= ' ' }
        val pattern = Pattern.compile(PASSWORD_PATTERN)
        val matcher = pattern.matcher(pwd)
        return pwd.length > 0 && matcher.matches()
    }

    fun isValidateEmail(email: String): Boolean {
        val emailId = email.trim { it <= ' ' }
        val pattern = Pattern.compile(EMAIL_PATTERN)
        val matcher = pattern.matcher(emailId)
        return emailId.length > 0 && matcher.matches()
    }
    fun isValidateUsername(username: String): Boolean {
        val userName = username.trim { it <= ' ' }
        val pattern = Pattern.compile(USERNAME_PATTERN)
        val matcher = pattern.matcher(userName)
        return matcher.matches()
    }
    fun conformPasswordValidate(newPassword: String,conformPassword:String): Boolean {
        return newPassword.equals(conformPassword)
    }


    companion object {
        // at least 8 characters. => {8,}
        // at lease 1 numeric => (?=.*\\d)
        // at lease 1 capital => (?=.*[A-Z])
        private val PASSWORD_PATTERN = "^(?=.*[A-Za-z]{2})(?=.*[0-9]{2}).{8,}$"
    }
}