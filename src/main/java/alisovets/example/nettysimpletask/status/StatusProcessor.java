package alisovets.example.nettysimpletask.status;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

/**
 * stored statuses and creates status response content
 */

public class StatusProcessor {
	private final static int LAST_REQUEST_NUMBER = 16;
	
	private static volatile long openConnectTime = 0;
	private static volatile int receiveByteCount = 0;
	private static volatile int openConnectCount = 0;
	private static volatile long totalConnectCount = 0;
	
	//stores one entry for each ip-uri pair
	private static Set<String> uniqueRequestSet = Collections.synchronizedSortedSet(new TreeSet<String>());
	
	//stores a count and a timestamp for each ip 
	private static Map<String, CountTimestamp> requestCountByIpMap = new TreeMap<String, CountTimestamp>();
	
	//stores a count for each redirect uri
	private static Map<String, Integer> redirectCountMap = new TreeMap<String, Integer>();
	
	//stores 16 last requests
	private static NullEndStorage<Request> lastRequests = new NullEndStorage<Request>(
			LAST_REQUEST_NUMBER);

	private Request request;
	
	public void fixOpenConnect(int receiveByte ){
		openConnectTime = new Date().getTime();
		synchronized (StatusProcessor.class) {
			openConnectCount++;
			totalConnectCount++;
		}

		receiveByteCount = receiveByte;
	}

	public void fixHttpOpenConnect(String ip, String uri) {
	
		String uniqueKey = ip + uri;
		uniqueRequestSet.add(uniqueKey);
		addRequestByIp(ip, openConnectTime);

		request = new Request(ip, openConnectTime, uri, 0, receiveByteCount, 0);
		lastRequests.add(request);

	}

	public void fixCloseConnect(String redirectUrl, int sentBytes) {
		synchronized (StatusProcessor.class) {
			openConnectCount--;
		}

		long timeStamp = new Date().getTime();
		addRedirect(redirectUrl);
		request.setSentBytes(sentBytes);
		request.setClosingTime(timeStamp);

	}

	private void addRequestByIp(String ip, long timestamp) {
		synchronized (requestCountByIpMap) {
			CountTimestamp countTimestamp = requestCountByIpMap.get(ip);
			if (countTimestamp == null) {
				countTimestamp = new CountTimestamp(1, timestamp);
				requestCountByIpMap.put(ip, countTimestamp);
			} else {
				countTimestamp.setCount(countTimestamp.getCount() + 1);
				countTimestamp.setTimestamp(timestamp);
			}
		}
	}

	private void addRedirect(String redirectUrl) {
		synchronized (redirectCountMap) {
			if (redirectUrl != null) {
				Integer count = redirectCountMap.get(redirectUrl);
				if (count == null) {
					redirectCountMap.put(redirectUrl, 1);
				} else {
					redirectCountMap.put(redirectUrl, count + 1);
				}
			}
		}
	}

	public static synchronized int getOpenConnectCount() {
		return openConnectCount;
	}

	public static synchronized long getTotalConnectCount() {

		return totalConnectCount;
	}

	/**
	 * creates html content for a status response
	 * return  string containing the html
	 */
	public static String createStatusReqponseContent() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("<html><body>");
		stringBuilder.append("the total number of requests: ")
				.append(getTotalConnectCount()).append("<br>");

		stringBuilder.append("the number of open requests: ")
				.append(getOpenConnectCount()).append("<br>");

		stringBuilder.append("the number of unique requests: ")
				.append(uniqueRequestSet.size()).append("<br>");

		addRequestCountByIpToResponseContent(stringBuilder);

		addRedirectCountToResponseContent(stringBuilder);

		addLastRequestsToResponseContent(stringBuilder);

		stringBuilder.append("</body></html>");

		return stringBuilder.toString();
	}

	private static void addRequestCountByIpToResponseContent(
			StringBuilder stringBuilder) {

		stringBuilder.append("<br>").append("request counts by ip: ")
				.append("<br>");

		stringBuilder.append("<table border=1>").append("<tr>").append("<th>")
				.append("ip").append("</th>").append("<th>").append("Count")
				.append("</th>").append("<th>").append("Count").append("</th>")
				.append("<tr>");
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy.MM.dd hh:mm:ss.S");

		synchronized (requestCountByIpMap) {

			for (String ip : requestCountByIpMap.keySet()) {
				CountTimestamp countTimeStamp = requestCountByIpMap.get(ip);
				stringBuilder
						.append("<tr>")
						.append("<td>")
						.append(ip)
						.append("</td>")
						.append("<td>")
						.append(countTimeStamp.getCount())
						.append("</td>")
						.append("<td>")
						.append(dateFormat.format(new Date(countTimeStamp
								.getTimestamp()))).append("</td>")
						.append("<tr>");
			}
		}
		stringBuilder.append("</table>");
	}

	private static void addRedirectCountToResponseContent(
			StringBuilder stringBuilder) {
		stringBuilder.append("<br>").append("redirect counts by URL: ");
		stringBuilder.append("<table border=1>").append("<tr>").append("<th>")
				.append("Url").append("</th>").append("<th>").append("Count")
				.append("</th>").append("<tr>");

		synchronized (redirectCountMap) {
			for (String url : redirectCountMap.keySet()) {
				stringBuilder.append("<tr>").append("<td>").append(url)
						.append("</td>").append("<td>")
						.append(redirectCountMap.get(url)).append("</td>")
						.append("<tr>");
			}
		}
		stringBuilder.append("</table>");
	}

	private static void addLastRequestsToResponseContent(
			StringBuilder stringBuilder) {

		stringBuilder.append("<table border=1>").append("<tr>").append("<th>")
				.append("Client ip").append("</th>").append("<th>")
				.append("Uri").append("</th>").append("<th>")
				.append("Timestamp").append("</th>").append("<th>")
				.append("Sent bytes").append("</th>").append("<th>")
				.append("Received bytes").append("</th>").append("<th>")
				.append("Speed bytes/s").append("</th>").append("<tr>");

		stringBuilder.append("<br>").append("the last requests: ");

		for (Request item : lastRequests.toArray(new Request[0])) {

			long time = item.getClosingTime() - item.getRequestTime();
			String speed;
			if(time == 0){
				speed = "infinity";
			}
			else{
				speed = "" + (item.getSentBytes() + item.getReceivedBytes()) * 1000 / time;
			}
					
			stringBuilder.append("<tr>").append("<td>").append(item.getIp())
					.append("</td>").append("<td>").append(item.getUri())
					.append("</td>").append("<td>")
					.append(item.getRequestTime()).append("</td>")
					.append("<td>").append(item.getSentBytes()).append("</td>")
					.append("<td>").append(item.getReceivedBytes())
					.append("</td>").append("<td>")
					.append(speed).append("</td>")
					.append("<tr>");
		}
		stringBuilder.append("</table>");
	}

}
