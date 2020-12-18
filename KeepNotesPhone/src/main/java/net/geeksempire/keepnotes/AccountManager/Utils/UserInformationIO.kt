/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 11/8/20 9:53 AM
 * Last modified 11/8/20 5:05 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.AccountManager.Utils

import android.content.Context
import net.geeksempire.keepnotes.Utils.PreferencesIO.ReadPreferences
import net.geeksempire.keepnotes.Utils.PreferencesIO.SavePreferences

class UserInformationIO(private val context: Context) {

    fun saveUserInformation(userEmailAddress: String) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "Email", userEmailAddress)

    }

    fun getUserAccountName() : String? {

        return ReadPreferences(context).readPreference("UserInformation", "Email", null)
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

    /**
     * Account Type;
     * InvitationConstant.InvitationTypes.Personal
     * InvitationConstant.InvitationTypes.Business
     **/
    fun saveAccountType(accountType: String) {

        val savePreferences = SavePreferences(context)

        savePreferences.savePreference("UserInformation", "AccountType", accountType)

    }

}