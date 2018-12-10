package  org.zubbl.configs

import android.annotation.SuppressLint
import android.content.SharedPreferences
import org.zubbl.application.AppController
import javax.inject.Inject

class SessionManager {
    @Inject
    lateinit var sharedPreferences: SharedPreferences

    init {
        AppController.appComponent.inject(this)
    }

    fun setToken(token: String?) {
        sharedPreferences.edit().putString("token", token).apply()
    }

    fun getToken(): String? {
        return sharedPreferences.getString("token", "")
    }
    fun setRememberMe(remember:Boolean){
        sharedPreferences.edit().putBoolean("remember",remember).apply()
    }
    fun getRememberMe(): Boolean{
        return sharedPreferences.getBoolean("remember", false)
    }

    fun setpersonId(personid: String?) {
        sharedPreferences.edit().putString("personId", personid!!).apply()
    }

    fun getPersonID(): String {
        return sharedPreferences.getString("personId", "")
    }

    fun setUserName(username: String?) {
        sharedPreferences.edit().putString("userName", username!!).apply()
    }

    fun getUserName(): String? {
        return sharedPreferences.getString("userName", "")
    }
    fun setPassword(password: String?) {
        sharedPreferences.edit().putString("password", password).apply()
    }

    fun getPassword(): String {
        return sharedPreferences.getString("password", "")
    }

    fun setFirstName(firstname: String?) {
        sharedPreferences.edit().putString("name", firstname).apply()
    }

    fun setLastName(lastname: String?) {
        sharedPreferences.edit().putString("lastName", lastname).apply()
    }

    fun getLastName(): String? {
        return sharedPreferences.getString("lastName", "")
    }


    fun getFirstName(): String? {
        return sharedPreferences.getString("name", "")
    }


    fun clearAll() {
        sharedPreferences.edit().clear().apply()
    }

    fun setSessionUserName(sessionUsername: String?) {
        sharedPreferences.edit().putString("sessionUserName", sessionUsername!!).apply()
    }

    fun getSessionUserName(): String? {
        return sharedPreferences.getString("sessionUserName", "")
    }

    }