package net.geeksempire.ready.keep.notes.Notes.Taking.Extensions

import android.animation.Animator
import android.annotation.SuppressLint
import android.content.res.ColorStateList
import android.util.Log
import android.view.MotionEvent
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
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.changePaintingData
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.redoProcess
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.removeAllPaints
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.Extensions.undoProcess
import net.geeksempire.ready.keep.notes.Notes.Tools.Painting.NewPaintingData
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayX
import net.geeksempire.ready.keep.notes.Utils.UI.Display.displayY
import kotlin.math.hypot

@SuppressLint("ClickableViewAccessibility")
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

    takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.setOnTouchListener { view, motionEvent ->

        when (motionEvent.action) {
            MotionEvent.ACTION_DOWN -> {

                when (motionEvent.getToolType(0)) {
                    MotionEvent.TOOL_TYPE_FINGER -> {
                        Log.d(this@setupPaintingActions.javaClass.simpleName, "Finger Touch")

                        inputRecognizer.stylusDetected = false

                    }
                    MotionEvent.TOOL_TYPE_STYLUS -> {
                        Log.d(this@setupPaintingActions.javaClass.simpleName, "Stylus Touch")

                        inputRecognizer.stylusDetected = true

                    }
                }

            }
            MotionEvent.ACTION_UP -> {

                if (inputRecognizer.stylusDetected) {

                    paintingCanvasView.newPaintingData = paintingCanvasView.stylusPaintingData

                    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = paintingCanvasView.stylusPaintingData.paintStrokeSliderPosition

                    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = paintingCanvasView.stylusPaintingData.paintColor
                    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(paintingCanvasView.stylusPaintingData.paintColor)

                } else {

                    paintingCanvasView.newPaintingData = paintingCanvasView.fingerPaintingData

                    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = paintingCanvasView.fingerPaintingData.paintStrokeSliderPosition

                    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = paintingCanvasView.fingerPaintingData.paintColor
                    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(paintingCanvasView.fingerPaintingData.paintColor)

                }

                allColorPalette.invoke()

            }
            MotionEvent.ACTION_CANCEL -> {

                if (inputRecognizer.stylusDetected) {

                    paintingCanvasView.newPaintingData = paintingCanvasView.stylusPaintingData

                    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = paintingCanvasView.stylusPaintingData.paintStrokeSliderPosition

                    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = paintingCanvasView.stylusPaintingData.paintColor
                    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(paintingCanvasView.stylusPaintingData.paintColor)

                } else {

                    paintingCanvasView.newPaintingData = paintingCanvasView.fingerPaintingData

                    takeNoteLayoutBinding.colorPaletteInclude.strokeWidthFluidSlider.position = paintingCanvasView.fingerPaintingData.paintStrokeSliderPosition

                    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = paintingCanvasView.fingerPaintingData.paintColor
                    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(paintingCanvasView.fingerPaintingData.paintColor)

                }

                allColorPalette.invoke()

            }
        }

        true
    }

    takeNoteLayoutBinding.paintingToolbarInclude.undoPaint.setOnClickListener {

        paintingCanvasView.undoProcess()

    }

    takeNoteLayoutBinding.paintingToolbarInclude.redoPaint.setOnClickListener {

        paintingCanvasView.redoProcess()

    }

    takeNoteLayoutBinding.paintingToolbarInclude.clearAllPaint.setOnClickListener {

        doVibrate(applicationContext, 179)

        paintingCanvasView.removeAllPaints()

    }

    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color = getColor(R.color.default_color_bright)
    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(getColor(R.color.default_color_bright))

    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.setOnColorChangedListener { pickedColor ->

        if (inputRecognizer.stylusDetected) {

            paintingCanvasView.changePaintingData(NewPaintingData(paintColor = pickedColor,
                paintStrokeWidth = paintingCanvasView.stylusPaintingData.paintStrokeWidth,
                paintStrokeSliderPosition = paintingCanvasView.stylusPaintingData.paintStrokeSliderPosition))

        } else {

            paintingCanvasView.changePaintingData(NewPaintingData(paintColor = pickedColor,
                paintStrokeWidth = paintingCanvasView.fingerPaintingData.paintStrokeWidth,
                paintStrokeSliderPosition = paintingCanvasView.fingerPaintingData.paintStrokeSliderPosition))

        }

        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(pickedColor)
        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.rippleColor = ColorStateList.valueOf(pickedColor)

        strokePaintingCanvasView.changePaintingData(NewPaintingData(paintColor = pickedColor, paintStrokeWidth = paintingCanvasView.newPaintingData.paintStrokeWidth))

        strokePaintingCanvasView.removeAllPaints()
    }

    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.setOnClickListener {

        allColorPalette.invoke()

        paintingIO.saveRecentPickedColor(takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color)

        recentColorsAdapter.allPickedColors.add(takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.color)
        recentColorsAdapter.notifyDataSetChanged()

    }

    takeNoteLayoutBinding.colorPaletteInclude.blurView.setOnClickListener {

        allColorPalette.invoke()

    }

    paintingActionsStrokeWidthSample()

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