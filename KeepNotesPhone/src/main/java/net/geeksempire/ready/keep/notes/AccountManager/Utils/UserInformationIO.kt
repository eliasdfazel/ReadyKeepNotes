/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.AccountManager.Utils

import android.content.Context
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.ready.keep.notes.Utils.PreferencesIO.SavePreferences

class UserInformationIO(private val context: Context) {

    fun saveUserInformation(userEmailAddress: String) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "Email", userEmailAddress)

    }

    fun getUserAccountName() : String? {

        return ReadPreferences(context).readPreference("UserInformation", "Email", null)
    }

    fun saveNewUserInformation(newUser: Boolean) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "NewUser", newUser)

    }

    fun newUser() : Boolean {

        return ReadPreferences(context).readPreference("UserInformation", "NewUser", false)
    }

    fun saveOldFirebaseUniqueIdentifier(oldFirebaseUniqueIdentifier: String) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "OldFirebaseUniqueIdentifier", oldFirebaseUniqueIdentifier)

    }

    fun getOldFirebaseUniqueIdentifier() : String {

        val readPreferences = ReadPreferences(context)

        return readPreferences.readPreference("UserInformation", "OldFirebaseUniqueIdentifier", "").orEmpty()
    }

    fun saveNewFirebaseUniqueIdentifier(newFirebaseUniqueIdentifier: String) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "NewFirebaseUniqueIdentifier", newFirebaseUniqueIdentifier)

    }

    fun getNewFirebaseUniqueIdentifier() : String {

        val readPreferences = ReadPreferences(context)

        return readPreferences.readPreference("UserInformation", "NewFirebaseUniqueIdentifier", "").orEmpty()
    }

    fun userSignedIn() : Boolean {

        return (ReadPreferences(context).readPreference("UserInformation", "Email", "Unknown") != "Unknown")
    }

    fun savePrivacyAgreement() {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "PrivacyAgreement", true)

    }

    fun readPrivacyAgreement() : Boolean {

        val readPreferences = ReadPreferences(context)

        return readPreferences.readPreference("UserInformation", "PrivacyAgreement", false)
    }

    fun userIsReturning() : Boolean {

        return (getOldFirebaseUniqueIdentifier() != getNewFirebaseUniqueIdentifier())
    }

}