package emaildata;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Vector;

public class EmailMessage{
	private int id;
	private String message_id;
	private Date date;
	private String from;
	private final List<String> to;
//	private final List<String> cc;
//	private final List<String> bcc;
	
	public EmailMessage(String message_id, Date date, String from, List<String> to){
		this.setMessageId(message_id);
		this.setDate(date);
		this.setFromAddress(from);
		this.to = to;
		
	}	
	public EmailMessage(){
		to = new Vector<String>();
//		this.cc = new Vector<String>();
//		this.bcc = new Vector<String>();
	}
	
	public void print() {
		System.out.println("id: "+getId());
		System.out.println("message_id: "+getMessageId());
		System.out.println("date: " + getDate().toString());
		System.out.println("From: "+getFromAddress());
		System.out.print("To: ");
		for(String t:getReceivers())
			System.out.print(t+",");
		System.out.println();
	}
	
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMessageId() {
		return message_id;
	}
	public void setMessageId(String message_id) {
		this.message_id = message_id;
	}
	public String getFromAddress() {
		return from;
	}
	public void setFromAddress(String from) {
		this.from = from;
	}
	public List<String> getReceivers() {
		return Collections.unmodifiableList(this.to);		
	}
	public void addReceiver(String m) {
		m=m.trim();
		if (to.contains(m))
			return;
		this.to.add(m);
	}
	
//	public void setAddToAddress(String toAddress) {
//		this.to.add(toAddress);
//	}
}