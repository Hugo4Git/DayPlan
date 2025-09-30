package com.hdy.plan

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.*
import com.hdy.plan.ui.TasksScreen
import com.hdy.plan.ui.theme.PlanTheme
import com.hdy.plan.ui.theme.ThemeFamily
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppGraph.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            var theme by rememberSaveable { mutableStateOf(ThemeFamily.NATURE_CALM) }
            PlanTheme(
                theme = theme,
                useDynamicColor = false,
                darkTheme = null
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    TasksScreen(
                        onFabLongClick = {
                            val all = ThemeFamily.entries.toTypedArray()
                            theme = all[(theme.ordinal + 1) % all.size]
                        }
                    )
                }
            }
        }
    }
}