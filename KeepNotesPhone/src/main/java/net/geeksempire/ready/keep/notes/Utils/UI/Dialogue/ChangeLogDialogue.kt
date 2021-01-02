package net.geeksempire.ready.keep.notes.Utils.UI.Dialogue

import android.app.Dialog
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.text.Html
import android.view.Window
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Data.FileIO
import net.geeksempire.ready.keep.notes.Utils.Data.percentage
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY
import net.geeksempire.ready.keep.notes.databinding.ChangeLogLayoutBinding
import kotlin.math.roundToInt

class ChangeLogDialogue (val context: AppCompatActivity) {

    private val themePreferences = ThemePreferences(context)

    private val fileIO = FileIO(context)

    fun initializeShow() : Dialog {

        val changeLogLayoutBinding = ChangeLogLayoutBinding.inflate(context.layoutInflater)

        val layoutParams = WindowManager.LayoutParams()
        layoutParams.width = displayX(context).percentage(70.0).roundToInt() //dialogueWidth
        layoutParams.height = displayY(context).percentage(50.0).roundToInt() //dialogueHeight
        layoutParams.windowAnimations = android.R.style.Animation_Dialog
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        layoutParams.dimAmount = 0.57f

        val dialogue = Dialog(context)
        dialogue.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogue.setContentView(changeLogLayoutBinding.root)
        dialogue.setCancelable(true)
        dialogue.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialogue.window?.decorView?.setBackgroundColor(Color.TRANSPARENT)
        dialogue.window?.attributes = layoutParams

        when (themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeLight -> {

                changeLogLayoutBinding.dialogueView.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.lighter))

                changeLogLayoutBinding.dialogueTitle.setTextColor(context.getColor(R.color.darker))
                changeLogLayoutBinding.dialogueContent.setTextColor(context.getColor(R.color.dark))

                changeLogLayoutBinding.rateIt.setBackgroundColor(context.getColor(R.color.white))
                changeLogLayoutBinding.followIt.setBackgroundColor(context.getColor(R.color.white))

                changeLogLayoutBinding.rateIt.setTextColor(context.getColor(R.color.black))
                changeLogLayoutBinding.followIt.setTextColor(context.getColor(R.color.black))

            }
            ThemeType.ThemeDark -> {

                changeLogLayoutBinding.dialogueView.backgroundTintList = ColorStateList.valueOf(context.getColor(R.color.darker))

                changeLogLayoutBinding.dialogueTitle.setTextColor(context.getColor(R.color.lighter))
                changeLogLayoutBinding.dialogueContent.setTextColor(context.getColor(R.color.light))

                changeLogLayoutBinding.rateIt.setBackgroundColor(context.getColor(R.color.black))
                changeLogLayoutBinding.followIt.setBackgroundColor(context.getColor(R.color.black))

                changeLogLayoutBinding.rateIt.setTextColor(context.getColor(R.color.white))
                changeLogLayoutBinding.followIt.setTextColor(context.getColor(R.color.white))

            }
        }

        changeLogLayoutBinding.dialogueTitle.text = Html.fromHtml("${context.getString(R.string.whatNew)} <small> ${BuildConfig.VERSION_CODE}</small>", Html.FROM_HTML_MODE_COMPACT)
        changeLogLayoutBinding.dialogueContent.text = Html.fromHtml(context.getString(R.string.changelog), Html.FROM_HTML_MODE_COMPACT)

        changeLogLayoutBinding.rateIt.setOnClickListener {
            if (!context.isFinishing) {
                dialogue.dismiss()
            }

            context.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.playStoreLink))))
        }

        changeLogLayoutBinding.followIt.setOnClickListener {
            if (!context.isFinishing) {
                dialogue.dismiss()
            }

            context.startActivity(Intent(Intent.ACTION_VIEW,
                Uri.parse(context.getString(R.string.twitterLink))))
        }

        dialogue.setOnDismissListener {

            fileIO.saveFile(".Updated", "${BuildConfig.VERSION_CODE}")

            dialogue.window?.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        }

        if (!context.getFileStreamPath(".Updated").exists()) {

            if (!context.isFinishing) {
                dialogue.show()
            }

        } else if (BuildConfig.VERSION_CODE > fileIO.readFile(".Updated")?.toInt()?:0) {

            if (!context.isFinishing) {
                dialogue.show()
            }

        }

        return dialogue
    }

}