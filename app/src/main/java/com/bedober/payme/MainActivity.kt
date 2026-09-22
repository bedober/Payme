package com.bedober.payme

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bedober.payme.ui.PaymeApp
import com.bedober.payme.ui.PaymeViewModel
import com.bedober.payme.ui.theme.PaymeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PaymeTheme {
                PaymeApp(viewModel<PaymeViewModel>())
            }
        }
    }
}
