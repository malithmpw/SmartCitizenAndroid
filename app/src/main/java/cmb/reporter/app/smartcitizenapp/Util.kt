package cmb.reporter.app.smartcitizenapp

import java.util.*

fun Calendar.getDateString(): String {
    return "${this.get(Calendar.YEAR)}${this.get(Calendar.MONTH) + 1}${
        this.get(
            Calendar.DAY_OF_MONTH
        )
    }"
}

fun String.toTwoDigitNumber(): String {
    return if (this.length == 1) {
        "0${this}"
    } else {
        this
    }
}

fun Calendar.getFormattedDateString():String{
    val y = this.get(Calendar.YEAR)
    val m = "${(this.get(Calendar.MONTH)+1)}"
    val d = "${this.get(Calendar.DAY_OF_MONTH)}"
    return "$y${m.toTwoDigitNumber()}${d.toTwoDigitNumber()}"
}