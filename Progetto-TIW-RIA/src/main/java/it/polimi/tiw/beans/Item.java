package it.polimi.tiw.beans;

public class Item {
	final public int id;
	final public Integer auctionId;
	final public String name;
	final public String descr;
	final public float price;
	
	public static void isValid(String name, String descr, String price) throws Exception {
		/// Some is null
		if(name == null || descr == null|| price == null ) 						throw new Exception("Fields can't be null");
		if(name.length() < 4 || name.length() >= 64) 							throw new Exception("Name format not valid");
		if(descr.length() < 0 || descr.length() >= 256)  						throw new Exception("Description format not valid");
		
		Float temp = Float.parseFloat(price);
		if(temp <= 0) 															throw new Exception("Price must be >= 0");
	}
	
	public Item(int id, int auctionId, String name, String descr, String price) throws Exception {
		
		Item.isValid(name, descr, price);

		this.id = id;
		this.name = name;
		this.descr = descr;
		this.price = Float.parseFloat(price);;
		if(auctionId == 0) this.auctionId = null; else this.auctionId = auctionId;
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public String getDescr() {
		return this.descr;
	}
	
	public float getPrice() {
		return this.price;
	}
	
}