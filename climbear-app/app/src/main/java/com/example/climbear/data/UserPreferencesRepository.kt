package com.example.climbear.data

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import com.example.climbear.data.user.model.EditUserInfoRequest
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException
import javax.inject.Inject

class UserPreferencesRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val USER_HEIGHT = doublePreferencesKey("user_height")
        val USER_ARMSPAN = doublePreferencesKey("user_armSpan")
    }

    val userDimensions: Flow<EditUserInfoRequest> = dataStore.data
        .catch {
            if (it is IOException) {
                Log.e("DataStore", "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            EditUserInfoRequest(
            height = preferences[USER_HEIGHT],
            armSpan = preferences[USER_ARMSPAN]
            )
        }

    suspend fun savedUserDimensions(height: Double, reach: Double) {
        dataStore.edit { preferences ->
            preferences[USER_HEIGHT] = height
            preferences[USER_ARMSPAN] = reach
        }
    }

    suspend fun clearUserDimensions() {
        dataStore.edit { preferences ->
            preferences.remove(USER_HEIGHT)
            preferences.remove(USER_ARMSPAN)
        }
    }

}