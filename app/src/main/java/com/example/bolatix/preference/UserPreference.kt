package com.example.bolatix.preference


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.bolatix.data.models.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesDataStore by preferencesDataStore(name = "user_preferences")

class UserPreferences(private val context: Context) {

    private object PreferencesKeys {
        val id = stringPreferencesKey("id")
        val name = stringPreferencesKey("name")
        val email = stringPreferencesKey("email")
        val profileImage = stringPreferencesKey("profileImage")
        val favoriteTeam = stringPreferencesKey("favoriteTeam")
        val birthday = stringPreferencesKey("birthday")
        val gender = stringPreferencesKey("gender")
        val numberPhone = stringPreferencesKey("numberPhone")
    }

    suspend fun setUser(user: User) {
        context.userPreferencesDataStore.edit { preferences ->
            preferences[PreferencesKeys.id] = user.id ?: ""
            preferences[PreferencesKeys.name] = user.name ?: ""
            preferences[PreferencesKeys.email] = user.email ?: ""
            preferences[PreferencesKeys.profileImage] = user.profilePicture ?: ""
            preferences[PreferencesKeys.favoriteTeam] = user.favoriteTeam ?: ""
            preferences[PreferencesKeys.birthday] = user.birthday ?: ""
            preferences[PreferencesKeys.gender] = user.gender ?: ""
            preferences[PreferencesKeys.numberPhone] = user.phoneNumber ?: ""
        }
    }

    fun getUser(): Flow<User> {
        return context.userPreferencesDataStore.data
            .map { preferences ->
                User(
                    id = preferences[PreferencesKeys.id] ?: "",
                    name = preferences[PreferencesKeys.name] ?: "",
                    email = preferences[PreferencesKeys.email] ?: "",
                    profilePicture = preferences[PreferencesKeys.profileImage] ?: "",
                    favoriteTeam = preferences[PreferencesKeys.favoriteTeam] ?: "",
                    birthday = preferences[PreferencesKeys.birthday] ?: "",
                    gender = preferences[PreferencesKeys.gender] ?: "",
                    phoneNumber = preferences[PreferencesKeys.numberPhone] ?: "",
                )
            }
    }

    suspend fun updateUser(user: User) {
        context.userPreferencesDataStore.edit { preferences ->
            user.id?.let { preferences[PreferencesKeys.id] = it }
            user.name?.let { preferences[PreferencesKeys.name] = it }
            user.email?.let { preferences[PreferencesKeys.email] = it }
            user.profilePicture?.let { preferences[PreferencesKeys.profileImage] = it }
            user.favoriteTeam?.let { preferences[PreferencesKeys.favoriteTeam] = it }
            user.birthday?.let { preferences[PreferencesKeys.birthday] = it }
            user.gender?.let { preferences[PreferencesKeys.gender] = it }
            user.phoneNumber?.let { preferences[PreferencesKeys.numberPhone] = it }
        }
    }


    suspend fun clearUser() {
        context.userPreferencesDataStore.edit { preferences ->
            preferences.clear()
        }
    }
}