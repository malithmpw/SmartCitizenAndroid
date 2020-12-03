package cmb.reporter.smartcitizen.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.Status;

class SmartCitySmsBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
            val extras: Bundle? = intent.extras
            var status :Status? = null;
            if (extras != null) {
                status = extras.get(SmsRetriever.EXTRA_STATUS) as Status?
            }

            if(status != null) {
                when (status.statusCode) {
                    CommonStatusCodes.SUCCESS-> {
                        // Get SMS message contents
                        val message: String = extras?.get(SmsRetriever.EXTRA_SMS_MESSAGE) as String
                        // Extract one-time code from the message and complete verification
                        // by sending the code back to your server.
                    }
                    CommonStatusCodes.TIMEOUT ->{}
                }
            }
        }
    }
}