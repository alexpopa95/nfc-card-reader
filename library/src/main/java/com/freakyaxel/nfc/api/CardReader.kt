package com.freakyaxel.nfc.api

import android.content.Context
import android.nfc.tech.IsoDep
import com.freakyaxel.nfc.card.CardData
import com.freakyaxel.nfc.intent.NfcIntentProvider
import com.freakyaxel.nfc.provider.TransceiverProvider
import com.freakyaxel.nfc.reader.CardReaderImpl
import com.github.devnied.emvnfccard.parser.EmvTemplate

interface CardReader {
    fun getCard(isoDep: IsoDep): CardData
    fun getCardResult(isoDep: IsoDep): Result<CardData>

    fun openNfcSettings(context: Context)

    companion object {
        fun newInstance(): CardReader {
            val config = EmvTemplate.Config()
                .setContactLess(true) // Enable contact less reading
                .setReadAllAids(false) // Read all aids in card
                .setReadTransactions(false) // Read all transactions
                .setRemoveDefaultParsers(false) // Remove default parsers (GeldKarte and Emv)
                .setReadAt(true) //  To extract ATS or ATR
                .setReadCplc(false) // To read CPLC data. Not for contactless cards.
            return CardReaderImpl(
                config = config,
                builder = EmvTemplate.Builder(),
                provider = TransceiverProvider(),
                intentProvider = NfcIntentProvider()
            )
        }
    }
}
