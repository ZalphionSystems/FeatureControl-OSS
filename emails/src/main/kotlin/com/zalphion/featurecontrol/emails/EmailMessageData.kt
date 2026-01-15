package com.zalphion.featurecontrol.emails

data class EmailMessageData(
    val subject: String,
    val textBody: String,
    val htmlBody: String?
) {
    companion object
}