package com.example

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.ui.theme.AppThemeMode
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Pulso", appName)
  }

  @Test
  fun `theme mode can be changed and persisted`() {
    val application = ApplicationProvider.getApplicationContext<Application>()
    val viewModel = PulsoViewModel(application)

    // Altera para tema AMOLED
    viewModel.setThemeMode(AppThemeMode.AMOLED)
    assertEquals(AppThemeMode.AMOLED, viewModel.themeMode.value)

    // Altera para tema Sunset
    viewModel.setThemeMode(AppThemeMode.SUNSET)
    assertEquals(AppThemeMode.SUNSET, viewModel.themeMode.value)

    // Altera para tema Claro
    viewModel.setThemeMode(AppThemeMode.LIGHT)
    assertEquals(AppThemeMode.LIGHT, viewModel.themeMode.value)

    // Altera para tema Escuro
    viewModel.setThemeMode(AppThemeMode.DARK)
    assertEquals(AppThemeMode.DARK, viewModel.themeMode.value)
  }
}
