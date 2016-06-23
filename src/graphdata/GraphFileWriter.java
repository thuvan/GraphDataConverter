package graphdata;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.graphstream.graph.Edge;

public class GraphFileWriter implements IGraphWriter,Closeable {
	String filePath;
	FileWriter writer ;
	public GraphFileWriter(String filePath,boolean append) {
		this.filePath = filePath;
		try {
			writer = new FileWriter(filePath,append);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void write(MyGraph graph) throws IOException {
		writer.write("t # "+graph.getGraphId()+"\n");
		HashMap<String, Integer> nodeIndex = new HashMap<>();
		for(int i=0;i<graph.getNodeCount();i++){
			String nodeID = graph.getNode(i).getId();
			if (nodeIndex.containsKey(nodeID))
				throw new RuntimeException("Dublicate nodeId '"+nodeID+"'");
			nodeIndex.put(nodeID,i);			
		}
		
		for(int i=0;i<graph.getNodeCount();i++){
			String nodeLabel= graph.getNode(i).getId();			
			writeLine("v "+i+" "+nodeLabel);			
		}
		
		for(int i=0;i<graph.getEdgeCount();i++){
			Edge e = graph.getEdge(i);
			int sID = nodeIndex.get(e.getSourceNode().getId());
			int tID = nodeIndex.get(e.getTargetNode().getId());
			String label = "1";
			writeLine("e "+sID+" "+tID+" "+ label);			
		}
	}

	private void writeLine(String str) throws IOException{
		writer.write(str+"\n");
	}
	@Override
	public void close() throws IOException {
		if (writer!=null)
			writer.close();
		writer = null;
	}

}
