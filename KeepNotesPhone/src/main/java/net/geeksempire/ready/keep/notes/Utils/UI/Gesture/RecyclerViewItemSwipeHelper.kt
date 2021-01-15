package net.geeksempire.ready.keep.notes.Utils.UI.Gesture

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.view.MotionEvent
import android.view.View
import androidx.annotation.ColorRes
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R
import net.geeksempire.ready.keep.notes.Utils.UI.Display.DpToInteger
import java.util.*
import kotlin.math.abs
import kotlin.math.max

interface SwipeActions {
    /**
     * Open Menu
     **/
    fun onSwipeToStart(context: KeepNoteOverview, position: Int) = CoroutineScope(Dispatchers.Main).launch {}
    /**
     * Delete
     **/
    fun onSwipeToEnd(context: KeepNoteOverview, position: Int) = CoroutineScope(Dispatchers.Main).launch {}
}

abstract class RecyclerViewItemSwipeHelper(private val context: KeepNoteOverview, private val swipeActions: SwipeActions) : ItemTouchHelper.SimpleCallback(
    ItemTouchHelper.UP
            or ItemTouchHelper.DOWN,
    ItemTouchHelper.START
            or ItemTouchHelper.END
) {

    private val selectedItemBackground = ContextCompat.getDrawable(context, R.drawable.round_corner_background)?.mutate()

    private var swipedPosition = -1

    private val buttonsBuffer: MutableMap<Int, List<UnderlayButton>> = mutableMapOf()

    private val recoverQueue = object : LinkedList<Int>() {

        override fun add(element: Int): Boolean {

            if (contains(element)) return false

            return super.add(element)
        }

    }

    @SuppressLint("ClickableViewAccessibility")
    private val touchListener = View.OnTouchListener { _, event ->
        if (swipedPosition < 0) return@OnTouchListener false
        buttonsBuffer[swipedPosition]?.forEach { it.handle(event) }
        recoverQueue.add(swipedPosition)
        swipedPosition = -1
        recoverSwipedItem()
        true
    }

    init {
        context.overviewLayoutBinding.overviewRecyclerView.setOnTouchListener(touchListener)
    }

    private fun recoverSwipedItem() {
        while (!recoverQueue.isEmpty()) {
            val position = recoverQueue.poll() ?: return
            context.overviewLayoutBinding.overviewRecyclerView.adapter?.notifyItemChanged(position)
        }
    }

    private fun drawButtons(
        canvas: Canvas,
        buttons: List<UnderlayButton>,
        itemView: View,
        dX: Float
    ) {
        var right = itemView.right
        buttons.forEach { button ->
            val width = button.intrinsicWidth / buttons.intrinsicWidth() * abs(dX)
            val left = right - width
            button.draw(
                canvas,
                RectF(left, itemView.top.toFloat(), right.toFloat(), itemView.bottom.toFloat())
            )

            right = left.toInt()
        }
    }

    override fun onChildDraw(
        canvas: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val position = viewHolder.adapterPosition
        var maxDX = dX
        val itemView = viewHolder.itemView

        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
            if (dX < 0) {
                if (!buttonsBuffer.containsKey(position)) {
                    buttonsBuffer[position] = instantiateUnderlayButton(position)
                }

                val buttons = buttonsBuffer[position] ?: return
                if (buttons.isEmpty()) return
                maxDX = max(-buttons.intrinsicWidth(), dX)
                drawButtons(canvas, buttons, itemView, maxDX)
            }
        }

        super.onChildDraw(
            canvas,
            recyclerView,
            viewHolder,
            maxDX,
            dY,
            actionState,
            isCurrentlyActive
        )

        if (dX > 0) {

            selectedItemBackground?.let {

                selectedItemBackground.setTint(context.getColor(R.color.default_color_game_light_transparent))
                selectedItemBackground.setBounds(
                    itemView.left,
                    itemView.top,
                    itemView.left + dX.toInt(),
                    itemView.bottom
                )

                val rect = RectF(itemView.left.toFloat(), itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                val titleBounds = Rect()

                val paint = Paint()

                canvas.drawRoundRect(rect, DpToInteger(context, 5).toFloat(), DpToInteger(context, 5).toFloat(), paint)

                paint.color = ContextCompat.getColor(context, R.color.lighter)
                paint.textSize = 49f
                paint.typeface = ResourcesCompat.getFont(context, R.font.houston_regular)
                paint.textAlign = Paint.Align.LEFT

                paint.getTextBounds(context.getString(R.string.deletedText), 0, context.getString(R.string.deletedText).length, titleBounds)

                val x = (canvas.width / 2f) - titleBounds.centerX()
                val y = rect.top + rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom

                canvas.drawText(context.getString(R.string.deletedText), x.toFloat(), y, paint)

            }

        } else if (dX < 0) {

            selectedItemBackground?.let {

                selectedItemBackground.setTint(context.getColor(R.color.default_color_light_transparent))
                selectedItemBackground.setBounds(
                    itemView.right + dX.toInt(),
                    itemView.top,
                    itemView.right,
                    itemView.bottom
                )

            }

        } else {

            selectedItemBackground?.let {

                selectedItemBackground.setTint(context.getColor(R.color.light_transparent_high))
                selectedItemBackground.setBounds(0, 0, 0, 0)

            }

        }

        selectedItemBackground?.draw(canvas)
        
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {

        return true
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {

        val position = viewHolder.adapterPosition

        if (swipedPosition != position) recoverQueue.add(swipedPosition)
        swipedPosition = position

        recoverSwipedItem()

        when (direction) {
            ItemTouchHelper.START -> {

                swipeActions.onSwipeToStart(context, viewHolder.adapterPosition)

            }
            ItemTouchHelper.END -> {

                swipeActions.onSwipeToEnd(context, viewHolder.adapterPosition)

            }
        }

    }

    abstract fun instantiateUnderlayButton(position: Int): List<UnderlayButton>

    interface UnderlayOptionsActions {
        fun onClick() = CoroutineScope(Dispatchers.Main).launch {  }
    }

    class UnderlayButton(
        private val context: Context,
        private val title: String,
        textSize: Float,
        @ColorRes private val colorRes: Int,
        private val clickListener: UnderlayOptionsActions
    ) {
        private var clickableRegion: RectF? = null
        private val textSizeInPixel: Float = textSize * context.resources.displayMetrics.density
        private val horizontalPadding = 50.0f
        val intrinsicWidth: Float

        init {
            val paint = Paint()
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.MONOSPACE
            paint.textAlign = Paint.Align.LEFT
            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)
            intrinsicWidth = titleBounds.width() + 2 * horizontalPadding
        }

        fun draw(canvas: Canvas, rect: RectF) {
            val paint = Paint()

            paint.color = ContextCompat.getColor(context, colorRes)
            canvas.drawRoundRect(rect, DpToInteger(context, 5).toFloat(), DpToInteger(context, 5).toFloat(), paint)

            paint.color = ContextCompat.getColor(context, R.color.lighter)
            paint.textSize = textSizeInPixel
            paint.typeface = Typeface.MONOSPACE
            paint.textAlign = Paint.Align.LEFT

            val titleBounds = Rect()
            paint.getTextBounds(title, 0, title.length, titleBounds)

            val y = rect.height() / 2 + titleBounds.height() / 2 - titleBounds.bottom
            canvas.drawText(title, rect.left + horizontalPadding, rect.top + y, paint)

            clickableRegion = rect
        }

        fun handle(event: MotionEvent) {

            clickableRegion?.let {
                if (it.contains(event.x, event.y)) {

                    clickListener.onClick()

                }

            }

        }
    }

}

private fun List<RecyclerViewItemSwipeHelper.UnderlayButton>.intrinsicWidth(): Float {

    if (isEmpty()) {
        return 0.0f
    }

    return map { it.intrinsicWidth }.reduce { acc, fl -> acc + fl }
}