package com.gura.face_recognition_app.services

import android.content.Context
import android.util.Log
import com.gura.face_recognition_app.App
import com.gura.face_recognition_app.data.model.User
import com.gura.face_recognition_app.repository.OrganizationRepository

class OrganizationService(private var context: Context) {

    private val authService = AuthService(context)
    private val organizationRepository = OrganizationRepository(context)

    companion object {
        private const val TAG = "OrganizationService"
    }
    suspend fun requestJoinOrgWithPasscode(passcode: String, callback: (Int)->Unit) {
        // check user is already login
        val session = authService.getSessionId() ?: return

        // check passcode entered is not empty
        if (passcode.isEmpty()) {
            return
        }

        // request services
        organizationRepository.requestJoinOrgWithPasscode(
            session,
            passcode,
            object: OrganizationRepository.RequestJoinInterface {

                override fun onJoinSuccess() {
                    Log.d(TAG, "Join an organization successfully")
                    callback(1)
                }

                override fun onRequestSuccess() {
                    Log.d(TAG,"The request has been sent. Please wait for the admin to accept.")
                    callback(2)
                }

                override fun onAlreadyRequest() {
                    Log.d(TAG, "Already request to join this organization")
                    callback(0)
                }

                override fun onFailure() {
                    Log.d(TAG, "Passcode you entered incorrect.")
                    callback(-1)
                }

            }
        )
    }
}