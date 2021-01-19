package net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.Utils.System.hideKeyboard
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import net.geeksempire.ready.keep.notes.Database.Configurations.Offline.NotesRoomDatabaseConfiguration
import net.geeksempire.ready.keep.notes.Database.DataStructure.NotesDataStructureSearch
import net.geeksempire.ready.keep.notes.Database.IO.NoteDatabaseConfigurations
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Preferences.Theme.ThemePreferences
import net.geeksempire.ready.keep.notes.Preferences.UserInterface.PreferencesControl
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.SearchConfigurations.SearchLiveData.SearchViewModel
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Adapter.SearchResultAdapter
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupSearchColors
import net.geeksempire.ready.keep.notes.SearchConfigurations.UserInterface.Extensions.setupSearchViews
import net.geeksempire.ready.keep.notes.Utils.Security.Encryption.ContentEncryption
import net.geeksempire.ready.keep.notes.Utils.UI.Display.columnCount
import net.geeksempire.ready.keep.notes.databinding.SearchProcessLayoutBinding

class SearchProcess : AppCompatActivity() {

    val contentEncryption = ContentEncryption()

    val firebaseUser = Firebase.auth.currentUser!!

    val themePreferences: ThemePreferences by lazy {
        ThemePreferences(applicationContext)
    }

    val searchResultAdapter: SearchResultAdapter by lazy {
        SearchResultAdapter(this@SearchProcess)
    }

    val searchViewModel: SearchViewModel by lazy {
        ViewModelProvider(this@SearchProcess).get(SearchViewModel::class.java)
    }

    val allNotesData = ArrayList<NotesDataStructureSearch>()

    private val noteDatabaseConfigurations: NoteDatabaseConfigurations by lazy {
        NoteDatabaseConfigurations(applicationContext)
    }

    var searchOperationRequested: Boolean = false

    private lateinit var notesRoomDatabaseConfiguration: NotesRoomDatabaseConfiguration

    lateinit var searchProcessLayoutBinding: SearchProcessLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        searchProcessLayoutBinding = SearchProcessLayoutBinding.inflate(layoutInflater)
        setContentView(searchProcessLayoutBinding.root)

        setupSearchViews()

        notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

        searchViewModel.searchPreparedData.observe(this@SearchProcess, Observer {

            allNotesData.addAll(it)

            if (searchOperationRequested) {

                searchOperationRequested = false

                invokeSearchOperations(searchProcessLayoutBinding.searchTerm.text.toString(), allNotesData)

            }

        })

        lifecycleScope.launchWhenCreated {

            searchViewModel.prepareDataForSearch(
                contentEncryption, firebaseUser.uid, notesRoomDatabaseConfiguration.prepareRead().getAllNotesRawData())

        }

        searchProcessLayoutBinding.root.post {

            searchProcessLayoutBinding.recyclerViewSearchResults.layoutManager = GridLayoutManager(
                applicationContext,
                columnCount(applicationContext, 313),
                RecyclerView.VERTICAL,
                false
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

                    searchProcessLayoutBinding.searchActionView.isEnabled = true

                    Handler(Looper.getMainLooper()).postDelayed({

                        searchProcessLayoutBinding.loadingView.pauseAnimation()
                        searchProcessLayoutBinding.loadingView.visibility = View.INVISIBLE

                        searchProcessLayoutBinding.searchActionView.icon = getDrawable(R.drawable.vector_icon_search)

                    }, 2369)

                } else {

                    searchProcessLayoutBinding.loadingView.pauseAnimation()
                    searchProcessLayoutBinding.loadingView.visibility = View.INVISIBLE

                    searchProcessLayoutBinding.searchActionView.isEnabled = true

                    searchProcessLayoutBinding.searchActionView.icon = getDrawable(R.drawable.vector_icon_search)

                    searchResultAdapter.notesDataStructureList.addAll(it)

                    searchResultAdapter.notifyDataSetChanged()

                    searchProcessLayoutBinding.searchTerm.clearFocus()

                    hideKeyboard(applicationContext, searchProcessLayoutBinding.searchTerm)

                }

            })

            searchProcessLayoutBinding.searchTerm.setOnEditorActionListener { textView, actionId, keyEvent ->

                when (actionId) {
                    EditorInfo.IME_ACTION_SEARCH -> {

                        searchProcessLayoutBinding.searchTerm.text?.let { searchTermText ->

                            invokeSearchOperations(searchTermText.toString(), allNotesData)

                        }

                    }
                }

                true
            }

            searchProcessLayoutBinding.searchActionView.setOnClickListener {

                searchProcessLayoutBinding.searchTerm.text?.let { searchTermText ->

                    invokeSearchOperations(searchTermText.toString(), allNotesData)

                }

            }

        }

    }

    override fun onResume() {
        super.onResume()

        setupSearchColors()

    }

    override fun onDestroy() {
        super.onDestroy()

        notesRoomDatabaseConfiguration.closeDatabase()

    }

    override fun onBackPressed() {

        notesRoomDatabaseConfiguration.closeDatabase()

        this@SearchProcess.finish()
        overridePendingTransition(0, R.anim.fade_out)

    }

    private fun invokeSearchOperations(searchTerm: String, allNotesData: List<NotesDataStructureSearch>) {

        if (searchTerm.isNotBlank()
            && allNotesData.size.toLong() == noteDatabaseConfigurations.databaseSize()) {

            searchOperationRequested = true

            searchProcessLayoutBinding.textInputSearchTerm.isErrorEnabled = false

            searchProcessLayoutBinding.recyclerViewSearchResults.removeAllViews()
            searchResultAdapter.notesDataStructureList.clear()

            searchProcessLayoutBinding.searchActionView.isEnabled = false

            searchProcessLayoutBinding.searchActionView.icon = null

            searchProcessLayoutBinding.loadingView.visibility = View.VISIBLE

            searchProcessLayoutBinding.loadingView.setAnimation(R.raw.searching_loading_animation)
            searchProcessLayoutBinding.loadingView.playAnimation()

            searchViewModel.searchInDatabase(searchTerm, allNotesData)

        } else {

            searchProcessLayoutBinding.searchTerm.error = getString(R.string.errorOccurred)

        }

    }

}