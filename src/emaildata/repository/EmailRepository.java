package emaildata.repository;

import java.util.Comparator;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import java.util.TreeSet;
import java.util.Vector;

import common.AbstractRepository;
import emaildata.*;
import rawdata.exception.DuplicateKeyException;

public class EmailRepository extends AbstractRepository implements IEmailMessageRepository {	
	private final Map<Integer, EmailMessage> emailByID = new Hashtable<Integer,EmailMessage>();
	private final Map<String, EmailMessage> emailByMessageID = new Hashtable<String,EmailMessage>();	
	SortedSet<EmailMessage> timeOrderedEmail = new TreeSet<EmailMessage>(new SentDateEmailComparator());
	
	class SentDateEmailComparator implements Comparator<EmailMessage>{
		@Override
		public int compare(EmailMessage em1, EmailMessage em2) {
			return em1.getDate().compareTo(em2.getDate());
		}		
	}
	
	@Override
	public EmailMessage add(EmailMessage email) {		
		email.setId(this.getAndIncrementNextId());		
		if (emailByMessageID.containsKey(email.getMessageId()))
			throw new DuplicateKeyException("email message id '"+email.getMessageId()+"' is duplicated");		
		emailByMessageID.put(email.getMessageId(), email);		
		emailByID.put(email.getId(), email);		
		timeOrderedEmail.add(email);
		return email;
	}

	@Override
	public EmailMessage getEmailMessageByID(String messageId) {
		return emailByMessageID.get(messageId);
	}

	@Override
	public EmailMessage getEmailByID(int id) {
		return emailByID.get(id);
	}

	@Override
	public List<EmailMessage> getEmailByTime(long beginTime, long endTime) {
		EmailMessage fromElement = new EmailMessage();
		fromElement.setDate(new Date(beginTime));
		EmailMessage toElement = new EmailMessage();
		toElement.setDate(new Date(endTime));
		
		Vector<EmailMessage> result = new Vector<EmailMessage>();		
		for (EmailMessage email : timeOrderedEmail.subSet(fromElement, toElement))
			result.add(email);
		return result;
	}

	@Override
	public int size() {
		return this.emailByMessageID.size();
	}

	@Override
	public EmailMessage getFirstSentEmail() {
		return timeOrderedEmail.first();
	}

	@Override
	public EmailMessage getLastSentEmail() {
		return this.timeOrderedEmail.last();
	}
}
