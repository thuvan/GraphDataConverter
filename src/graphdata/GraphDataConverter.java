package graphdata;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
import emaildata.repository.EmailDatabase;
import emaildata.repository.EmailRepository;
import emaildata.repository.UserRepository;
import utils.DateUtil;

public class GraphDataConverter {
	EmailDatabase emailDatabase;
	IGraphRepository graphRepository;

	public GraphDataConverter(EmailDatabase database, IGraphRepository graphRepo) {
		this.emailDatabase = database;
		this.graphRepository = graphRepo;
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
		String lastDateString = "12/07/2002"; // "Fri Jul 12 17:31:00 KST 2002";

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
		String mailDirPath = "E:/tmp/dataset/maildir";
		IGraphRepository graphRepo = new GraphRepository();

		EmailDatabase database = new EmailDatabase(mailDirPath, new EmailFileParser(), new UserRepository(),
				new EmailRepository());
		database.loadData();

		GraphDataConverter dataConverter = new GraphDataConverter(database, graphRepo);
		// dataConverter.loadEmailDatabase();
		//
		// dataConverter.convertToGraph(firstDate,lastDate, period);
		//

		long afterLoadTime = System.currentTimeMillis();
		System.out.println("DB load time: " + TimeUnit.MILLISECONDS.toSeconds(afterLoadTime - start));

		Date beginDate = firstDate;
		for (int duration = 1; duration < 30; duration += 2) {
			Date endDate = DateUtil.addDays(beginDate, duration);
			List<EmailMessage> lstMails = database.getMailMessage(beginDate.getTime(), endDate.getTime());
			System.out.println("Number of email: " + lstMails.size());

			MyGraph graph = dataConverter.createGraphFromEmailLists(lstMails);
			List<MyGraph> lstGraph = dataConverter.splitConnectedComponentsIntoGraph(graph);

			FileSinkImages pic = new FileSinkImages(OutputType.PNG, Resolutions.VGA);
			pic.setLayoutPolicy(LayoutPolicy.COMPUTED_FULLY_AT_NEW_IMAGE);

			int i = 0;
			pic.writeAll(graph, "E:\\tmp\\dataset\\graph_d" + duration + "_group_v" + graph.getNodeCount() + "_e"
					+ graph.getEdgeCount() + ".png");

			for (MyGraph g : lstGraph) {
				pic.writeAll(g, "E:\\tmp\\dataset\\graph_d" + duration + "_i" + (++i) + "_v" + g.getNodeCount() + "_e"
						+ g.getEdgeCount() + ".png");
			}
		}
	}
}
