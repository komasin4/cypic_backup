package com.komasin4.cypic.model;

public class Folder {
	private String id;
	private String name;
	private String depth;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDepth() {
		return depth;
	}
	public void setDepth(String depth) {
		this.depth = depth;
	}
	public Folder(String id, String name, String depth) {
		super();
		this.id = id;
		this.name = name;
		this.depth = depth;
	}
}
