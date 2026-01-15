package com.zalphion.featurecontrol.emails

import com.zalphion.featurecontrol.users.EmailAddress

data class FullEmailMessage(
    val to: List<EmailAddress>,
    val data: EmailMessageData
) {
    companion object
}