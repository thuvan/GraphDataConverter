package graphdata;

import java.util.Iterator;

public interface IGraphRepository {
	MyGraph getGraphByID(int id);
	MyGraph addGraph(MyGraph g);
	void clear();
	
	Iterator<MyGraph> getIterator();
	int getGraphCount();
}
