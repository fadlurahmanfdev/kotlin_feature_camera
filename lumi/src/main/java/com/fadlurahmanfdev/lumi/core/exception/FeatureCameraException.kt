package com.fadlurahmanfdev.lumi.core.exception

data class FeatureCameraException(
    val enumError: String,
    override val message: String?
) : Throwable(message = message)
