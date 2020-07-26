package birthday.greetings

import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.mail.Message
import javax.mail.Session
import javax.mail.Transport
import javax.mail.internet.InternetAddress
import javax.mail.internet.MimeMessage

data class Employee(
  val name: String,
  val surname: String,
  val dateOfBirth: LocalDate,
  val email: String
)

private val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

fun sendGreetings(fileName: String, birthDate: String, smptHost: String, smtpPort: Int) {
  File(object {}.javaClass.getResource("/$fileName").toURI())
    .readLines()
    .drop(1)
    .map {  csvLine ->
      val (surname, name, dateAsString, email) = csvLine.split(", ")
      Employee(name, surname, LocalDate.parse(dateAsString, dateTimeFormatter), email)
    }
    .filter { e -> e.dateOfBirth.isEqual(LocalDate.parse(birthDate, dateTimeFormatter))  }
    .forEach {
      System.setProperty("mail.smtp.host", smptHost)
      System.setProperty("mail.smtp.port", smtpPort.toString())
      val mimeMessage = MimeMessage(Session.getDefaultInstance(System.getProperties()))
      mimeMessage.setFrom(InternetAddress("sender@email.com"))
      mimeMessage.addRecipients(Message.RecipientType.TO, arrayOf(InternetAddress(it.email)))
      mimeMessage.subject = "Happy Birthday!"
      mimeMessage.setContent("Happy Birthday, dear ${it.name}!", "text/plain")
      Transport.send(mimeMessage)
    }
}