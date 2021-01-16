package net.geeksempire.ready.keep.notes.AccountManager.UserInterface.Extensions

import android.content.Intent
import android.text.Html
import android.util.Log
import android.view.Gravity
import android.view.Menu
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformation
import net.geeksempire.ready.keep.notes.Browser.BuiltInBrowser
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDatabase
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Data.resizeDrawable
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder

class MoreOptions(private val context: AccountInformation) {

    object MenuItemIdentifier {
        const val PrivacyAgreementItem = 0
        const val SignOutItem = 1
    }

    fun setup() {

        context.accountInformationLayoutBinding.moreOptions.setOnClickListener {

            val popupMenu = PopupMenu(context, it, Gravity.CENTER, 0, R.style.GeeksEmpire_Material)

            popupMenu.menu.add(Menu.NONE, 0, 0,
                Html.fromHtml("<font color='" + context.getColor(R.color.light) + "'>" + context.getString(R.string.privacyAgreement) + "</font>", Html.FROM_HTML_MODE_COMPACT))

            popupMenu.menu.add(Menu.NONE, 1, 1,
                Html.fromHtml("<font color='" + context.getColor(R.color.light) + "'>" + context.getString(R.string.signOutText) + "</font>", Html.FROM_HTML_MODE_COMPACT))
                .icon = context.getDrawable(R.drawable.not_login_icon)?.resizeDrawable(context, 77, 77)

            try {
                val fields = popupMenu.javaClass.declaredFields
                for (field in fields) {
                    if ("mPopup" == field.name) {
                        field.isAccessible = true
                        val menuPopupHelper = field[popupMenu]
                        val classPopupHelper = Class.forName(menuPopupHelper.javaClass.name)
                        val setForceIcons = classPopupHelper.getMethod(
                            "setForceShowIcon",
                            Boolean::class.javaPrimitiveType
                        )
                        setForceIcons.invoke(menuPopupHelper, true)
                        break
                    }
                }
            } catch (e: Throwable) {
                e.printStackTrace()
            }

            popupMenu.setOnMenuItemClickListener { menuItem ->

                when (menuItem?.itemId) {
                    MenuItemIdentifier.PrivacyAgreementItem -> {

                        BuiltInBrowser.show(
                            context = context,
                            linkToLoad = context.getString(R.string.privacyAgreementLink),
                            gradientColorOne = context.getColor(R.color.default_color_dark),
                            gradientColorTwo = context.getColor(R.color.default_color_game_dark)
                        )

                        true
                    }
                    MenuItemIdentifier.SignOutItem -> {

                        SnackbarBuilder(context).show(
                            rootView = context.accountInformationLayoutBinding.rootView,
                            messageText = context.getString(R.string.signOutConfirmText),
                            messageDuration = Snackbar.LENGTH_INDEFINITE,
                            actionButtonText = R.string.signInText,
                            snackbarActionHandlerInterface = object :
                                SnackbarActionHandlerInterface {

                                override fun onActionButtonClicked(snackbar: Snackbar) {
                                    super.onActionButtonClicked(snackbar)

                                    Firebase.auth.currentUser?.let { firebaseUser ->

                                        (context.application as KeepNoteApplication).firebaseStorage
                                            .getReference("ReadyKeepNotes/${firebaseUser.uid}/")
                                            .delete()
                                            .addOnCompleteListener {
                                                Log.d(this@MoreOptions.javaClass.simpleName, "Storage Deleted")

                                                (context.application as KeepNoteApplication).firestoreDatabase
                                                    .document(DatabaseEndpoints().baseEndpoints(firebaseUser.uid))
                                                    .delete()
                                                    .addOnCompleteListener {
                                                        Log.d(this@MoreOptions.javaClass.simpleName, "Database Deleted")

                                                        (context.application as KeepNoteApplication).firestoreDatabase
                                                            .document(UserInformation.userProfileDatabasePath(firebaseUser.uid))
                                                            .delete()
                                                            .addOnCompleteListener {
                                                                Log.d(this@MoreOptions.javaClass.simpleName, "Profile Deleted")

                                                                Firebase.auth.currentUser
                                                                    ?.delete()
                                                                    ?.addOnCompleteListener {
                                                                        Log.d(this@MoreOptions.javaClass.simpleName, "Firebase User Deleted")

                                                                        Firebase.auth.signOut()

                                                                        try {

                                                                            context.cacheDir.delete()
                                                                            context.getFileStreamPath("/").delete()
                                                                            context.deleteDatabase(NotesDatabase)

                                                                            context.startActivity(Intent(context, EntryConfigurations::class.java)
                                                                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                                                                            context.finish()

                                                                        } catch (e: Exception) {
                                                                            e.printStackTrace()

                                                                        }

                                                                    }

                                                            }

                                                    }

                                            }

                                    }

                                }

                            }
                        )

                        true
                    }
                    else -> {
                        false
                    }
                }
            }

            popupMenu.show()

        }

    }

}