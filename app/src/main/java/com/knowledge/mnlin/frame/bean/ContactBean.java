package com.knowledge.mnlin.frame.bean;


import org.litepal.crud.DataSupport;

/**
 *
 * Created by admin on 2017/5/4.
 *
 * 联系人对象
 */

public class ContactBean extends DataSupport {
	private long id;
	private String name;
	private String phone;
	private String address;
	private String mark;
	private long time;
	
	public ContactBean(){}
	
	public ContactBean(String name,String phone,String address,String mark,long time){
		this.name=name;
		this.phone=phone;
		this.address=address;
		this.mark=mark;
		this.time=time;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getMark() {
		return mark;
	}

	public void setMark(String mark) {
		this.mark = mark;
	}
}
