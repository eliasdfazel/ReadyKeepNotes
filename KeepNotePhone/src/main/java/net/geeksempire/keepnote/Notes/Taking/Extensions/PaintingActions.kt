package net.geeksempire.keepnote.Notes.Taking.Extensions

import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.view.View
import net.geeksempire.keepnote.Notes.Painting.NewPaintingData
import net.geeksempire.keepnote.Notes.Taking.TakeNote
import net.geeksempire.keepnote.R

fun TakeNote.setupPaintingActions() {

    takeNoteLayoutBinding.paintingToolbarInclude.allColorsPicker.setOnClickListener {

        //Open All Color Picker
        if (takeNoteLayoutBinding.colorPaletteInclude.root.isShown) {

            takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.INVISIBLE

        } else {

            takeNoteLayoutBinding.colorPaletteInclude.root.visibility = View.VISIBLE

        }

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

    }

}