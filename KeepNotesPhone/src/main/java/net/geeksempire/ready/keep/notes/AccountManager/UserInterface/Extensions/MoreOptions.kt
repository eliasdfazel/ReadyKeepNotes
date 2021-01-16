package net.geeksempire.ready.keep.notes.AccountManager.UserInterface.Extensions

import android.content.Intent
import android.view.MenuItem
import androidx.appcompat.widget.PopupMenu
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformation
import net.geeksempire.ready.keep.notes.Database.NetworkEndpoints.DatabaseEndpoints
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import java.io.File


class MoreOptions(private val context: AccountInformation) : PopupMenu.OnMenuItemClickListener {

    fun setup() {

        context.accountInformationLayoutBinding.moreOptions.setOnClickListener {

            val popupMenu = PopupMenu(context, it)

            popupMenu.inflate(R.menu.account_information_menu_options)

            popupMenu.setOnMenuItemClickListener(this@MoreOptions)

            popupMenu.show()

        }

    }

    override fun onMenuItemClick(item: MenuItem?): Boolean {

        return when (item?.itemId) {
            R.id.privacyAgreementItem -> {



                true
            }
            R.id.signOutItem -> {

                SnackbarBuilder(context).show (
                    rootView = context.accountInformationLayoutBinding.rootView,
                    messageText= context.getString(R.string.signOutConfirmText),
                    messageDuration = Snackbar.LENGTH_INDEFINITE,
                    actionButtonText = R.string.signInText,
                    snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                        override fun onActionButtonClicked(snackbar: Snackbar) {
                            super.onActionButtonClicked(snackbar)

                            Firebase.auth.currentUser?.let { firebaseUser ->

                                (context.application as KeepNoteApplication).firebaseStorage
                                    .getReference("ReadyKeepNotes/${firebaseUser.uid}")
                                    .delete()

                                (context.application as KeepNoteApplication).firestoreDatabase
                                    .document(DatabaseEndpoints().generalEndpoints(firebaseUser.uid))
                                    .delete()

                                (context.application as KeepNoteApplication).firestoreDatabase
                                    .document(UserInformation.userInformationDatabase(firebaseUser.uid))
                                    .delete()

                                Firebase.auth.currentUser?.delete()?.addOnSuccessListener {

                                    Firebase.auth.signOut()

                                    try {

                                        File("/data/data/${context.packageName}/").delete()

                                    } catch (e: Exception) {
                                        e.printStackTrace()

                                    }

                                    context.startActivity(Intent(context, EntryConfigurations::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                                    context.finish()

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

}