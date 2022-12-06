package com.freakyaxel.nfc.provider

import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.parser.IProvider

internal class TransceiverProvider {
    fun getTransceiver(isoDep: IsoDep): IProvider = IProviderImpl(isoDep)
}
