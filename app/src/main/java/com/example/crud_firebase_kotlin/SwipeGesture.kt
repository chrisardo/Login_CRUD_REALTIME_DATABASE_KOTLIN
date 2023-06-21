package com.example.crud_firebase_kotlin

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

open class SwipeGesture(
    private val buttonWidth: Int
) : ItemTouchHelper.Callback() {

    private val background = ColorDrawable(Color.RED)
    private val buttonText = "Eliminar"
    private val buttonPaint = Paint().apply {
        color = Color.WHITE
        textSize = 32f
        textAlign = Paint.Align.CENTER
    }

    override fun getMovementFlags(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder
    ): Int {
        return makeMovementFlags(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT)
    }

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val position = viewHolder.adapterPosition

       //onSwipeCallback.invoke(position)
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
        super.onChildDraw(canvas, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

        val itemView = viewHolder.itemView
        val itemHeight = itemView.bottom - itemView.top

        // Dibujar el fondo rojo
        background.setBounds(
            itemView.left,
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(canvas)

        // Dibujar el botón a la izquierda
        if (dX < 0) {
            val buttonLeft = itemView.right - buttonWidth
            val buttonTop = itemView.top + (itemHeight - buttonWidth) / 2
            val buttonRight = itemView.right
            val buttonBottom = buttonTop + buttonWidth

            canvas.drawRect(
                buttonLeft.toFloat(),
                buttonTop.toFloat(),
                buttonRight.toFloat(),
                buttonBottom.toFloat(),
                buttonPaint
            )
            val textOffset =
                (buttonBottom - buttonTop) / 2f - (buttonPaint.descent() + buttonPaint.ascent()) / 2f
            canvas.drawText(
                buttonText,
                (buttonLeft + buttonRight) / 2f,
                buttonTop + textOffset,
                buttonPaint
            )
        }

        // Dibujar el botón a la derecha
        if (dX > 0) {
            val buttonLeft = itemView.left
            val buttonTop = itemView.top + (itemHeight - buttonWidth) / 2
            val buttonRight = itemView.left + buttonWidth
            val buttonBottom = buttonTop + buttonWidth

            canvas.drawRect(
                buttonLeft.toFloat(),
                buttonTop.toFloat(),
                buttonRight.toFloat(),
                buttonBottom.toFloat(),
                buttonPaint
            )
            val textOffset =
                (buttonBottom - buttonTop) / 2f - (buttonPaint.descent() + buttonPaint.ascent()) / 2f
            canvas.drawText(
                buttonText,
                (buttonLeft + buttonRight) / 2f,
                buttonTop + textOffset,
                buttonPaint
            )
        }
    }
}