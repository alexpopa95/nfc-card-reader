package com.freakyaxel.nfc.provider

import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.parser.IProvider

internal class IProviderImpl(private val tag: IsoDep) : IProvider {
    override fun transceive(command: ByteArray): ByteArray? = tag.transceive(command)

    override fun getAt(): ByteArray? = with(tag) {
        historicalBytes // Extract ATS from NFC-A
            ?: hiLayerResponse // Extract ATS from NFC-B
    }
}
