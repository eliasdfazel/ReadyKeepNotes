package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.Utils.System.showKeyboard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData.SearchViewModel
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Adapter.SearchResultAdapter
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupColors
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.Display.columnCount
import net.geeksempire.ready.keep.notes.databinding.SearchProcessLayoutBinding

class SearchProcess : AppCompatActivity() {

    val contentEncryption = ContentEncryption()

    val firebaseUser = Firebase.auth.currentUser

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

            if (firebaseUser != null) {

                searchProcessLayoutBinding.searchTerm.requestFocus()
                showKeyboard(applicationContext, searchProcessLayoutBinding.searchTerm)

                searchProcessLayoutBinding.recyclerViewSearchResults.layoutManager = GridLayoutManager(
                    applicationContext,
                    columnCount(applicationContext, 313),
                    RecyclerView.VERTICAL,
                    true
                )

                val searchResultAdapter = SearchResultAdapter(this@SearchProcess)

                searchProcessLayoutBinding.recyclerViewSearchResults.adapter = searchResultAdapter

                searchProcessLayoutBinding.goBackView.setOnClickListener {

                    this@SearchProcess.finishAffinity()
                    overridePendingTransition(0, R.anim.fade_out)

                }

                searchViewModel.searchResults.observe(this@SearchProcess, Observer {

                    if (it.isEmpty()) {



                    } else {

                        searchResultAdapter.notesDataStructureList.clear()
                        searchResultAdapter.notesDataStructureList.addAll(it)

                        searchResultAdapter.notifyDataSetChanged()

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

                searchProcessLayoutBinding.searchActionView.setOnClickListener {

                    val searchTermText = searchProcessLayoutBinding.searchTerm.text


                }

            } else {

                startActivity(Intent(this@SearchProcess, AccountInformation::class.java),
                    ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

                this@SearchProcess.finish()

            }

        }

    }

}