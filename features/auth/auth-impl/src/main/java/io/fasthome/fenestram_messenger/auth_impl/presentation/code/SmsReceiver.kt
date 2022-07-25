package io.fasthome.fenestram_messenger.auth_impl.presentation.code

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony

class SmsReceiver(val callback : String.() -> Unit) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        Telephony.Sms.Intents.getMessagesFromIntent(intent)
            .forEach {
                if (it.originatingAddress == phoneNumber) {
                    callback(it.displayMessageBody)
                }
            }
    }

    companion object {
        const val phoneNumber = "OOO_VERSHIN"
    }
}