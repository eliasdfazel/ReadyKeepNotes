/*
 * Copyright © 2020 By Geeks Empire.
 *
 * Created by Elias Fazel on 8/9/20 10:52 PM
 * Last modified 8/9/20 10:52 PM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.InApplicationReview

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.testing.FakeReviewManager
import net.geeksempire.ready.keep.notes.BuildConfig
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.Data.LastUpdateInformation

class InApplicationReviewProcess (private val context: AppCompatActivity) {

    private val reviewManager = ReviewManagerFactory.create(context)

    private val fakeReviewManager = FakeReviewManager(context)

    fun start(forceReviewFlow: Boolean) {

        val lastUpdateInformation = LastUpdateInformation(context)

        if (lastUpdateInformation.isApplicationUpdated() || forceReviewFlow) {

            val requestReviewFlow = if (BuildConfig.DEBUG) {
                Log.d(this@InApplicationReviewProcess.javaClass.simpleName, "Fake Review Flow Invoked")

                fakeReviewManager.requestReviewFlow()

            } else {
                Log.d(this@InApplicationReviewProcess.javaClass.simpleName, "Real Review Flow Invoked")

                reviewManager.requestReviewFlow()

            }

            requestReviewFlow.addOnSuccessListener { reviewInfo ->

                val reviewFlow = if (BuildConfig.DEBUG) {
                    fakeReviewManager.launchReviewFlow(context, reviewInfo)
                } else {
                    reviewManager.launchReviewFlow(context, reviewInfo)
                }

                reviewFlow.addOnSuccessListener {
                    Log.d(this@InApplicationReviewProcess.javaClass.simpleName, "Real Review Flow Showed Successfully")

                }.addOnFailureListener {

                    Intent().apply {
                        action = Intent.ACTION_VIEW
                        data = Uri.parse(context.getString(R.string.playStoreLink))
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                        context.startActivity(this@apply)
                    }

                }

            }.addOnFailureListener {
                Log.d(this@InApplicationReviewProcess.javaClass.simpleName, "In Application Review Process Error ${it.printStackTrace().toString()}")

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