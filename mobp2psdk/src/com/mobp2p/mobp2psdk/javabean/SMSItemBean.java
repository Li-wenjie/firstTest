package com.mobp2p.mobp2psdk.javabean;

public class SMSItemBean {
    private String phonenumber = null;
    private String content = null;
    private String time = null;
    private int type = -1;
    public SMSItemBean(String phonenumber, String content, String time, int type) {
        super();
        this.phonenumber = phonenumber;
        this.content = content;
        this.time = time;
        this.type = type;
    }
    
    public String getPhonenumber() {
		return phonenumber;
	}
	public void setPhonenumber(String phonenumber) {
		this.phonenumber = phonenumber;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
}
