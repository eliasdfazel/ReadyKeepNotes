/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/9/20 10:52 PM
 * Last modified 8/9/20 10:52 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.keepnotes.Utils.InApplicationReview

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import net.geeksempire.keepnotes.BuildConfig
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.Data.LastUpdateInformation

class InApplicationReviewProcess (private val context: AppCompatActivity) {

    private val reviewManager = ReviewManagerFactory.create(context)

    private val fakeReviewManager = FakeReviewManager(context)

    fun start(forceReviewFlow: Boolean) {

        val lastUpdateInformation = LastUpdateInformation(context)

        if (lastUpdateInformation.isApplicationUpdated() || forceReviewFlow) {

            val requestReviewFlow = if (BuildConfig.DEBUG) {
                fakeReviewManager.requestReviewFlow()
            } else {
                reviewManager.requestReviewFlow()
            }

            requestReviewFlow.addOnSuccessListener { reviewInfo ->

                val reviewFlow = if (BuildConfig.DEBUG) {
                    fakeReviewManager.launchReviewFlow(context, reviewInfo)
                } else {
                    reviewManager.launchReviewFlow(context, reviewInfo)
                }

                reviewFlow.addOnSuccessListener {

                }.addOnFailureListener {

                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(context.getString(R.string.playStoreLink))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(this@apply)
                    }

                }

            }.addOnFailureListener {
                Log.d(this@InApplicationReviewProcess.javaClass.simpleName, "In Application Review Process Error")

                Intent().apply {
                    action = Intent.ACTION_VIEW
                    data = Uri.parse(context.getString(R.string.playStoreLink))
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(this@apply)
                }

            }

        } else {

        }

    }

}