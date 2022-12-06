package com.freakyaxel.nfc.api

import android.app.Activity
import android.content.Context
import com.freakyaxel.nfc.reader.CardReaderObservableImpl
import kotlinx.coroutines.flow.Flow

interface CardReaderObservable {
    val event: Flow<CardReaderEvent>

    fun start(activity: Activity)
    fun stop(activity: Activity)

    fun openNfcSettings(context: Context)

    companion object {
        fun newInstance(cardReader: CardReader): CardReaderObservable {
            return CardReaderObservableImpl(cardReader)
        }
    }
}
