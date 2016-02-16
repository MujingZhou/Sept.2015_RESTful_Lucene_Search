package model;

public class SearchPublicationYear {
	private int lowerLeft;
	private int lowerRight;
	private int upperLeft;
	private int upperRight;
	
	public SearchPublicationYear(int lowerLeft, int lowerRight, int upperLeft, int upperRight) {
		this.lowerLeft = lowerLeft;
		this.lowerRight = lowerRight;
		this.upperLeft = upperLeft;
		this.upperRight = upperRight;
	}
	
	public int getLowerLeft(){
		return this.lowerLeft;
	}
	
	public int getUpperLeft(){
		return this.upperLeft;
	}
}
