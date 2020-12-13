/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/5/20 3:46 AM
 * Last modified 8/5/20 3:46 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnote.DependencyInjections.SubComponents

import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import dagger.BindsInstance
import dagger.Subcomponent
import net.geeksempire.keepnote.DependencyInjections.Modules.Network.NetworkConnectionModule
import net.geeksempire.keepnote.DependencyInjections.Scopes.ActivityScope
import net.geeksempire.keepnote.Notes.Taking.TakeNote

@ActivityScope
@Subcomponent(modules = [NetworkConnectionModule::class])
interface NetworkSubDependencyGraph {

    @Subcomponent.Factory
    interface Factory {
        fun create(@BindsInstance appCompatActivity: AppCompatActivity, @BindsInstance constraintLayout: ConstraintLayout): NetworkSubDependencyGraph
    }

    fun inject(takeNote: TakeNote)

}