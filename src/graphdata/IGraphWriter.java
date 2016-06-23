package graphdata;

import java.io.Closeable;
import java.io.IOException;

public interface IGraphWriter extends Closeable {
	void write(MyGraph graph) throws IOException;
}
