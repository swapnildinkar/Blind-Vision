package com.example.demo;

import com.example.demo.R;

public class Response {
	public boolean isUnderstood;
	public boolean isAnswered;
	public String result;
	public String status;
	
	public Response(boolean isUnderstood, boolean isAnswered, String result, String status) {
		this.isUnderstood = isUnderstood;
		this.isAnswered = isAnswered;
		this.result = result;
		this.status = status;
	}
	
	public boolean isUnderstood() {
		return this.isUnderstood;
	}
	
	public boolean isAnswered() {
		return this.isAnswered;
	}
	
	public String getResult() {
		return this.result;
	}
	
	public String getStatus() {
		return this.status;
	}
}
