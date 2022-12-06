package com.freakyaxel.nfc.api

import com.freakyaxel.nfc.card.CardData

sealed class CardReaderEvent {
    object ReadyToScan : CardReaderEvent()
    object StartReading : CardReaderEvent()
    object CardLost : CardReaderEvent()
    object NFCDisabled : CardReaderEvent()
    object NFCNotSupported : CardReaderEvent()
    data class Error(val throwable: Throwable) : CardReaderEvent()
    data class Success(val card: CardData) : CardReaderEvent()
}
