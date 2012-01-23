package com.nachete.muestra;

import org.json.JSONObject;

import android.text.Html;

public class MensajeBean {
	private int status_id; 
	private String status_text;
	private String status_time_created;
	private String status_username;
	private String status_blog;
	public int getStatus_id() {
		return status_id;
	}
	public void setStatus_id(String status_id) {
		this.status_id = Integer.parseInt(status_id);
	}
	public String getStatus_text() {
		return status_text;
	}
	public void setStatus_text(String status_text) {
		this.status_text = status_text;
	}
	public String getStatus_time_created() {
		return status_time_created;
	}
	public void setStatus_time_created(String status_time_created) {
		this.status_time_created = status_time_created + "000";
	}
	public String getStatus_username() {
		return status_username;
	}
	public void setStatus_username(String status_username) {
		this.status_username = status_username;
	}
	//Para los blogs
	public String getStatus_blog() {
		return status_blog;
	}
	public void setStatus_blog(String status_blog) {
		this.status_blog = "BLOG" + Html.fromHtml(status_blog);
	}
}
