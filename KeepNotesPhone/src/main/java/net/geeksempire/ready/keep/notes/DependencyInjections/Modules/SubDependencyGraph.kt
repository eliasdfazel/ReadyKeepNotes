/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.DependencyInjections.Modules

import dagger.Module
import net.geeksempire.ready.keep.notes.DependencyInjections.SubComponents.NetworkSubDependencyGraph

@Module(subcomponents = [NetworkSubDependencyGraph::class])
class SubDependencyGraphs