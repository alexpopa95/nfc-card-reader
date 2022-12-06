package com.freakyaxel.nfc.reader

import android.content.Context
import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.freakyaxel.nfc.api.CardReader
import com.freakyaxel.nfc.card.CardData
import com.freakyaxel.nfc.card.CardState
import com.freakyaxel.nfc.intent.NfcIntentProvider
import com.freakyaxel.nfc.provider.TransceiverProvider
import com.freakyaxel.nfc.toCardData

internal class CardReaderImpl constructor(
    private val builder: EmvTemplate.Builder,
    private val config: EmvTemplate.Config,
    private val provider: TransceiverProvider,
    private val intentProvider: NfcIntentProvider
) : CardReader {

    companion object {
        val EMPTY_CARD = CardData(
            aids = emptyList(),
            types = emptyList(),
            expireDate = null,
            number = null,
            state = CardState.UNKNOWN
        )
    }

    override fun getCard(isoDep: IsoDep): CardData {
        return getCardResult(isoDep).getOrElse { EMPTY_CARD }
    }

    override fun getCardResult(isoDep: IsoDep): Result<CardData> {
        return runCatching {
            isoDep.connect()
            builder.setConfig(config)
                .setProvider(provider.getTransceiver(isoDep))
                .build()
                .readEmvCard()
                .toCardData()
        }.also {
            runCatching {
                isoDep.close()
            }
        }
    }

    override fun openNfcSettings(context: Context) {
        context.startActivity(intentProvider.settings())
    }
}
