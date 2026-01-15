package com.zalphion.featurecontrol.emails

import com.zalphion.featurecontrol.AppError
import dev.forkhandles.result4k.Result4k

interface EmailSender {
    fun send(message: FullEmailMessage): Result4k<FullEmailMessage, AppError>
    
    companion object
}