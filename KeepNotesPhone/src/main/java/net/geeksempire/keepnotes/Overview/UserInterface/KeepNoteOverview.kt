package net.geeksempire.keepnotes.Overview.UserInterface

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.keepnote.databinding.OverviewLayoutBinding

class KeepNoteOverview : AppCompatActivity() {

    lateinit var overviewLayoutBinding: OverviewLayoutBinding

    //show list of saved notes
    // and have an option to take note

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overviewLayoutBinding = OverviewLayoutBinding.inflate(layoutInflater)
        setContentView(overviewLayoutBinding.root)



    }

}