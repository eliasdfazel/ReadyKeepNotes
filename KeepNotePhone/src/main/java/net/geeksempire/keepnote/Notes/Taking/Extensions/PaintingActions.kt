package net.geeksempire.keepnote.Notes.Taking.Extensions

import android.animation.Animator
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import android.view.ViewAnimationUtils
import android.view.animation.AccelerateInterpolator
import net.geeksempire.keepnote.Notes.Painting.NewPaintingData
import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.R
import net.geeksempire.keepnote.Utils.UI.Display.displayX
import net.geeksempire.keepnote.Utils.UI.Display.displayY
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

        paintingCanvasView.removeAllPaints()

        true
    }

    takeNoteLayoutBinding.colorPaletteInclude.colorPaletteView.setOnColorChangedListener { pickedColor ->

        paintingCanvasView.changePaintingData(NewPaintingData(paintColor = pickedColor, paintStrokeWidth = paintingCanvasView.newPaintingData.paintStrokeWidth))

        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.iconTint = ColorStateList.valueOf(pickedColor)
        takeNoteLayoutBinding.colorPaletteInclude.pickColorView.rippleColor = ColorStateList.valueOf(pickedColor)

    }

    takeNoteLayoutBinding.colorPaletteInclude.pickColorView.setOnClickListener {

        //Save Picked Color To Shared Preferences
        allColorPalette.invoke()



    }

    takeNoteLayoutBinding.colorPaletteInclude.firstPickedColor.setOnClickListener {



    }

    takeNoteLayoutBinding.colorPaletteInclude.secondPickedColor.setOnClickListener {



    }

    takeNoteLayoutBinding.colorPaletteInclude.thirdPickedColor.setOnClickListener {



    }

}