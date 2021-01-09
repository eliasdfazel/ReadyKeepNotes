package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.EntryConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData.SearchViewModel
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Adapter.SearchResultAdapter
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupColors
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupViews
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.Display.columnCount
import net.geeksempire.ready.keep.notes.databinding.SearchProcessLayoutBinding
import net.geekstools.floatshort.PRO.Widgets.RoomDatabase.NotesDatabaseDataAccessObject

class SearchProcess : AppCompatActivity() {

    val contentEncryption = ContentEncryption()

    val firebaseUser = Firebase.auth.currentUser

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val searchResultAdapter: SearchResultAdapter by lazy {
        SearchResultAdapter(this@SearchProcess)
    }

    val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this@SearchProcess).get(SearchViewModel::class.java)
    }

    lateinit var searchProcessLayoutBinding: SearchProcessLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchProcessLayoutBinding = SearchProcessLayoutBinding.inflate(layoutInflater)
        setContentView(searchProcessLayoutBinding.root)

        setupViews()

        val notesDatabaseDataAccessObject = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

        searchProcessLayoutBinding.root.post {

            if (firebaseUser != null) {

                searchProcessLayoutBinding.recyclerViewSearchResults.layoutManager = GridLayoutManager(
                    applicationContext,
                    columnCount(applicationContext, 313),
                    RecyclerView.VERTICAL,
                    true
                )

                searchProcessLayoutBinding.recyclerViewSearchResults.adapter = searchResultAdapter

                searchProcessLayoutBinding.goBackView.setOnClickListener {

                    this@SearchProcess.finish()
                    overridePendingTransition(0, R.anim.fade_out)

                }

                searchProcessLayoutBinding.preferencesView.setOnClickListener {

                    val accountInformation = Intent(applicationContext, PreferencesControl::class.java)
                    startActivity(accountInformation, ActivityOptions.makeSceneTransitionAnimation(this@SearchProcess, searchProcessLayoutBinding.preferencesView, getString(R.string.preferenceImageTransitionName)).toBundle())

                }

                searchViewModel.searchResults.observe(this@SearchProcess, Observer {

                    if (it.isEmpty()) {

                        searchProcessLayoutBinding.loadingView.setAnimation(R.raw.search_no_results_found)
                        searchProcessLayoutBinding.loadingView.playAnimation()

                    } else {

                        searchProcessLayoutBinding.loadingView.pauseAnimation()
                        searchProcessLayoutBinding.loadingView.visibility = View.INVISIBLE

                        searchResultAdapter.notesDataStructureList.clear()
                        searchResultAdapter.notesDataStructureList.addAll(it)

                        searchResultAdapter.notifyDataSetChanged()

                    }

                })

                searchProcessLayoutBinding.searchTerm.setOnEditorActionListener { textView, actionId, keyEvent ->

                    when (actionId) {
                        EditorInfo.IME_ACTION_SEARCH -> {

                            searchProcessLayoutBinding.searchTerm.text?.let { searchTermText ->

                                invokeSearchOperations(searchTermText.toString(), notesDatabaseDataAccessObject)

                            }

                        }
                    }

                    true
                }

                searchProcessLayoutBinding.searchActionView.setOnClickListener {

                    searchProcessLayoutBinding.searchTerm.text?.let { searchTermText ->

                        invokeSearchOperations(searchTermText.toString(), notesDatabaseDataAccessObject)

                    }

                }

            } else {

                startActivity(Intent(applicationContext, EntryConfigurations::class.java).apply {
                   flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, 0).toBundle())

                this@SearchProcess.finish()

            }

        }

    }

    override fun onResume() {
        super.onResume()

        setupColors()

    }

    override fun onBackPressed() {

        this@SearchProcess.finish()
        overridePendingTransition(0, R.anim.fade_out)

    }

    private fun invokeSearchOperations(searchTerm: String, notesDatabaseDataAccessObject: NotesDatabaseDataAccessObject) {

        searchProcessLayoutBinding.recyclerViewSearchResults.removeAllViews()
        searchResultAdapter.notesDataStructureList.clear()

        searchProcessLayoutBinding.loadingView.visibility = View.VISIBLE

        searchProcessLayoutBinding.loadingView.setAnimation(R.raw.searching_loading_animation)
        searchProcessLayoutBinding.loadingView.playAnimation()

        searchViewModel.searchInDatabase(contentEncryption.encryptEncodedData(searchTerm, firebaseUser!!.uid).asList().toString(), notesDatabaseDataAccessObject)

    }

}