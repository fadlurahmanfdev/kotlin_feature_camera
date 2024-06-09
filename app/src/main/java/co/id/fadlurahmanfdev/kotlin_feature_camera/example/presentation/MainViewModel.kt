package co.id.fadlurahmanfdev.kotlin_feature_camera.example.presentation

import androidx.lifecycle.ViewModel
import co.id.fadlurahmanfdev.kotlin_feature_camera.example.domain.ExampleCorePlatformUseCase

class MainViewModel(
    private val exampleCorePlatformUseCase: ExampleCorePlatformUseCase
) : ViewModel() {}