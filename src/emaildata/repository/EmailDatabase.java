package emaildata.repository;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import org.graphstream.algorithm.ConnectedComponents;
import org.graphstream.algorithm.ConnectedComponents.ConnectedComponent;
import org.graphstream.graph.Edge;
import org.graphstream.stream.file.FileSinkImages;
import org.graphstream.stream.file.FileSinkImages.LayoutPolicy;
import org.graphstream.stream.file.FileSinkImages.OutputType;
import org.graphstream.stream.file.FileSinkImages.Resolutions;

import emaildata.EmailFileParser;
import emaildata.EmailMessage;
import emaildata.User;
import graphdata.GraphRepository;
import graphdata.IGraphRepository;
import graphdata.MyGraph;
import utils.DateUtil;

public class EmailDatabase {
	static final String SENT_MAIL_DIR_NAME = "_sent_mail";
	final EmailFileParser emailFileParser ;	
	final IUserRepository userRepository ;
	final IEmailMessageRepository emailRepository ;
//	IGraphRepository graphRepository = new GraphRepository();
	
	final String rootDir;
	public EmailDatabase(String rootDir,EmailFileParser emailMessageFileParser,IUserRepository userRepo,IEmailMessageRepository emailMessageRepo) throws IOException, ParseException{
		this.rootDir = rootDir;		
		this.emailFileParser = emailMessageFileParser;
		this.userRepository = userRepo;
		this.emailRepository = emailMessageRepo;
	}
	
	public void loadData() throws IOException, ParseException{
		final File folder = new File(rootDir);		
		//load 
		for (final File userDir : folder.listFiles()) {
	        if (userDir.isDirectory()) {
	            String username = userDir.getName();	 
	            for (final File userSubDir : userDir.listFiles()) {
	            	if (userSubDir.isDirectory() && userSubDir.getName().equalsIgnoreCase(SENT_MAIL_DIR_NAME)){
			            for (final File emailFile : userSubDir.listFiles()) {
			            	if (emailFile.isFile()) {
			            		EmailMessage email = emailFileParser.parseFile(emailFile);	            		
			            		userRepository.addOrUpdate(email.getFromAddress(),username);
			            		
			            		if (email.getReceivers() !=null)
				            		for(String to: email.getReceivers())
				            			userRepository.addIfNotExist(to);	            		
			            		
			            		emailRepository.add(email);
			            	}
			            }
	            	}
	            }
	        }
	    }
	}
	
	public List<EmailMessage> getMailMessage(long fromTime,long toTime){
		return emailRepository.getEmailByTime(fromTime, toTime);
	}
	
	
	public void printSummary(){
		System.out.println("Number of user: "+userRepository.size());
		System.out.println("Number of email: "+emailRepository.size());
	}
	
	public static void main(String[] args) throws IOException, ParseException {		
		long start = System.currentTimeMillis();
		
		String mailDirPath = "E:/tmp/dataset/maildir";
		EmailDatabase database = new EmailDatabase(mailDirPath,new EmailFileParser(),new UserRepository(),new EmailRepository());
		database.loadData();				
		database.printSummary();
		EmailMessage first = database.emailRepository.getFirstSentEmail();
		EmailMessage last = database.emailRepository.getLastSentEmail();
		
		System.out.println("first date: "+first.getDate().toString()+", Last date: "+last.getDate().toString());
		System.out.println("Total date: "+DateUtil.subDate(last.getDate(),first.getDate()));
		
		long afterLoadTime = System.currentTimeMillis() ;		
		System.out.println("DB load time: "+ TimeUnit.MILLISECONDS.toSeconds(afterLoadTime-start));
		
//		
//		int i=0;
//		
//		Date beginDate = first.getDate();		
//		while (beginDate.before(last.getDate())){	
//			Date endDate = DateUtil.addDays(beginDate, 7);
//			System.out.println("================ FROM DATE "+ beginDate.toString() +" =====> DATE"+endDate.toString());
//			List<EmailMessage> msgs = database.emailRepository.getEmailByTime(beginDate.getTime(), endDate.getTime());
//			for(EmailMessage em: msgs)
//				em.print();
//			i++;
//			if (i>=10)
//				break;
//		}
	}

	public User getUserByEmail(String email) {
		return userRepository.getUserByEmail(email);
	}
}
