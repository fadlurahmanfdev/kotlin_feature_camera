package com.fadlurahmanfdev.lumi.core.exception

data class LumiCameraException(
    val enumError: String,
    override val message: String?
) : Throwable(message = message)
