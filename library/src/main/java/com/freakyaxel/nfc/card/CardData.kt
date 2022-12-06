package com.freakyaxel.nfc.card

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CardData(
    val aids: List<String>,
    val types: List<String>,
    val expireDate: Date?,
    val number: String?,
    val state: CardState
) {

    var formattedNumber: String? = null
        private set

    var formattedDate: String? = null
        private set

    var expireMonth: String? = null
        private set

    var expireYear: String? = null
        private set

    val isValid: Boolean
        get() = number != null && expireDate != null && state != CardState.LOCKED

    init {
        if (number != null) {
            formattedNumber = number.filter { it.isDigit() }
        }

        if (expireDate != null) {
            formattedDate = EXPECTED_DATE_FORMAT.format(expireDate).also {
                expireMonth = it.take(2)
                expireYear = it.takeLast(2)
            }
        }
    }

    override fun toString(): String {
        return """
            AID: ${aids.joinToString()}
            types: ${types.joinToString()}
            Number: $number
            FormattedNumber: $formattedNumber
            ExpDate: $expireDate
            State: $state
            IsValid: $isValid
        """
    }

    companion object {
        internal val EXPECTED_DATE_FORMAT = SimpleDateFormat("MM/yy", Locale.US)
    }
}
