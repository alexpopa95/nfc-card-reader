package com.freakyaxel.nfcreader

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.freakyaxel.nfc.api.CardReaderEvent
import com.freakyaxel.nfc.card.CardData
import com.freakyaxel.nfc.card.CardState
import com.freakyaxel.nfcreader.ui.theme.NFCReaderTheme
import java.util.Date

class MainActivity : ComponentActivity() {

    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cardStateText = mutableStateOf("")
        val settingsButtonVisible = mutableStateOf(false)

        lifecycleScope.launchWhenStarted {
            viewModel.cardReaderObservable.event.collect {
                settingsButtonVisible.value = false
                cardStateText.value = when (it) {
                    CardReaderEvent.CardLost -> "Keep it steady. Card lost!"
                    is CardReaderEvent.Error -> getErrorLabel(it.throwable)
                    CardReaderEvent.NFCDisabled -> {
                        settingsButtonVisible.value = true
                        "NFC Disabled"
                    }

                    CardReaderEvent.NFCNotSupported -> "NFC Not Supported"
                    CardReaderEvent.ReadyToScan -> "Ready to Scan"
                    CardReaderEvent.StartReading -> "Reading card..."
                    is CardReaderEvent.Success -> getCardLabel(it.card)
                }
            }
        }
        setContent {
            NFCReaderTheme {
                CardDataScreen(
                    data = cardStateText.value,
                    settingsButtonVisible = settingsButtonVisible.value
                ) {
                    viewModel.cardReader.openNfcSettings(this)
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.cardReaderObservable.start(this)
    }

    override fun onPause() {
        super.onPause()
        viewModel.cardReaderObservable.stop(this)
    }

    companion object {
        internal fun getCardLabel(cardData: CardData): String {
            return """
                AID: ${cardData.aids.joinToString(" | ")}
                Type: ${cardData.types.joinToString(" | ")}
                State: ${cardData.state.name}
                
                Number: ${cardData.formattedNumber}
                Expires: ${cardData.formattedDate}
                
                Valid: ${cardData.isValid}
            """.trimIndent()
        }

        internal fun getErrorLabel(error: Throwable): String {
            return """
                ERROR
                Message: ${error.message ?: error.cause?.message}
                
            """.trimIndent()
        }
    }

    @Composable
    fun CardDataScreen(
        data: String,
        settingsButtonVisible: Boolean,
        openNfcSettings: () -> Unit = {}
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = data)
            if (settingsButtonVisible) {
                Button(
                    onClick = openNfcSettings,
                    modifier = Modifier.padding(top = 15.dp)
                ) {
                    Text("SETTINGS")
                }
            }
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun ErrorPreview() {
        NFCReaderTheme {
            CardDataScreen(data = getErrorLabel(Exception("Error")), true)
        }
    }

    @Preview(showBackground = true)
    @Composable
    fun SuccessPreview() {
        val cardData = CardData(
            aids = listOf("A00000302", "A000004502"),
            types = listOf("Mastercard", "PagoBANCOMAT"),
            expireDate = Date(),
            number = "1234567890123456",
            state = CardState.ACTIVE
        )
        NFCReaderTheme {
            CardDataScreen(data = getCardLabel(cardData), true)
        }
    }
}