package com.example.crud_firebase_kotlin

import android.content.Context
import android.content.SharedPreferences

class preferences(context: Context) {
    private val TAG_STATUS = "status"
    private val TAG_LEVEL = "level"
    private val TAG_ID_USER = "iduser"
    private val TAG_NOMBRE_USER = "Nombre"
    private val TAG_EMAIL_USER = "Email"
    private val TAG_USERNAME = "UserName"
    private val TAG_PASSWORD_USER = "Password"
    private val TAG_APP = "app"

    private val pref: SharedPreferences =
        context.getSharedPreferences(TAG_APP, Context.MODE_PRIVATE)

    var prefStatus: Boolean
        get() = pref.getBoolean(TAG_STATUS, false)
        set(value) = pref.edit().putBoolean(TAG_STATUS, value).apply()

    var prefLevel: String?
        get() = pref.getString(TAG_LEVEL, "")
        set(value) = pref.edit().putString(TAG_LEVEL, value).apply()
    var prefIdUser: String?
        get() = pref.getString(TAG_ID_USER, "")
        set(value) = pref.edit().putString(TAG_ID_USER, value).apply()
    var prefNombreUser: String?
        get() = pref.getString(TAG_NOMBRE_USER, "")
        set(value) = pref.edit().putString(TAG_NOMBRE_USER, value).apply()
    var prefEmailUser: String?
        get() = pref.getString(TAG_EMAIL_USER, "")
        set(value) = pref.edit().putString(TAG_EMAIL_USER, value).apply()
    var prefUsername: String?
        get() = pref.getString(TAG_USERNAME, "")
        set(value) = pref.edit().putString(TAG_USERNAME, value).apply()
    var prefPasswordUser: String?
        get() = pref.getString(TAG_PASSWORD_USER, "")
        set(value) = pref.edit().putString(TAG_PASSWORD_USER, value).apply()
    fun prefClear(){
        pref.edit().remove(TAG_STATUS).apply()
        pref.edit().remove(TAG_LEVEL).apply()
        pref.edit().remove(TAG_ID_USER).apply()
        pref.edit().remove(TAG_NOMBRE_USER).apply()
        pref.edit().remove(TAG_EMAIL_USER).apply()
        pref.edit().remove(TAG_USERNAME).apply()
        pref.edit().remove(TAG_PASSWORD_USER).apply()
    }

}