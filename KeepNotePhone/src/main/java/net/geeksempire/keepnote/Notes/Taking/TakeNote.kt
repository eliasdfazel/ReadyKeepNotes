package net.geeksempire.keepnote.Notes.Taking

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.keepnote.databinding.TakeNoteLayoutBinding

class TakeNote : AppCompatActivity() {

    lateinit var takeNoteLayoutBinding: TakeNoteLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        takeNoteLayoutBinding = TakeNoteLayoutBinding.inflate(layoutInflater)
        setContentView(takeNoteLayoutBinding.root)


    }

}