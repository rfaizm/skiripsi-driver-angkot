package com.example.driverangkot.data.preference

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.driverangkot.domain.entity.User
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking


val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class UserPreference(private val dataStore: DataStore<Preferences>) {

    suspend fun saveSession(user: User, token : String) {
        dataStore.edit { preferences ->
            preferences[ID_KEY] = user.id
            preferences[DRIVER_ID] = user.driverId
            preferences[TRAYEK_ID] = user.trayekId
            preferences[NO_HP_KEY] = user.noHp
            preferences[NO_HP_EMERGENCY_KEY] = user.noHpEmergency
            preferences[EMAIL_KEY] = user.email
            preferences[NAME_KEY] = user.name
            preferences[PLATE_NUMBER_KEY] = user.platNumber
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGIN_KEY] = true
        }
    }

    suspend fun saveOnlineStatus(isOnline: Boolean) {
        dataStore.edit { preferences ->
            preferences[IS_ONLINE] = isOnline
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    private fun <T> read(key: Preferences.Key<T>): T? = runBlocking { dataStore.data.first()[key] }

    fun getStatusOnline(): Boolean? = read(IS_ONLINE)
    fun getDriverId(): Int? = read(DRIVER_ID)
    fun getName(): String? = read(NAME_KEY)
    fun getEmail(): String? = read(EMAIL_KEY)
    fun getLogin(): Boolean? = read(IS_LOGIN_KEY)
    fun getTrayekId(): Int? = read(TRAYEK_ID)
    fun getAuthToken(): String? = read(TOKEN_KEY)

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        private val ID_KEY = intPreferencesKey("userId")
        private val DRIVER_ID = intPreferencesKey("driverId")
        private val TRAYEK_ID = intPreferencesKey("trayekId")
        private val NO_HP_KEY = stringPreferencesKey("noHp")
        private val NO_HP_EMERGENCY_KEY = stringPreferencesKey("noHpEmergency")
        private val PLATE_NUMBER_KEY = stringPreferencesKey("plateNumber")
        private val EMAIL_KEY = stringPreferencesKey("email")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")
        private val IS_ONLINE = booleanPreferencesKey("isOnline")
        private val IS_LOGIN_KEY = booleanPreferencesKey("isLogin")


        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
