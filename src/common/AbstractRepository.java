package common;

import java.util.concurrent.atomic.AtomicInteger;

public class AbstractRepository {
	public static final int FIRST_ID= 0;
	private final AtomicInteger nextId = new AtomicInteger(FIRST_ID);
	
	/**
	 * This method delivers the next ID and increments the ID counter atomically.
	 * 
	 * @return The next ID.
	 */
	protected final int getAndIncrementNextId() {		
		return this.nextId.getAndIncrement();
	}
	public void reset(){
		nextId.set(FIRST_ID);
	}
}
