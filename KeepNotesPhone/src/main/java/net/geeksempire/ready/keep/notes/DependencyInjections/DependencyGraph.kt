/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 6/28/20 2:44 PM
 * Last modified 6/28/20 1:53 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.DependencyInjections

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import net.geeksempire.ready.keep.notes.DependencyInjections.Modules.Network.NetworkCheckpointModule
import net.geeksempire.ready.keep.notes.DependencyInjections.Modules.SubDependencyGraphs
import net.geeksempire.ready.keep.notes.DependencyInjections.SubComponents.NetworkSubDependencyGraph
import javax.inject.Singleton

@Singleton
@Component (modules = [NetworkCheckpointModule::class, SubDependencyGraphs::class])
interface DependencyGraph {

    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): DependencyGraph
    }

    fun subDependencyGraph(): NetworkSubDependencyGraph.Factory

//    fun inject(entryConfiguration: EntryConfigurations)

}
