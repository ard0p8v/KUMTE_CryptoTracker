package cz.ukh.fim.kumte.cryptotracker.util

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

fun formatNumberWithSpace(number: Double): String {
    val symbols = DecimalFormatSymbols(Locale("cs", "CZ")).apply {
        groupingSeparator = ' '
        decimalSeparator = '.'
    }
    val decimalFormat = DecimalFormat("#,##0.##", symbols)
    return decimalFormat.format(number)
}
