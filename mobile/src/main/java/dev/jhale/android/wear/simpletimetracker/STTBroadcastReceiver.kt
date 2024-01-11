package dev.jhale.android.wear.simpletimetracker

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log

class STTBroadcastReceiver : BroadcastReceiver() {
    private val messageSenderUtil: MessageSenderUtil = MessageSenderUtil()

    override fun onReceive(context: Context?, intent: Intent?) {
        val action = intent?.action
        if (intent == null || action == null) { return }

        Log.i(LOG_TAG, action)

        when (action) {
            "com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_CATEGORIES_RESPONSE" -> {
                intent.getStringExtra("categories")?.also {
                    Log.i(LOG_TAG, it)

                    if (context != null) {
                        returnCategoriesList(context, it)
                    }
                }
            }

            "com.razeeman.util.simpletimetracker.ACTION_ADHOC_EXPORT_PREFS_RESPONSE" -> {
                intent.getBooleanExtra("allow_multitasking", false)?.also {
                    Log.i(LOG_TAG, it.toString())
                }
            }

            else -> {
            }
        }
    }

    fun returnCategoriesList(context: Context, sttExportModelJson: String) {
        Thread(Runnable {
            messageSenderUtil.sendMessage(
                context,
                MessageReceiverService.RECEIVE_CATEGORIES,
                sttExportModelJson
            )
        }).start()
    }

}