package com.pussycat.minions;

import android.util.Log;

public class EndlessQueue<T> {

	private T[] queue;
	private int begin;
	private int next;

	final int MINIMUM_QUEUE_LENGTH = 4;
	
	public EndlessQueue(T[] queue) {	
		
		if( !isValidQueueLength(queue.length) ) {
			Log.e("Android", "FATAL ERROR: Size of EndlessQueue is too small.");
		} 
		
		this.queue = queue;
		begin = 0;
		next = begin;
	}
	
	
	public boolean isValidQueueLength(final int length) {
		return length >= MINIMUM_QUEUE_LENGTH;
	}
	
	
	private int increment(int index) {
		return (++index) % (queue.length - 1);
	}
	
	
	private int decrement(int index) {		
		return (queue.length + --index) % queue.length;
	}
	
	
	private int getNextIndex() {
		if(begin == next) {
			// The queue is empty
			next = increment(next);
			return begin;
		}
		
		int nextIndex = next;
		next = increment(next);
		
		if(next == begin) {
			begin = increment(begin);
		} 
		
		return nextIndex;
	}
	
	
	private int getFirstIndex() {
		if(begin == next) {		
			return -1; // the queue is empty
		}
		
		int firstIndex = begin;
		begin = increment(begin);
		
		return firstIndex;
	}
	
	
	private int getLastIndex() {
		if(begin == next) {
			return -1; // the queue is empty
		}
		
		int lastIndex = decrement(next);
		next = lastIndex;
		
		return lastIndex;
	}
	
	
	public void add(T elementToAdd) {
		synchronized(this) {
			int index = getNextIndex();
		
			queue[index] = elementToAdd;
		}
	}
	
	
	public T popFront() {
		synchronized(this) {
			int index = getFirstIndex();
			
			if(index == -1) {
				return null; // The queue is empty
			}
			
			return queue[index];
		}
	}
	
	
	public T popEnd() {
		synchronized(this) {
			int index = getLastIndex();
			
			if(index == -1) {
				return null; // The queue is empty
			}
			
			return queue[index];
		}
	}
	
	
	public void clear() {
		begin = 0;
		next = begin;
	}
	
	
}
