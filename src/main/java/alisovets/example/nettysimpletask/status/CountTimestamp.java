package alisovets.example.nettysimpletask.status;

/**
 * Wrapper class to store two long values - a count and a timestamp.
 */

public class CountTimestamp {
	private long count;
	private long timestamp;
	
	/**
	 * default constructor
	 */
	public CountTimestamp(){}
		
	/**
	 * creates new object 
	 * @param count
	 * @param timestamp
	 */
	public CountTimestamp(long count, long timestamp){
		this.count = count;
		this.timestamp = timestamp;		
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
}
