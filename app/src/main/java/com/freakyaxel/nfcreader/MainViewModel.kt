package com.freakyaxel.nfcreader

import androidx.lifecycle.ViewModel
import com.freakyaxel.nfc.api.CardReader
import com.freakyaxel.nfc.api.CardReaderObservable

class MainViewModel : ViewModel() {
    val cardReader = CardReader.newInstance()
    val cardReaderObservable = CardReaderObservable.newInstance(cardReader)
}