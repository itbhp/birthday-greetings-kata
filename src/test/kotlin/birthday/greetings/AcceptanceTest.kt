package birthday.greetings

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import javax.mail.Address
import javax.mail.Message
import javax.mail.internet.MimeMessage

private const val NONSTANDARD_PORT = 9999

class AcceptanceTest {
  private lateinit var mailServer: GreenMail

  @Before
  fun setUp() {
    mailServer = GreenMail(ServerSetup(NONSTANDARD_PORT, null, "smtp"))

    mailServer.start()
  }

  @After
  fun tearDown() {
    mailServer.stop()
    Thread.sleep(200)
  }

  @Test
  fun willSendGreetings_whenItsSomebodysBirthday() {
    sendGreetings(
      "good_file.txt",
      "1982/10/08",
      "localhost",
      NONSTANDARD_PORT
    )

    val messages: Array<MimeMessage> = mailServer.receivedMessages

    assertEquals("message not sent?", 1, messages.size)

    val message: MimeMessage = messages[0]
    val content: String = message.content as String
    assertThat(content, containsString("Happy Birthday, dear John!"))

    assertEquals("Happy Birthday!", message.subject)

    val recipients: Array<out Address> = message.getRecipients(Message.RecipientType.TO)
    assertEquals(1, recipients.size)
    assertThat(recipients[0].toString(), equalTo("john.doe@foobar.com"))
  }

  @Test
  fun willNotSendEmailsWhenNobodysBirthday() {
    sendGreetings(
      "good_file.txt",
      "2008/01/01",
      "localhost",
      NONSTANDARD_PORT
    )
    assertEquals("what? messages?", 0, mailServer.getReceivedMessagesForDomain("localhost").size)
  }
}