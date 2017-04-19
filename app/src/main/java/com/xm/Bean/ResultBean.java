package com.xm.Bean;

import java.io.Serializable;

public class ResultBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8028912233485872621L;
	private int code;
	private Object data;
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	
}
