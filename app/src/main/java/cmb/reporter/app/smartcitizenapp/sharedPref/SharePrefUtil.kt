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
        editor.commit()
    }

    fun putIntValue(key: String, value: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt(key, value)
        editor.commit()
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

    fun getUser(): User{
        val userJson = getStringValue(USER)
        return Gson().fromJson(userJson, User::class.java)
    }
}

const val USER_PASSWORD = "user_password"
const val AREA_DATA = "area_data"
const val CATEGORY_DATA = "category_data"
const val USER_ROLE = "user_role"
const val USER = "user"
const val LANGUAGE = "language"
const val PHONE_NUMBER = "phoneNumber"
const val SI = "si"
const val TA = "ta"
const val EN = "en"