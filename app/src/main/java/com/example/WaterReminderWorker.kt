package com.example

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import java.util.concurrent.TimeUnit

/**
 * Worker do WorkManager responsável por disparar lembretes periódicos
 * de hidratação e autocuidado para o usuário.
 */
class WaterReminderWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        return try {
            showHydrationNotification()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun showHydrationNotification() {
        createNotificationChannel()

        // Verificar permissão no Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val permissionCheck = ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
                return
            }
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val tips = listOf(
            "Um copo d'água agora renova sua energia e clareza mental!",
            "Pausa saudável: hidrate-se e descanse os olhos da tela por 20 segundos.",
            "Manter-se hidratado previne a fadiga e melhora a concentração digital.",
            "Lembrete do Pulso: beba água e faça uma respiração profunda."
        )
        val selectedTip = tips.random()

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hora de se hidratar! 💧")
            .setContentText(selectedTip)
            .setStyle(NotificationCompat.BigTextStyle().bigText(selectedTip))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = NotificationManagerCompat.from(context)
        try {
            notificationManager.notify(NOTIFICATION_ID, notification)
        } catch (securityException: SecurityException) {
            // Falha silenciosa de segurança de permissão
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Lembretes de Hidratação - Pulso"
            val descriptionText = "Notificações periódicas para incentivar o consumo de água e pausas saudáveis"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_ID = "pulso_water_reminders_channel"
        const val NOTIFICATION_ID = 1001
        const val UNIQUE_PERIODIC_WORK_NAME = "pulso_periodic_water_reminder"

        /**
         * Agenda o lembrete periódico com o WorkManager.
         * Intervalo mínimo padrão do Android para periódicos é de 15 minutos.
         */
        fun schedulePeriodicReminder(context: Context, intervalHours: Long = 2) {
            try {
                val workManager = WorkManager.getInstance(context)
                val effectiveHours = if (intervalHours < 1) 1L else intervalHours

                val periodicRequest = PeriodicWorkRequestBuilder<WaterReminderWorker>(
                    effectiveHours, TimeUnit.HOURS
                ).build()

                workManager.enqueueUniquePeriodicWork(
                    UNIQUE_PERIODIC_WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    periodicRequest
                )
            } catch (e: Exception) {
                // Previne falha em ambientes de teste de unidade sem WorkManager inicializado
                e.printStackTrace()
            }
        }

        /**
         * Cancela os lembretes agendados no WorkManager.
         */
        fun cancelReminders(context: Context) {
            try {
                val workManager = WorkManager.getInstance(context)
                workManager.cancelUniqueWork(UNIQUE_PERIODIC_WORK_NAME)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /**
         * Dispara uma execução imediata através do WorkManager para teste rápido.
         */
        fun triggerImmediateTest(context: Context) {
            try {
                val workManager = WorkManager.getInstance(context)
                val oneTimeRequest = OneTimeWorkRequestBuilder<WaterReminderWorker>().build()
                workManager.enqueue(oneTimeRequest)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
