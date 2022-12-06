package com.freakyaxel.nfc.reader

import android.content.Context
import android.content.Intent
import android.nfc.tech.IsoDep
import com.github.devnied.emvnfccard.model.EmvCard
import com.github.devnied.emvnfccard.model.enums.CardStateEnum
import com.github.devnied.emvnfccard.parser.EmvTemplate
import com.github.devnied.emvnfccard.parser.IProvider
import com.freakyaxel.nfc.card.CardData
import com.freakyaxel.nfc.card.CardState
import com.freakyaxel.nfc.intent.NfcIntentProvider
import com.freakyaxel.nfc.provider.TransceiverProvider
import com.freakyaxel.nfc.reader.CardReaderImpl.Companion.EMPTY_CARD
import com.freakyaxel.nfc.toCardData
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

internal class CardReaderImplTest {

    private val emvCard = EmvCard().apply {
        state = CardStateEnum.LOCKED
    }

    private val transciever: IProvider = mockk()

    private val isoDep: IsoDep = mockk(relaxed = true)

    private val emvTemplate: EmvTemplate = mockk {
        every { readEmvCard() } returns emvCard
    }

    private val config: EmvTemplate.Config = mockk()
    private val builder: EmvTemplate.Builder = mockk {
        every { setConfig(any()) } returns this
        every { setProvider(any()) } returns this
        every { build() } returns emvTemplate
    }
    private val provider: TransceiverProvider = mockk {
        every { getTransceiver(isoDep) } returns transciever
    }

    private val intentProvider: NfcIntentProvider = mockk {
        every { settings() } returns mockk {
            every { action } returns "android.settings.NFC_SETTINGS"
        }
    }

    private fun getSut() = CardReaderImpl(
        builder = builder,
        config = config,
        provider = provider,
        intentProvider = intentProvider
    )

    private val expectedCard = CardData(
        aids = emptyList(),
        types = emptyList(),
        expireDate = null,
        number = null,
        state = CardState.LOCKED
    )

    @Test
    fun `read card success`() {
        val sut = getSut()
        val result = sut.getCardResult(isoDep)

        assertTrue(result.isSuccess)
        assertEquals(expectedCard.state, result.getOrNull()?.state)

        verifyOrder {
            isoDep.connect()
            builder.setConfig(config)
            provider.getTransceiver(isoDep)
            builder.setProvider(transciever)
            builder.build()
            emvTemplate.readEmvCard()
            emvCard.toCardData()
            isoDep.close()
        }
    }

    @Test
    fun `read card error`() {
        every { builder.build() } throws Exception("Broken")

        val sut = getSut()
        val result = sut.getCardResult(isoDep)

        assertTrue(result.isFailure)
        assertEquals("Broken", result.exceptionOrNull()!!.message)

        verifyOrder {
            isoDep.connect()
            builder.setConfig(config)
            provider.getTransceiver(isoDep)
            builder.setProvider(transciever)
            builder.build()
            isoDep.close()
        }
        verify(exactly = 0) {
            emvTemplate.readEmvCard()
            emvCard.toCardData()
        }
    }

    @Test
    fun `read card error - non null get`() {
        every { builder.build() } throws Exception("Broken")

        val sut = getSut()
        val result = sut.getCard(isoDep)

        assertEquals(EMPTY_CARD, result)
    }

    @Test
    fun `read card success - non null get`() {
        val sut = getSut()
        val result = sut.getCard(isoDep)

        assertEquals(expectedCard.state, result.state)
    }

    @Test
    fun `open nfc settings correctly`() {
        val intent = CapturingSlot<Intent>()
        val context = mockk<Context> {
            every { startActivity(capture(intent)) } returns Unit
        }
        getSut().openNfcSettings(context)

        assertEquals("android.settings.NFC_SETTINGS", intent.captured.action)
        verifyOrder {
            intentProvider.settings()
            context.startActivity(intent.captured)
        }
    }
}
