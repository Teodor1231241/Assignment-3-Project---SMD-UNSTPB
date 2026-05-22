package com.phishguard.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.phishguard.app.utils.NotificationHelper
import com.phishguard.app.utils.PhishDetector
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Telephony.Sms.Intents.SMS_RECEIVED_ACTION) return

        val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
        if (messages.isNullOrEmpty()) return

        val pendingResult = goAsync()

        // Using Dispatchers.IO for background processing including Room DB operations
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val detector = PhishDetector(context)
                for (sms in messages) {
                    val sender = sms.originatingAddress ?: "Unknown"
                    val body = sms.messageBody ?: ""

                    val result = detector.analyzeMessage(sender, body)

                    if (result.isSuspicious) {
                        NotificationHelper.showPhishWarning(context, sender, body, result)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                // Ensure finish() is called to let the system know we are done
                pendingResult.finish()
            }
        }
    }
}