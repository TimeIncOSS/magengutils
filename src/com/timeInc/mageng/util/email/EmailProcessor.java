/*******************************************************************************
 * Copyright 2014 Time Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.timeInc.mageng.util.email;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.log4j.Logger;

import com.timeInc.mageng.util.misc.Precondition;

/**
 * @author jbabaria0609
 *
 * Singleton object to process email
 */
public class EmailProcessor {
	private static final Logger log = Logger.getLogger(EmailProcessor.class);

	private static final EmailProcessor instance = new EmailProcessor();

	private EmailProcessor() {}

	public static EmailProcessor getInstance() {
		return instance;
	}
	
	/**
	 * Sends an email to the smtp server. Using the provided parameters
	 * @param recipientList a list of emails to send to
	 * @param subject the subject of the email
	 * @param emailBody the email body
	 * @param from the sender of this email
	 * @param smtpHost the smtp server
	 */
	public void sendEmail(List<String> recipientList, String subject, String emailBody, String from, String smtpHost)  {
		List<Address> addresses = getRecipients(recipientList);

		log.debug("Sending email smtp host:" + smtpHost + " sender " + from);
		
		if(!addresses.isEmpty()) {
			Properties props = new Properties();
			props.put("mail.smtp.host", smtpHost);	
			
			Session session = Session.getDefaultInstance(props, null);
			session.setDebug(false);
			try {
				MimeMessage message = new MimeMessage(session);
				if(from != null){
					try {
						message.setFrom(new InternetAddress(from));
					} catch(AddressException e){	log.error(e.getMessage());	}					
				}
				message.setSubject(subject);				
				message.setRecipients(MimeMessage.RecipientType.TO, addresses.toArray(new Address[]{}));
				message.setText(emailBody);
				message.setSentDate(new Date());
				Transport.send(message);
			} catch(MessagingException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private static List<Address> getRecipients(List<String> recipientList) {
		Precondition.checkNull(recipientList, "recipientList");

		List<Address> addressList = new ArrayList<Address>();

		for(String recipient : recipientList) {
			try {
				addressList.add(new InternetAddress(recipient));
			} catch(AddressException e){
				throw new RuntimeException(e);
			}
		}
		
		return addressList;
	}	
}
