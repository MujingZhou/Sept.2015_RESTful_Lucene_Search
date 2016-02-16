package model;

public class SearchResult {
	private int paperId;
	private String title;
	private int year;
	private String journalName;
	private int volume;
	private String page;
	
	public SearchResult(int paperId, String title,int year, String journalName, int volume, String page){
		this.paperId = paperId;
		this.title = title;
		this.year = year;
		this.journalName = journalName;
		this.volume = volume;
		this.page = page;
	}
	
	public int getPaperId(){
		return this.paperId;
	}
	
	public String getTitle(){
		return this.title;
	}
	
	public int getYear(){
		return this.year;
	}
	
	public String getJournalName(){
		return this.journalName;
	}
	
	public int getVolume(){
		return this.volume;
	}
	
	public String getPage(){
		return this.page;
	}
}
