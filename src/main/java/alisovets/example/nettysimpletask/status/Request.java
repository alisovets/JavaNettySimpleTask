package alisovets.example.nettysimpletask.status;

/**
 * To hold request pars to store
 */
public class Request {
	
	private long id;
	private String ip;
	private long requestTime;
	private String uri;
	private long closingTime;
	private int receivedBytes;
	private int sentBytes;
	
	public Request(){}
	
	/**
	 * 
	 * @param ip
	 * @param requestTime
	 * @param uri
	 * @param closingTime
	 * @param receivedBytes
	 * @param sentBytes
	 */
	public Request(String ip, long requestTime, String uri, long closingTime, int receivedBytes, int sentBytes){
		this.ip = ip;
		this.requestTime = requestTime;
		this.uri = uri;
		this.closingTime = closingTime;
		this.receivedBytes = receivedBytes;
		this.sentBytes = sentBytes;
	}

	public synchronized long getId() {
		return id;
	}

	public synchronized void setId(long id) {
		this.id = id;
	}

	public synchronized String getIp() {
		return ip;
	}

	public synchronized void setIp(String ip) {
		this.ip = ip;
	}

	public synchronized long getRequestTime() {
		return requestTime;
	}

	public synchronized void setRequestTime(long requestTime) {
		this.requestTime = requestTime;
	}

	public synchronized String getUri() {
		return uri;
	}

	public synchronized void setUri(String uri) {
		this.uri = uri;
	}

	public synchronized long getClosingTime() {
		return closingTime;
	}

	public synchronized void setClosingTime(long closingTime) {
		this.closingTime = closingTime;
	}

	public synchronized int getSentBytes() {
		return sentBytes;
	}

	public synchronized void setSentBytes(int sentBytes) {
		this.sentBytes = sentBytes;
	}

	public synchronized int getReceivedBytes() {
		return receivedBytes;
	}

	public synchronized void setReceivedBytes(int receivedBytes) {
		this.receivedBytes = receivedBytes;
	}
	
}
