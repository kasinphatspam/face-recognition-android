package com.gura.face_recognition_app.services

import android.content.Context
import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.data.request.LoginRequest
import com.gura.face_recognition_app.data.request.RegisterRequest
import com.gura.face_recognition_app.data.response.LoginResponse
import com.gura.face_recognition_app.data.response.RegisterResponse
import com.gura.face_recognition_app.helper.SharePreferencesHelper
import com.gura.face_recognition_app.repository.AuthRepository
import retrofit2.Response

class AuthService(private var context: Context) {

    private val preferences = SharePreferencesHelper(context).getInstance()
    private val authRepository = AuthRepository(context)

    companion object {
        private const val TAG = "AuthService"
    }

    fun getSessionId(): String? {
        if (!hasSessionId()) {
            return null
        }
        return read().toString()
    }

    suspend fun getCurrentUser(callback: (User?)->Unit) {
        if (!hasSessionId()) {
            callback(null)
            return
        }

        // read session in device storage
        val session = read()

        // validate session by sending to server
        authRepository.currentUser(session!!, object: AuthRepository.AuthMeInterface {
            override fun onSuccess(user: User) {
                callback(user)
                Log.d(TAG, "Fetch current user: ${user.email}")
            }

            override fun onFailure() {
                Log.d(TAG, "ERROR: Fetch current user")
                callback(null)
            }
        })
    }

    suspend fun login(email: String, password: String, callback: (Boolean)->Unit)
    {
        val body = LoginRequest(email, password)
        authRepository.login(body, object: AuthRepository.AuthLoginInterface {
            override fun onResponse(response: LoginResponse) {
                // update session in device storage
                val session = response.session
                write(session)
                Log.d(TAG, "Login success by session id:$session")
                callback(true)
            }

            override fun onFailure(error: String) {
                Log.d(TAG, "ERROR: Login failure with $error")
                callback(false)
            }
        })
    }

    suspend fun register(
        email: String,
        password: String,
        firstname: String,
        lastname: String,
        personalId: String,
        callback: (Boolean) -> Unit
    ) {
        val body = RegisterRequest(email, password, firstname, lastname, personalId)
        authRepository.register(body, object: AuthRepository.AuthRegisterInterface {
            override fun onSuccess(response: RegisterResponse) {
                // update session in device storage
                write(response.session)
                Log.d(TAG, "Register success by session id:${response.session}")
                callback(true)
            }

            override fun onFailure(error: String) {
                Log.d(TAG, "ERROR: Register failure with $error")
                callback(false)
            }

        })
    }

    fun logout() {
        if (!hasSessionId()) {
            return
        }
        Log.d(TAG, "Logout success")
        // clear session in device storage
        clear()
    }


    /* Share Preference for storing session id
     * there are 2 main function of this features
     * - write => store or update login session in device storage
     * - read  => read session id to verify and validate service requests
     * */
    private fun write(session: String?) {
        Log.d(TAG, "Update login session")
        preferences.edit().putString("session", session).apply()
    }

    private fun read(): String? {
        Log.d(TAG, "Fetch login session")
        return preferences.getString("session", null)
    }

    private fun clear() {
        Log.d(TAG, "Clear login session")
        write(null)
    }

    private fun hasSessionId(): Boolean {
        if ((preferences.getString("session", null) == null)) {
            Log.d(TAG, "Login session is empty")
            return false
        }
        return true
    }

}