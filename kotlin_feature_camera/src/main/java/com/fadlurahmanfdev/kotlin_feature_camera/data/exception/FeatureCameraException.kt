package com.fadlurahmanfdev.kotlin_feature_camera.data.exception

data class FeatureCameraException(
    val enumError: String,
    override val message: String?
) : Throwable(message = message)
