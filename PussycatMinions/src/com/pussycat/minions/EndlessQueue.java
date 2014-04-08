package com.pussycat.minions;

import android.util.Log;

public class EndlessQueue<T> {

	private T[] queue;
	private int begin;
	private int next;

	
	public EndlessQueue(T[] queue) {	
		
		if(queue.length < 3) {
			Log.e("Android", "FATAL ERROR: Size of EndlessQueue is too small.");
		}
		
		this.queue = queue;
		begin = 0;
		next = begin;
	}
	
	
	private int increment(int index) {
		
		// if  index < last_index
		// 		return next_index
		// else 
		//		// We are at the last index so
		//      return first_index
		
		/*
		if(index <  queue.length - 1) {
			return index + 1;
		} else {
			return 0;
		}
		*/
		
		return (++index)%(queue.length - 1);
		
	}
	
	
	private int decrement(int index) {
		
		// if  index > first_index
		// 		return previous_index
		// else
		//      // we are at the first index
		//      return last_index
		
		
		if(index > 0) {
			return index - 1;
		} else {
			return queue.length - 1;
		}
	
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
			// the queue is empty
			return -1;
		}
		
		int firstIndex = begin;
		
		begin = increment(begin);
		
		return firstIndex;
	}
	
	
	private int getLastIndex() {
		
		if(begin == next) {
			// the queue is empty
			return -1;
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
			
			if(index < 0) {
				// The queue is empty
				return null;
			}
			
			return queue[index];
		}
		
	}
	
	
	public T popEnd() {
		
		synchronized(this) {
			int index = getLastIndex();
			
			if(index < 0) {
				// The queue is empty
				return null;
			}
			
			return queue[index];
		}
		
	}
	
	
}
