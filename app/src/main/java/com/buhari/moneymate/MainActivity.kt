package com.buhari.moneymate

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.buhari.moneymate.navigation.MoneyMateApp
import com.buhari.moneymate.ui.theme.MoneyMateTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MoneyMateTheme {
                MoneyMateApp()
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AppNavigationPreview() {
    MoneyMateTheme {
        MoneyMateApp()
    }
}