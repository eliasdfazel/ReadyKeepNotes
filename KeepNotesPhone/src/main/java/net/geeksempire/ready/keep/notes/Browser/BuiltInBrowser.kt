/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Browser

import android.app.ActivityOptions
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.ClipDrawable
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemeType
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.navigationBarHeight
import net.geeksempire.ready.keep.notes.Utils.UI.Display.statusBarHeight
import net.geeksempire.ready.keep.notes.databinding.BuiltInBrowserLayoutBinding
import java.io.File

class BuiltInBrowser : AppCompatActivity() {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    private lateinit var builtInBrowserLayoutBinding: BuiltInBrowserLayoutBinding

    companion object {

        fun show(context: Context,
                 linkToLoad: String,
                 gradientColorOne: Int?,
                 gradientColorTwo: Int?) {

            Intent(context, BuiltInBrowser::class.java).apply {
                putExtra(Intent.EXTRA_TEXT, linkToLoad)
                putExtra("GradientColorOne", gradientColorOne)
                putExtra("GradientColorTwo", gradientColorTwo)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(this@apply, ActivityOptions.makeCustomAnimation(context, R.anim.slide_in_right, R.anim.fade_out).toBundle())
            }

        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        builtInBrowserLayoutBinding = BuiltInBrowserLayoutBinding.inflate(layoutInflater)
        setContentView(builtInBrowserLayoutBinding.root)

        when (themePreferences.checkThemeLightDark()) {
            ThemeType.ThemeDark -> {

                window.navigationBarColor = getColor(R.color.dark)

                builtInBrowserLayoutBinding.webView.setBackgroundColor(getColor(R.color.dark))

            }
            ThemeType.ThemeLight -> {

                window.navigationBarColor = getColor(R.color.light)

                builtInBrowserLayoutBinding.webView.setBackgroundColor(getColor(R.color.light))

            }
        }

        builtInBrowserLayoutBinding.root.setPadding(0, statusBarHeight(applicationContext) , 0, navigationBarHeight(applicationContext))

        val dominantColor = intent.getIntExtra("GradientColorOne", getColor(R.color.default_color))
        val vibrantColor = intent.getIntExtra("GradientColorTwo", getColor(R.color.default_color_game))

        window.setBackgroundDrawable(GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, arrayOf(vibrantColor, dominantColor).toIntArray()))

        val linkToLoad = intent.getStringExtra(Intent.EXTRA_TEXT)

        if (linkToLoad != null) {

            val progressBarLayerList = getDrawable(R.drawable.web_view_progress_bar_drawable) as LayerDrawable
            val progressBarClipDrawable = progressBarLayerList.findDrawableByLayerId(android.R.id.progress) as ClipDrawable
            progressBarClipDrawable.drawable = GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, arrayOf(vibrantColor, dominantColor).toIntArray())

            builtInBrowserLayoutBinding.webViewProgressBar.indeterminateDrawable = progressBarLayerList

            builtInBrowserLayoutBinding.webView.settings.javaScriptEnabled = true

            builtInBrowserLayoutBinding.webView.settings.builtInZoomControls = true
            builtInBrowserLayoutBinding.webView.settings.displayZoomControls = false
            builtInBrowserLayoutBinding.webView.settings.setSupportZoom(true)

            builtInBrowserLayoutBinding.webView.settings.useWideViewPort = true
            builtInBrowserLayoutBinding.webView.settings.loadWithOverviewMode = true
            builtInBrowserLayoutBinding.webView.setInitialScale(0)

            builtInBrowserLayoutBinding.webView.settings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK
            builtInBrowserLayoutBinding.webView.settings.setAppCacheEnabled(true)
            builtInBrowserLayoutBinding.webView.settings.setAppCachePath(getFileStreamPath("").path + "${File.separator}cache${File.separator}")

            builtInBrowserLayoutBinding.webView.webViewClient = BuiltInWebViewClient()
            builtInBrowserLayoutBinding.webView.webChromeClient = BuiltInChromeWebViewClient()
            builtInBrowserLayoutBinding.webView.addJavascriptInterface(WebInterface(this@BuiltInBrowser), "Android")
            builtInBrowserLayoutBinding.webView.loadUrl(linkToLoad)

        } else {

            this@BuiltInBrowser.finish()

        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onBackPressed() {

        this@BuiltInBrowser.finish()
        overridePendingTransition(R.anim.fade_in, android.R.anim.slide_out_right)

    }

    inner class BuiltInWebViewClient : WebViewClient() {

        override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
            if (request != null) {
                view?.loadUrl(request.url.toString())
            }

            return false
        }

        override fun onPageStarted(webView: WebView?, url: String?, favicon: Bitmap?) {
            super.onPageStarted(webView, url, favicon)

            builtInBrowserLayoutBinding.webViewProgressBar.visibility = View.VISIBLE

        }

        override fun onPageFinished(webView: WebView?, url: String?) {
            super.onPageFinished(webView, url)

            builtInBrowserLayoutBinding.webViewProgressBar.visibility = View.INVISIBLE

        }

    }

    inner class BuiltInChromeWebViewClient : WebChromeClient() {

    }

}