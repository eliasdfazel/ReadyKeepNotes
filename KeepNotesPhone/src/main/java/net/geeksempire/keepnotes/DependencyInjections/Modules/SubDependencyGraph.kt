/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 6/28/20 2:44 PM
 * Last modified 6/28/20 2:01 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.DependencyInjections.Modules

import dagger.Module
import net.geeksempire.keepnote.DependencyInjections.SubComponents.NetworkSubDependencyGraph

@Module(subcomponents = [NetworkSubDependencyGraph::class])
class SubDependencyGraphs