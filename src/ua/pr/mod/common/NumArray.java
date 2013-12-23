package ua.pr.mod.common;

import java.util.List;

public class NumArray {
	private int counter;
	private List<?> list;
	
	public NumArray () {
		
	}
	
	public NumArray (int c, List<?> list) {
		setCounter(c);
		setList(list);
	}
	
	public int getCounter() {
		return counter;
	}
	
	public void setCounter(int counter) {
		this.counter = counter;
	}
	
	public List<?> getList() {
		return list;
	}
	
	public void setList(List<?> list) {
		this.list = list;
	}
}
