package graphdata;

import org.graphstream.graph.implementations.SingleGraph;

public class MyGraph extends SingleGraph {
	static final String ATTRIBUTE_GRAPH_ID = "GRAPH_ID";

	public MyGraph(int id) {
		super(id+"");
		this.setStrict(false);
		this.setAutoCreate(true);
		this.setGraphId(id);
	}	
	public MyGraph(){
		this(-1);
	}
	
	public int getGraphId(){
		return this.getAttribute(ATTRIBUTE_GRAPH_ID);
	}

	public void setGraphId(int id) {
		this.setAttribute(ATTRIBUTE_GRAPH_ID,id);
	}
}
