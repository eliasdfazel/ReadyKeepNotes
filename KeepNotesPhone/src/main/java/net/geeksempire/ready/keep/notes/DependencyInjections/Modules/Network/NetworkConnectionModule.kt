/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.DependencyInjections.Modules.Network

import android.net.ConnectivityManager
import dagger.Binds
import dagger.Module
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener

@Module
abstract class NetworkConnectionModule {

    @Binds
    abstract fun provideNetworkConnectionListener(networkConnectionListener: NetworkConnectionListener/*This is Instance Of Return Type*/): ConnectivityManager.NetworkCallback
}