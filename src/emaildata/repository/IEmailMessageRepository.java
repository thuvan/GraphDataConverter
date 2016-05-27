package emaildata.repository;

import java.util.List;

import emaildata.EmailMessage;

public interface IEmailMessageRepository {
	EmailMessage getEmailMessageByID(String messageID);
	EmailMessage getEmailByID(int id);	
	List<EmailMessage> getEmailByTime(long beginTime,long endTime);
	EmailMessage add(EmailMessage email);

	EmailMessage getFirstSentEmail();
	EmailMessage getLastSentEmail();
	int size();	
}

