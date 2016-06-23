package graphdata;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.imageio.metadata.IIOMetadataFormat;

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
import emaildata.repository.EmailDatabase;
import emaildata.repository.EmailRepository;
import emaildata.repository.UserRepository;
import utils.DateUtil;

public class GraphDataConverter {
	EmailDatabase emailDatabase;
//	private IGraphRepository graphRepository;

	public GraphDataConverter(EmailDatabase database) {
		this.emailDatabase = database;
//		this.graphRepository = graphRepo;
	}

	MyGraph createGraphFromEmailLists(List<EmailMessage> emails) {
		MyGraph graph = new MyGraph();
		for (EmailMessage email : emails) {
			extendGraphFromEmail(graph, email);
		}
		return graph;
	}

	static final String GRAPH_ATTRIBUTE_CONNECTED_COMPONENT_ID = "GRAPH_ATTRIBUTE_CONNECTED_COMPONENT_ID";

	List<MyGraph> splitConnectedComponentsIntoGraph(MyGraph graph) {
		List<MyGraph> result = new Vector<MyGraph>();

		ConnectedComponents cc = new ConnectedComponents();
		cc.init(graph);
		cc.compute();
		cc.setCountAttribute(GRAPH_ATTRIBUTE_CONNECTED_COMPONENT_ID);

		for (ConnectedComponent comp : cc) {
			System.out.println(comp.id);			
			MyGraph g = new MyGraph();
			for (Edge edge : comp.getEachEdge()) {
				g.addEdge(edge.getId(), edge.getSourceNode().getId(), edge.getTargetNode().getId());
			}
			result.add(g);
		}
		return result;
	}

	static final String NODE_ATTRIBUTE_USER = "NODE_ATTRIBUTE_USER";

	MyGraph extendGraphFromEmail(MyGraph graph, EmailMessage email) {
		User sender = emailDatabase.getUserByEmail(email.getFromAddress());
		for (String toAddress : email.getReceivers()) {
			User receiver = emailDatabase.getUserByEmail(toAddress);

			String edgeID = sender.getId() < receiver.getId() ? sender.getId() + "-" + receiver.getId()
					: receiver.getId() + "-" + sender.getId();
			String node1 = sender.getId() + "";
			String node2 = receiver.getId() + "";

			graph.addEdge(edgeID, node1, node2, false);
		}
		return graph;
	}

	// private void convertToGraph(Date fromDate, Date toDate, long period) {
	// graphRepository.clear();
	// List<EmailMessage> lstMails=
	// emailDatabase.getMailMessage(fromDate.getTime(), toDate.getTime());
	// MyGraph graph = this.createGraphFromEmailLists(lstMails);
	// List<MyGraph> lstGraph = this.splitConnectedComponentsIntoGraph(graph);
	// }
	//
	// public void loadEmailDatabase() throws IOException, ParseException {
	// this.emailDatabase.loadData();
	// }
	
		
	
	public static void main(String[] args) throws IOException, ParseException {				
		String firstDateString = "10/12/1999"; // Fri Dec 10 22:05:00 KST 1999";
		String lastDateString = "12/07/2000"; // "Fri Jul 12 17:31:00 KST 2002";
		int duration = 7;
		
		String mailDirPath = "E:/tmp/dataset/maildir";		
		String outputDir = "E:/tmp/dataset/graphdata";				
		
		SimpleDateFormat dateParser = new SimpleDateFormat("dd/MM/yyyy"); // ("EEE,
																			// dd
																			// MMM
																			// yyyy
																			// HH:mm:ss
																			// Z
																			// (z)");
		Date firstDate = dateParser.parse(firstDateString);
		Date lastDate = dateParser.parse(lastDateString);
		// long period = 5;

		System.out.println("first date: " + firstDate.toString() + ", Last date: " + lastDate.toString());
		System.out.println("Total date: " + DateUtil.subDate(lastDate, firstDate));

		long start = System.currentTimeMillis();
		
		EmailDatabase database = new EmailDatabase(mailDirPath, new EmailFileParser(), new UserRepository(),
				new EmailRepository());
		database.loadData();
				
		long afterLoadTime = System.currentTimeMillis();
		System.out.println("DB load time: " + TimeUnit.MILLISECONDS.toSeconds(afterLoadTime - start));


		IGraphRepository graphRepo = new GraphRepository();
		GraphDataConverter dataConverter = new GraphDataConverter(database);
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		
		Date beginDate = firstDate;		
		while (beginDate.compareTo(lastDate)<0){
			Date endDate = DateUtil.addDays(beginDate, duration);
			if (endDate.compareTo(lastDate)>0)
				endDate = lastDate;
			
			List<EmailMessage> lstMails = database.getMailMessage(beginDate.getTime(), endDate.getTime());
			System.out.println("Number of email: " + lstMails.size());

			MyGraph graph = dataConverter.createGraphFromEmailLists(lstMails);
			List<MyGraph> lstGraph = dataConverter.splitConnectedComponentsIntoGraph(graph);
			//save into graph repository
			for (MyGraph g : lstGraph) {
				graphRepo.addGraph(g);
			}			

			//write graphs as image files
			FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.VGA);
			pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

//			int i = 0;
			pic.writeAll(graph, outputDir+"\\graph_d" + duration + "_t"+dateFormat.format(beginDate)+"-"+dateFormat.format(endDate)+ "_group_v" + graph.getNodeCount() + "_e"
					+ graph.getEdgeCount() + "_total"+lstGraph.size()+ ".png");

//			for (MyGraph g : lstGraph) {
//				pic.writeAll(g, outputDir+"\\graph_d" + duration + "_i" + (++i) + "_v" + g.getNodeCount() + "_e"
//						+ g.getEdgeCount() + ".png");
//			}
			
			beginDate = endDate;
		}
				
		String filePath = outputDir+"\\graphdata_"+dateFormat.format(beginDate)+"-"+dateFormat.format(lastDate)+"_total"+graphRepo.getGraphCount()+".txt";
		boolean append = false;
		IGraphWriter graphWriter = new GraphFileWriter(filePath,append);		
		Iterator<MyGraph> iter =graphRepo.getIterator(); 
		while (iter.hasNext()){
			graphWriter.write(iter.next());
		}
		graphWriter.close();
			
	}
}
