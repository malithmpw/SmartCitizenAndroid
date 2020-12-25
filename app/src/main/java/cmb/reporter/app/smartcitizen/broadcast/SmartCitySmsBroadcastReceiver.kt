package cmb.reporter.app.smartcitizen.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage
import android.widget.Toast

class SmartCitySmsBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val bundle = intent?.extras
        var msgs: Array<SmsMessage?>? = null
        var str = ""
        if (bundle != null) {
            // Retrieve SMS message
            val pdus = bundle["pdus"] as Array<*>?
            msgs = arrayOfNulls(pdus!!.size)
            for (i in msgs.indices) {
                msgs[i] = SmsMessage.createFromPdu(pdus[i] as ByteArray)
                str += msgs[i]?.messageBody.toString()
            }
            // Display message
            Toast.makeText(context, str, Toast.LENGTH_SHORT).show()
            val broadcastIntent = Intent()
            broadcastIntent.action = "SMS_RECEIVED_ACTION"
            broadcastIntent.putExtra("sms", str)
            context!!.sendBroadcast(broadcastIntent)
        }
    }
}