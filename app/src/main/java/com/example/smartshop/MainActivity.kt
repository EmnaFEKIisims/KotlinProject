package com.example.smartshop

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.example.smartshop.ui.navigation.NavGraph
import com.example.smartshop.ui.theme.SmartShopTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SmartShopTheme {
                val navController = rememberNavController()
                NavGraph(navController)
            }
        }

    }
}
