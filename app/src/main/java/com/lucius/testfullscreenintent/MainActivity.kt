package com.lucius.testfullscreenintent

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.lucius.testfullscreenintent.ui.theme.TestFullScreenIntentTheme
import android.Manifest // For the permission string
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
private const val FULL_SCREEN_NOTIFICATION_CHANNEL_ID = "full_screen_channel_01"
private const val FULL_SCREEN_NOTIFICATION_ID = 101

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TestFullScreenIntentTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    MainScreen()
                }
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "Full Screen Notifications"
        val descriptionText = "Channel for important full-screen alerts"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(FULL_SCREEN_NOTIFICATION_CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
        println("Notification channel created/updated.")
    }
}

// HELPER FUNCTION for showing the notification (Top-level private in the file)
private fun showFullScreenNotification(context: Context) {
    println("Attempting to show full-screen notification.")
    createNotificationChannel(context) // Ensure channel exists

    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    val fullScreenIntent = Intent(context, FullScreenActivity::class.java)
    val fullScreenPendingIntent = PendingIntent.getActivity(
        context,
        0,
        fullScreenIntent,
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
    )

    val builder = NotificationCompat.Builder(context, FULL_SCREEN_NOTIFICATION_CHANNEL_ID)
        .setSmallIcon(R.drawable.ic_launcher_foreground) // IMPORTANT: Use a real icon
        .setContentTitle("Full Screen Alert!")
        .setContentText("This is a full-screen notification demo.")
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setContentIntent(fullScreenPendingIntent)
        .setFullScreenIntent(fullScreenPendingIntent, true)
    // .setAutoCancel(true)

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
            notificationManager.notify(FULL_SCREEN_NOTIFICATION_ID, builder.build())
            println("Notification posted.")
        } else {
            println("POST_NOTIFICATIONS permission NOT granted. Cannot show notification.")
        }
    } else {
        notificationManager.notify(FULL_SCREEN_NOTIFICATION_ID, builder.build())
        println("Notification posted (older OS).")
    }
}

@Composable
fun MainScreen(modifier: Modifier = Modifier) {
    val context = LocalContext.current // Get the current context

    // State to track if permission is granted
    var hasNotificationPermission by remember {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // TIRAMISU is API 33
            mutableStateOf(
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            )
        } else {
            mutableStateOf(true) // Permissions auto-granted on older versions
        }
    }

    // Launcher for permission request
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            hasNotificationPermission = isGranted
            if (!isGranted) {
                // Optional: Show a toast or a message if permission is denied
                println("Notification permission DENIED. Prepare for disappointment.")
            } else {
                println("Notification permission GRANTED. Let the spam begin!")
            }
        })


    Column(
        modifier = modifier.fillMaxSize(), // Make the Column take the whole screen
        verticalArrangement = Arrangement.Center, // Center its children vertically
        horizontalAlignment = Alignment.CenterHorizontally // Center its children horizontally
    ) {

        Button(
            onClick = {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (!hasNotificationPermission) {
                        permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else {
                        // Permission already granted
                        showFullScreenNotification(context) // Call our new function
                    }
                } else {
                    // No runtime permission needed for older OS
                    showFullScreenNotification(context) // Call our new function
                }
                println("Button clicked! Prepare for glory... or a crash.")
            }
        ) {
            Text("Show notification with full screen")
        }
        // Optional: Display permission status
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                if (hasNotificationPermission) "Notification Permission: GRANTED"
                else "Notification Permission: DENIED / NOT REQUESTED"
            )
        }
    }
    }



@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    TestFullScreenIntentTheme {
        MainScreen()
    }
}