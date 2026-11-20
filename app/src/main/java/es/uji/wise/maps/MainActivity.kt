package es.uji.wise.maps

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import es.uji.wise.maps.ui.WiseMapsApp
import es.uji.wise.maps.ui.theme.WiseMapsTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WiseMapsTheme {
                WiseMapsApp()
            }
        }
    }
}

