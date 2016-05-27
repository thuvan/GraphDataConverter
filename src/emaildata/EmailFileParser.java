package emaildata;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import utils.RegexUtil;


public class EmailFileParser{
	
	Logger getLoger(){
		return Logger.getLogger(this.getClass());
	}
	
//	String mailDirPath = "E:/tmp/dataset/maildir";
//	String sentMailDirPath = "_sent_mail";
	
	
	
	public static String parseMessageID(String line){
		String regex = "<.+>";
		String result = RegexUtil.getSubStringByRegex(regex, line);
		if (result!=null)
			return result.substring(1, result.length()-1).trim();
		return null;
	}
	
	
	public static Date parseDate(String line) throws ParseException{
		//Date: Mon, 14 May 2001 13:39:00 -0700 (PDT)
		String regex = "(Date:)(.+)";
		String result = RegexUtil.getSubStringByRegex(regex, line,2).trim();
		if (result==null)			
			return null;		
		//Mon, 14 May 2001 13:39:00 -0700 (PDT)
		Date d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z (z)").parse(result);
		return d;
	}

	public static void main(String[] args) throws ParseException, IOException {
		String input = " Cc  : hello abc";
		String regex = "^\\s*(To|Cc|Bcc|Message-ID|Date|From)\\s*:\\s*(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(input); // get a matcher object
		if (m.find())
		{
			String first = input.substring(m.start(1),m.end(1));
			String second = input.substring(m.start(2),m.end(2));
			
			System.out.println("First: "+first);
			System.out.println("Second: "+second);
			System.out.println("Second: "+second.length());
		}
		
		EmailFileParser parser = new EmailFileParser();
		String name = "allen-p";
		String filePath = "E:/tmp/dataset/maildir/allen-p/_sent_mail/1";
		File file = new File(filePath);
		EmailMessage mail = parser.parseFile(file);
		mail.print();
	}
	
	public EmailMessage parseFile(File file) throws IOException, ParseException{
		//System.out.println("DEBUG: parse email file '"+file.getAbsolutePath()+"'");
		//getLoger().debug("parse email file '"+file.getAbsolutePath()+"'");
		
		FileInputStream fis = new FileInputStream(file);		 
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
		EmailMessage email = new EmailMessage();				
		
		String line = null;
		String lastHeader = ""; 
		while ((line = br.readLine()) != null) {
			line = line.trim();
			if (line.isEmpty())
				break; //finish header
			//String regexString = "^\\s*(To|Cc|Bcc|Message-ID|Date|From)\\s*:\\s*(.*)";
			String regexString = "^(\\w+\\-*\\w*):\\s*(.*)";
			Pattern p = Pattern.compile(regexString);
			Matcher m = p.matcher(line); // get a matcher object
			if (m.find())
			{
				String first = line.substring(m.start(1),m.end(1));
				String second = line.substring(m.start(2),m.end(2));
				lastHeader = first;
				
				if (first.equals("Message-ID")){
					email.setMessageId(parseMessageID(second));
				//sent date
				}else if (first.equals("Date")){
					//Mon, 14 May 2001 13:39:00 -0700 (PDT)
					Date d = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z (z)").parse(second);
					email.setDate(d);
				//from address
				}else if (first.equals("From")){
					email.setFromAddress(second);
				//to address multiple line?
				}else if (first.equals("To") || first.equals("Cc")||first.equals("Bcc")){
					addToAddress(email, second);
				}
			}else{
				if (lastHeader.equals("To") || lastHeader.equals("Cc")||lastHeader.equals("Bcc")){
					addToAddress(email, line);
				}
			}
						
		}	 
		br.close();
		
		return email;
	}
	
//	public EmailMessage parseFile1(File file) throws IOException, ParseException{
//		//System.out.println("DEBUG: parse email file '"+file.getAbsolutePath()+"'");
//		getLoger().debug("parse email file '"+file.getAbsolutePath()+"'");
//		
//		FileInputStream fis = new FileInputStream(file);		 
//		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
//		EmailMessage email = new EmailMessage();				
//		
//		String line = null;
//		while ((line = br.readLine()) != null) {
//			line = line.trim();
//			if (line.isEmpty())
//				break; //finish header			
//			
//			//message id
//			if (line.startsWith("Message-ID:")){
//				email.setMessageId(parseMessageID(line));
//			//sent date
//			}else if (line.startsWith("Date:")){
//				email.setDate(parseDate(line));
//			//from address
//			}else if (line.startsWith("From:")){
//				String regex = "^(From:)(.+)";
//				email.setFromAddress(RegexUtil.getSubStringByRegex(regex, line,2).trim());
//			//to address multiple line?
//			}else if (line.startsWith("To:") || line.startsWith("Cc:")||line.startsWith("Bcc:")){
//				//To: frank.ermis@enron.com, steven.south@enron.com
//				String regex = "^(To|Cc|Bcc):(.+)";
//				String to_emails = RegexUtil.getSubStringByRegex(regex, line,2).trim();		
//				addToAddress(email, to_emails);
//			}else if (line.startsWith("Cc:")){
//				//Cc: frank.ermis@enron.com, steven.south@enron.com
//				String regex = "(Cc:)(.+)";
//				String to_emails = RegexUtil.getSubStringByRegex(regex, line,2).trim();		
//				addToAddress(email, to_emails);
//			}
//		}
//	 
//		br.close();
//		
//		return email;
//	}

	private void addToAddress(EmailMessage email, String to_emails) {								
		for(String m:to_emails.split(",")){
			if (m.trim().length()>0)
				email.addReceiver(m);
		}
	}
	
//	public EmailMessage readEmail(String name,int mailIndex) throws IOException, ParseException{
//		String filePath = mailDirPath+File.separator+name+File.separator+sentMailDirPath+File.separator+mailIndex;
//		File file = new File(filePath);
//		return parseFile(file);		
//	}
}
