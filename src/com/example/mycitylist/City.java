package com.example.mycitylist;

public class City {
	private String name;
	private String pinyi;
	
	public City(String name,String pinyi){
		this.name = name;
		this.pinyi = pinyi;
	}
	
	public City(){
	
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name){
		this.name = name;
	}
	
	public String getPinyi() {
		return pinyi;
	}
	
	public void setPinyi(String pinyi) {
		this.pinyi = pinyi;
	}
	
	

}
