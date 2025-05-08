package com.lucius.testfullscreenintent

import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity // Make sure it's AppCompatActivity if you used Empty Views Activity template

class FullScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // These flags are important for full-screen intents that need to show over the lock screen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                        or WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
                        or WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON // Optional, but good for alerts
                        or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON // Optional
            )
        }
        // For dismissing the activity on back press, which is default behavior.
        // You might want to add specific logic if you had a "Dismiss" button.

        setContentView(R.layout.activity_full_screen) // If you used XML

        // If you were using Compose for this Activity, it would be:
        // setContent {
        //     FullScreenNotificationComposeDemoTheme {
        //         Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        //             Text("Full Screen Composable Activity!", textAlign = TextAlign.Center)
        //         }
        //     }
        // }
    }
}