package com.wsclient;

public class Instance
{
	int instance;
	
	public Instance(int instance)
	{
		this.instance = instance;
	}
	
	public void inreament()
	{
		instance++;
	}
	
	public void dereament()
	{
		instance--;
	}
	
	public int get()
	{
		return instance;
	}
}