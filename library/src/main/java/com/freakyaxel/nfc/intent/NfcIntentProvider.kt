package com.freakyaxel.nfc.intent

import android.content.Intent
import android.provider.Settings

internal class NfcIntentProvider {
    fun settings(): Intent = Intent(Settings.ACTION_NFC_SETTINGS)
}