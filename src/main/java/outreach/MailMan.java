package outreach;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import model.Person;
import data.PersonList;

@ManagedBean(name="mailMan") @RequestScoped
public class MailMan implements Serializable {

	private static final long serialVersionUID = -1980670543127065573L;

	@Inject
	PersonList lister;
	
	String subject;
	String messageBody;
	
	public MailMan() {
		System.out.println("MailMan.MailMan()");
	}
	
	public void sendMessageToAll() {
		System.out.println("MailMan.sendMessageToAll()");
		List<Person> all = lister.findAll();
		List<String> emails = new ArrayList<>(all.size());
		for (Person p : all) {
			final String email = p.getEmail();
			if (email != null) {
				emails.add(email);
			}
		}
		sendMessage("localhost", 
				"sender@clublist.mock", emails, 
				subject, messageBody);
	}

	private void sendMessage(String smtpHost, 
			String fromAddress, List<String> emails, 
			String message_subject, String message_body) {
		
		notNull("smtpHost", smtpHost);
		notNull("fromAddress", fromAddress);
		notNull("message_subject", message_subject);
		notNull("message_body", message_body);
		
		// We need to pass info to the mail server as a Properties, since
        // JavaMail (wisely) allows room for LOTS of properties...
        Properties props = new Properties();

        // The name of the server to send mail out through.
        props.put("mail.smtp.host", smtpHost);

        // Create the Session object
        Session session = Session.getDefaultInstance(props, null);
        session.setDebug(true);         // Verbose!
        
        try {
                // create a message
                Message mesg = new MimeMessage(session);

                // From Address
                final InternetAddress fromInternetAddress = new InternetAddress(fromAddress);
				mesg.setFrom(fromInternetAddress);
                // Send ourself a copy as well CC Address
                mesg.addRecipient(Message.RecipientType.BCC, fromInternetAddress);

                // TO Addresses - may be a lot!
                for (String message_recip : emails) {
                	InternetAddress toAddress = new InternetAddress(message_recip);
                	mesg.addRecipient(Message.RecipientType.TO, toAddress);
                }
                
                // The Subject
                mesg.setSubject(message_subject);

                // Now the message body.
                mesg.setText(message_body);
                // XXX I18N: use setText(msgText.getText(), charset)
                
                // Finally, send the message!
                Transport.send(mesg);

        } catch (MessagingException ex) {
        	throw new RuntimeException("Message failed", ex);
        }

	}

	private void notNull(String name, String value) {
		if (value == null) {
			throw new NullPointerException("Value of " + name + " must not be null!");
		}
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getMessageBody() {
		return messageBody;
	}

	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}
}
