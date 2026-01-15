package com.zalphion.featurecontrol.emails

import com.zalphion.featurecontrol.AppError
import com.zalphion.featurecontrol.users.EmailAddress
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.mapFailure
import dev.forkhandles.result4k.peek
import dev.forkhandles.result4k.resultFrom
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.mail.Authenticator
import jakarta.mail.Message
import jakarta.mail.PasswordAuthentication
import jakarta.mail.Session
import jakarta.mail.Transport
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeBodyPart
import jakarta.mail.internet.MimeMessage
import jakarta.mail.internet.MimeMultipart
import org.http4k.config.Authority
import org.http4k.core.ContentType
import org.http4k.core.Credentials
import java.util.Properties
import kotlin.collections.set

private const val SMTP_PORT_DEFAULT = 25

fun EmailSender.Companion.smtp(
    fromName: String,
    fromAddress: EmailAddress,
    authority: Authority,
    credentials: Credentials?,
    startTls: Boolean
) = object: EmailSender {
    private val logger = KotlinLogging.logger {  }
    private val session = run {
        val props = Properties().apply {
            set("mail.smtp.host", authority.host.value)
            set("mail.smtp.port", authority.port?.value ?: SMTP_PORT_DEFAULT)
            set("mail.smtp.auth", credentials != null)
            set("mail.smtp.starttls.enable", startTls)
        }

        if (credentials != null) {
            val auth = PasswordAuthentication(credentials.user, credentials.password)
            Session.getInstance(props, object : Authenticator() {
                override fun getPasswordAuthentication() = auth
            })
        } else {
            Session.getInstance(props)
        }
    }

    override fun send(message: FullEmailMessage): Result4k<FullEmailMessage, AppError> {
        val content = MimeMultipart().apply {
            addBodyPart(MimeBodyPart().apply {
                setContent(message.data.textBody, ContentType.TEXT_PLAIN.toHeaderValue())
            })
            if (message.data.htmlBody != null) {
                addBodyPart(MimeBodyPart().apply {
                    setContent(message.data.htmlBody, ContentType.TEXT_HTML.toHeaderValue())
                })
            }
        }

        val mimeMessage = MimeMessage(session).apply {
            subject = message.data.subject
            setContent(content)
            setFrom(InternetAddress(fromAddress.value, fromName))
            message.to.forEach {
                addRecipient(Message.RecipientType.TO, InternetAddress(it.value))
            }
        }

        return resultFrom { Transport.send(mimeMessage) }
            .map { message }
            .peek { logger.info { "Email sent to ${it.to}: ${it.data.subject}" } }
            .mapFailure { AppError(it.message ?: "Error sending email: ${message.data.subject}") }
    }
}