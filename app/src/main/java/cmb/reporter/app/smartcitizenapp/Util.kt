package cmb.reporter.app.smartcitizenapp

import java.util.*

fun Calendar.getDateString(): String {
    return "${this.get(Calendar.YEAR)}${this.get(Calendar.MONTH) + 1}${
        this.get(
            Calendar.DAY_OF_MONTH
        )
    }"
}