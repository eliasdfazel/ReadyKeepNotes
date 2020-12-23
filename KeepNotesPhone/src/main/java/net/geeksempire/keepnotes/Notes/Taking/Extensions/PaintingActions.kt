package net.geeksempire.keepnotes.Notes.Taking.Extensions

import android.animation.Animator
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.text.Html
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abanabsalan.aban.magazine.Utils.System.doVibrate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import net.geeksempire.keepnotes.Notes.Taking.TakeNote
import net.geeksempire.keepnotes.Notes.Tools.Painting.NewPaintingData
import net.geeksempire.keepnotes.R
import net.geeksempire.keepnotes.Utils.UI.Display.displayX
import net.geeksempire.keepnotes.Utils.UI.Display.displayY
import java.math.RoundingMode
import java.text.DecimalFormat
import kotlin.math.hypot

fun TakeNote.setupPaintingActions() {

    val allColorPalette = {

        if (takeNoteLayoutBinding.colorPaletteInclude.root.isShown) {

            val finalRadius = hypot(displayX(applicationContext).toDouble(), displayY(applicationContext).toDouble())

            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(takeNoteLayoutBinding.colorPaletteInclude.root,
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.x.toInt()),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.y.toInt() - (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height)),
                finalRadius.toFloat(),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height.toFloat() / 2))

            circularReveal.duration = 555
            circularReveal.interpolator = AccelerateInterpolator()

            circularReveal.start()
            circularReveal.addListener(object : Animator.AnimatorListener {

                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {

                    takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.INVISIBLE

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })

        } else {

            val finalRadius = hypot(displayX(applicationContext).toDouble(), displayY(applicationContext).toDouble())

            val circularReveal: Animator = ViewAnimationUtils.createCircularReveal(takeNoteLayoutBinding.colorPaletteInclude.root,
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.x.toInt()),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.y.toInt() - (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height)),
                (takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.height.toFloat() / 2),
                finalRadius.toFloat())

            circularReveal.duration = 999
            circularReveal.interpolator = AccelerateInterpolator()

            takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.VISIBLE

            circularReveal.start()
            circularReveal.addListener(object : Animator.AnimatorListener {

                override fun onAnimationRepeat(animation: Animator?) {

                }

                override fun onAnimationEnd(animation: Animator?) {

                }

                override fun onAnimationCancel(animation: Animator?) {

                }

                override fun onAnimationStart(animation: Animator?) {

                }

            })

        }

    }

    takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.setOnClickListener {

        allColorPalette.invoke()

    }

    takeNoteLayoutBinding.paintingToolbarInclude.undoPaint.setOnClickListener {

        paintingCanvasView.undoProcess()

    }

    takeNoteLayoutBinding.paintingToolbarInclude.redoPaint.setOnClickListener {

        paintingCanvasView.redoProcess()

    }

    takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.setOnClickListener {

        if (takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.imageTintList == ColorStateList.valueOf(getColor(R.color.red_transparent))) {

            paintingCanvasView.disableClearing()

            takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.imageTintList = null

        } else {

            paintingCanvasView.enableClearing()

            takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.imageTintMode = PorterDuff.Mode.SRC_ATOP
            takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.imageTintList = ColorStateList.valueOf(getColor(R.color.red_transparent))

        }

    }

    takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.setOnLongClickListener {
        doVibrate(applicationContext, 179)

        paintingCanvasView.removeAllPaints()

        true
    }

    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = getColor(R.color.default_color_bright)
    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(getColor(R.color.default_color_bright))

    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.setOnColorChangedListener { pickedColor ->

        paintingCanvasView.changePaintingData(NewPaintingData(paintColor = pickedColor, paintStrokeWidth = paintingCanvasView.newPaintingData.paintStrokeWidth))

        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(pickedColor)
        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.rippleColor = ColorStateList.valueOf(pickedColor)

    }

    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.setOnClickListener {

        allColorPalette.invoke()

        paintingIO.saveRecentPickedColor(takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color)

        recentColorsAdapter.allPickedColors.add(takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color)
        recentColorsAdapter.notifyDataSetChanged()

    }

    /* Stroke Width Changer */
    val fluidSliderMinimum = 3.0.toFloat()
    val fluidSlideMaximum = 33.0.toFloat()
    val totalFluidSliderAmount = (fluidSlideMaximum - fluidSliderMinimum)

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.positionListener = { fluidSliderPosition ->

        val decimalFormat = DecimalFormat("#.##")
        decimalFormat.roundingMode = RoundingMode.HALF_EVEN

        val selectedStrokeWidth = decimalFormat.format((fluidSliderMinimum + (totalFluidSliderAmount  * fluidSliderPosition))).toFloat()

        takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.bubbleText = Html.fromHtml("<small>${selectedStrokeWidth.toString()}</small>", Html.FROM_HTML_MODE_COMPACT).toString()

        paintingCanvasView.changePaintingPathStrokeWidth(NewPaintingData(paintColor = paintingCanvasView.newPaintingData.paintColor, paintStrokeWidth = 55.0f))

    }

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = 0f

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.startText ="$fluidSliderMinimum"
    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.endText = "$fluidSlideMaximum"

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.beginTrackingListener = {



    }

    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.endTrackingListener = {



    }
    /* Stroke Width Changer */

    setupRecentColors(allColorPalette)

}

fun TakeNote.setupRecentColors(allColorPalette: () -> Unit) = CoroutineScope(Dispatchers.IO).launch {

    recentColorsAdapter.allPickedColors.clear()
    recentColorsAdapter.allPickedColors.addAll(paintingIO.readRecentPickedColor())

    recentColorsAdapter.allColorPalette = allColorPalette

    withContext(Dispatchers.Main) {

        takeNoteLayoutBinding.colorPaletteInclude.recentColorsList.layoutManager = LinearLayoutManager(applicationContext, RecyclerView.HORIZONTAL, false)

        takeNoteLayoutBinding.colorPaletteInclude.recentColorsList.adapter = recentColorsAdapter

    }

}