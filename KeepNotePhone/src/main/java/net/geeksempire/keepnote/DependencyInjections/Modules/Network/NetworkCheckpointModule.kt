/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 6/28/20 2:44 PM
 * Last modified 6/28/20 2:22 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnote.DependencyInjections.Modules.Network

import dagger.Binds
import dagger.Module
import net.geeksempire.keepnote.Utils.Network.InterfaceNetworkCheckpoint
import net.geeksempire.keepnote.Utils.Network.NetworkCheckpoint

@Module
abstract class NetworkCheckpointModule {

    @Binds
    abstract fun provideNetworkCheckpoint(networkCheckpoint: NetworkCheckpoint/*This is Instance Of Return Type*/): InterfaceNetworkCheckpoint
}