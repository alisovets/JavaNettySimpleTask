package alisovets.example.nettysimpletask.status;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * The limit size storage. if a new item is added when the maximum number is reached, then the oldest element is removed
 * @param <E>
 */
public class NullEndStorage<E> {
	
	private ArrayBlockingQueue<E> queue;
	private int capacity;
	
	/**
	 * Create new storage with the given fixed capacity
	 * @param capacity
	 */
	public NullEndStorage(int capacity) {
		this.capacity = capacity;
		queue = new ArrayBlockingQueue<E>(capacity);
	}
	
	/**
	 * adds new element in storage. if a new item is added when the maximum number is reached, then the oldest element is removed
	 * @param element
	 */
	public synchronized void add(E element){
		if(queue.size() == capacity){
			queue.poll();
		}
		queue.add(element);
	}
	
	/**
	 *  Returns an array containing all of the elements in this storage
	 * @param array
	 * @return
	 */
	public synchronized E[] toArray(E[] array){ 
		return queue.toArray(array);
	} 
	
}
