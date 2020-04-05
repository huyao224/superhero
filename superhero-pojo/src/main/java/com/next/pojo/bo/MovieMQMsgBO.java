package com.next.pojo.bo;

public class MovieMQMsgBO {

	private String title;
	private String text;
	
	public MovieMQMsgBO() {
		super();
	}

	public MovieMQMsgBO(String title, String text) {
		super();
		this.title = title;
		this.text = text;
	}
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
}
