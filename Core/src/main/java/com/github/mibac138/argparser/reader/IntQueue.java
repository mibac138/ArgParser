/*
 * Copyright (c) 2017 Michał Bączkowski
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.mibac138.argparser.reader;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mibac138 on 05-04-2017.
 */
class IntQueue {
	@NotNull
	private List<Integer> list = new ArrayList<Integer>(4);
	
	public int size() {
		return list.size();
	}
	
	public boolean add(int integer) {
		return list.add(integer);
	}
	
	@NotNull
	public Integer remove() {
		return list.remove(getHead());
	}
	
	@Nullable
	public Integer poll() {
		return list.isEmpty() ? null : list.remove(getHead());
	}
	
	public void shift(int amount) {
		for (int i = 0; i < list.size(); i++) {
			list.set(i, list.get(i) - amount);
		}
	}
	
	private int getHead() {
		return list.size() - 1;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		
		IntQueue intQueue = (IntQueue) o;
		
		return list.equals(intQueue.list);
	}
	
	@Override
	public int hashCode() {
		return list.hashCode();
	}
	
	@Override
	public String toString() {
		return list.toString();
	}
}
