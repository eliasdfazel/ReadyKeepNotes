package net.geeksempire.keepnote

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import net.geeksempire.keepnote.databinding.EntryConfigurationLayoutBinding

class EntryConfigurations : AppCompatActivity() {

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)



    }

}