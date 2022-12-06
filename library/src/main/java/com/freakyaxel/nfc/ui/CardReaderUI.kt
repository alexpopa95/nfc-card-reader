package com.freakyaxel.nfc.ui

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.freakyaxel.nfc.api.CardReaderEvent
import com.freakyaxel.nfc.R

// TODO: XML Layout
class CardReaderUI : ConstraintLayout {
    private lateinit var text: TextView

    constructor(context: Context) : super(context) {
        View.inflate(context, R.layout.card_reader_ui, this)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        View.inflate(context, R.layout.card_reader_ui, this)
    }

    override fun onFinishInflate() {
        text = rootView.findViewById(R.id.readerText)
        super.onFinishInflate()
    }

    fun update(readerEvent: CardReaderEvent) {
        text.text = readerEvent.toString()
    }
}