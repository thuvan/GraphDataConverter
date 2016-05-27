package graphdata;

public interface IGraphRepository {
	MyGraph getGraphByID(int id);
	MyGraph addGraph(MyGraph g);
	void clear();
	
}
