package graphdata;

import java.util.Comparator;
import java.util.Hashtable;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import common.AbstractRepository;

public class GraphRepository extends AbstractRepository implements IGraphRepository {
	private final Map<Integer, MyGraph> graphById = new Hashtable<Integer,MyGraph>();	
	SortedSet<MyGraph> orderedGraphSet = new TreeSet<MyGraph>(new IdGraphComparator());

	class IdGraphComparator implements Comparator<MyGraph>{
		@Override
		public int compare(MyGraph g1, MyGraph g2) {
			return Integer.compare(g1.getGraphId(),g2.getGraphId());
		}		
	}
	
	@Override
	public MyGraph getGraphByID(int id) {
		return graphById.get(id);
	}

	@Override
	public MyGraph addGraph(MyGraph g) {
		int id = this.getAndIncrementNextId();
		g.setGraphId(id);
		graphById.put(g.getGraphId(), g);
		orderedGraphSet.add(g);
		return g;
	}

	@Override
	public void clear() {
		graphById.clear();
		orderedGraphSet.clear();
		super.reset();
	}
	
	
}
