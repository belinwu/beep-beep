package org.thechance.common.presentation.users

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import com.beepbeep.designSystem.ui.theme.Theme
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.thechance.common.presentation.uistate.UserUiState


object UserScreen : Screen, KoinComponent {

    private val screenModel: UserScreenModel by inject()

    @Composable
    override fun Content() {
        val state by screenModel.state.collectAsState()

        UserContent(state = state)
    }

    @Composable
    private fun UserContent(
        state: UserUiState,
    ) {

        Column(
            Modifier.background(Theme.colors.surface).fillMaxSize()
        ) {
            Box(Modifier.weight(1f)) {
                Text(text = "User Screen", color = Theme.colors.onPrimary)
            }
        }
    }
}