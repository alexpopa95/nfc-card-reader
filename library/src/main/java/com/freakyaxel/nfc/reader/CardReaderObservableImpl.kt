package com.freakyaxel.nfc.reader

import android.app.Activity
import android.content.Context
import android.nfc.NfcAdapter
import android.nfc.Tag
import android.nfc.TagLostException
import android.nfc.tech.IsoDep
import com.freakyaxel.nfc.api.CardReader
import com.freakyaxel.nfc.api.CardReaderEvent
import com.freakyaxel.nfc.api.CardReaderObservable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.filterNotNull

internal class CardReaderObservableImpl constructor(
    private val cardReader: CardReader
) : CardReaderObservable, NfcAdapter.ReaderCallback {

    private var adapter: NfcAdapter? = null

    private val _event = MutableStateFlow<CardReaderEvent?>(null)

    override val event: Flow<CardReaderEvent>
        get() = _event.filterNotNull()

    override fun start(activity: Activity) {
        adapter = NfcAdapter.getDefaultAdapter(activity)?.apply {
            // NFC Supported
            if (!isEnabled) {
                _event.tryEmit(CardReaderEvent.NFCDisabled)
                adapter = null
                return
            }
            if (adapter == null) {
                _event.tryEmit(CardReaderEvent.ReadyToScan)
            }
        } ?: run {
            // NFC Not Supported
            _event.tryEmit(CardReaderEvent.NFCNotSupported)
            null
        }
        if (adapter != null) {
            adapter?.enableReaderMode(
                activity,
                this,
                NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK or
                        NfcAdapter.FLAG_READER_NFC_A or
                        NfcAdapter.FLAG_READER_NFC_B,
                null
            )
        }
    }

    override fun stop(activity: Activity) {
        adapter?.disableReaderMode(activity)
    }

    override fun openNfcSettings(context: Context) {
        cardReader.openNfcSettings(context)
    }

    override fun onTagDiscovered(tag: Tag?) {
        _event.tryEmit(CardReaderEvent.StartReading)
        val isoDep = IsoDep.get(tag)
        val resultEvent = cardReader.getCardResult(isoDep).fold(
            onSuccess = CardReaderEvent::Success,
            onFailure = {
                when (it) {
                    is TagLostException -> CardReaderEvent.CardLost
                    else -> CardReaderEvent.Error(it)
                }
            }
        )
        _event.tryEmit(resultEvent)
    }
}
