package com.freakyaxel.nfc.provider

import android.nfc.tech.IsoDep
import com.freakyaxel.nfc.toHex
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.mockk.verifyOrder
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

internal class IProviderImplTest {

    private val isoDep: IsoDep = mockk {
        every { transceive(any()) } returns byteArrayOf(0x01)
        every { hiLayerResponse } returns byteArrayOf(0x02)
        every { historicalBytes } returns byteArrayOf(0x03)
    }

    private val sut: IProviderImpl
        get() = IProviderImpl(isoDep)

    @Test
    fun `transceive correctly`() {
        val input = byteArrayOf(0x07)
        val result = sut.transceive(input)

        assertEquals(byteArrayOf(0x01).toHex(), result?.toHex())
        verify { isoDep.transceive(input) }
    }

    @Test
    fun `getAt correctly - historicalBytes`() {
        val result = sut.at

        assertEquals(byteArrayOf(0x03).toHex(), result?.toHex())
        verify { isoDep.historicalBytes }
    }

    @Test
    fun `getAt correctly - hiLayerResponse`() {
        every { isoDep.historicalBytes } returns null
        val result = sut.at

        assertEquals(byteArrayOf(0x02).toHex(), result?.toHex())
        verifyOrder {
            isoDep.historicalBytes
            isoDep.hiLayerResponse
        }
    }

    @Test
    fun `getAt correctly - null`() {
        every { isoDep.hiLayerResponse } returns null
        every { isoDep.historicalBytes } returns null
        val result = sut.at

        assertNull(result)
        verifyOrder {
            isoDep.historicalBytes
            isoDep.hiLayerResponse
        }
    }
}
