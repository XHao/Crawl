package data;

import org.apache.solr.client.solrj.beans.Field;

public class SolrData {
	
	@Field("url")
	private String url;
	
	@Field("title")
	private String title;
	
	@Field("author")
	private String author;
	
	@Field("content")
	private String content;
	
	@Field("location")
	private String location;
	
	@Field("content_type")
	private String content_type;
	
	@Field("last_modified")
	private String last_modified;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getContent_type() {
		return content_type;
	}

	public void setContent_type(String content_type) {
		this.content_type = content_type;
	}

	public String getLast_modified() {
		return last_modified;
	}

	public void setLast_modified(String last_modified) {
		this.last_modified = last_modified;
	}
	
	
	
}
