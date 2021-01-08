package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData.SearchViewModel
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupColors
import net.geeksempire.ready.keep.notes.databinding.SearchProcessLayoutBinding

class SearchProcess : AppCompatActivity() {

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this@SearchProcess).get(SearchViewModel::class.java)
    }

    lateinit var searchProcessLayoutBinding: SearchProcessLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchProcessLayoutBinding = SearchProcessLayoutBinding.inflate(layoutInflater)
        setContentView(searchProcessLayoutBinding.root)

        setupColors()

        searchProcessLayoutBinding.root.post {

            searchProcessLayoutBinding.goBackView.setOnClickListener {

                this@SearchProcess.finishAffinity()
                overridePendingTransition(0, R.anim.fade_out)

            }

            searchViewModel.searchResults.observe(this@SearchProcess, Observer {

                if (it.isEmpty()) {



                } else {



                }

            })

            searchProcessLayoutBinding.searchTerm.setOnEditorActionListener { textView, actionId, keyEvent ->

                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {

                        val searchTermText = searchProcessLayoutBinding.searchTerm.text

                    }
                }

                true
            }

        }

    }

}