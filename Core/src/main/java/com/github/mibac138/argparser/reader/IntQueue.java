package com.github.mibac138.argparser.reader;

import java.util.ArrayList;

/**
 * Created by mibac138 on 05-04-2017.
 */
class IntQueue {
	private ArrayList<Integer> list = new ArrayList<Integer>(4);
	
	public int size() {
		return list.size();
	}
	
	public boolean add(int integer) {
		return list.add(integer);
	}
	
	public Integer remove() {
		return list.remove(getHead());
	}
	
	public Integer poll() {
		return list.size() == 0 ? null : list.remove(getHead());
	}
	
	public void shift(int amount) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i) - amount);
		}
	}
	
	private int getHead() {
		return list.size() - 1;
	}
}
