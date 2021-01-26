package cmb.reporter.app.smartcitizenapp.sharedPref

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.preference.PreferenceManager
import android.util.DisplayMetrics
import cmb.reporter.app.smartcitizenapp.models.LoginResponse
import cmb.reporter.app.smartcitizenapp.models.User
import com.google.gson.Gson
import java.util.*

class SharePrefUtil(context: Context) {

    var sharedPreferences: SharedPreferences =
        PreferenceManager.getDefaultSharedPreferences(context)

    fun getStringValue(key: String): String? {
        return sharedPreferences.getString(key, null)
    }

    fun putStringValue(key: String, value: String) {
        val editor = sharedPreferences.edit()
        editor.putString(key, value)
        editor.apply()
    }

    fun putLongValue(key: String, value: Long) {
        val editor = sharedPreferences.edit()
        editor.putLong(key, value)
        editor.apply()
    }

    fun getLongValue(key: String): Long {
        return sharedPreferences.getLong(key, -1L)
    }

    fun is24hoursElapsed(): Boolean {
        val lastAccessedTime = getLongValue(LAST_PHONE_NUMBER_VERIFIED_TIME)
        if (lastAccessedTime == -1L){
            return true
        }
        val now = System.currentTimeMillis()
        return (now - lastAccessedTime) > 24 * 60 * 60 * 1000
    }


    fun setApplicationLocale(context: Context, locale: String) {
        val resources: Resources = context.resources
        val dm: DisplayMetrics = resources.displayMetrics
        val config: Configuration = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(locale.toLowerCase()))
        } else {
            config.locale = Locale(locale.toLowerCase())
        }
        resources.updateConfiguration(config, dm)
    }

    fun saveUser(loginResponse: LoginResponse) {
        val userJson = Gson().toJson(loginResponse)
        putStringValue(USER, userJson)
        putStringValue(USER_ROLE, loginResponse.role.name)
    }

    fun getUser(): User {
        val userJson = getStringValue(USER)
        return Gson().fromJson(userJson, User::class.java)
    }
}

const val USER_PASSWORD = "user_password"
const val AREA_DATA = "area_data"
const val ADMIN_DATA = "admin_data"
const val CATEGORY_DATA = "category_data"
const val USER_ROLE = "user_role"
const val USER = "user"
const val LANGUAGE = "language"
const val PHONE_NUMBER = "phoneNumber"
const val SI = "si"
const val TA = "ta"
const val EN = "en"
const val LAST_PHONE_NUMBER_VERIFIED_TIME = "last_phone_number_verified_time"