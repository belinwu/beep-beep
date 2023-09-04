package presentation.notification

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import resources.Resources

class NotificationScreen : Screen {

    @Composable
    override fun Content() {
        Text(text = Resources.strings.notification)
    }

}