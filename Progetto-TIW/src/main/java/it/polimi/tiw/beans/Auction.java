package it.polimi.tiw.beans;

import java.time.LocalDateTime;
import java.util.ArrayList;

import it.polimi.tiw.generals.AuctionState;

public class Auction {
	private int id = 0;
	private AuctionState state;
	private float initPrice = 0;
	private int minIncr = 0;	
	private boolean finished;
	private LocalDateTime dateTime;
	private Offer lstOffer;
	private String username;
	private ArrayList<Item> items;
	
	public Auction(int id, float initPrice, int minIncr, boolean finished, LocalDateTime dateTime) {
		this.id = id;
		this.initPrice = initPrice;
		this.minIncr = minIncr;
		this.finished = finished;
		this.dateTime = dateTime;
		this.setState(AuctionState.NOT_FOUND);
	}
	
	public void addItem(Item item) {
		if(items == null) this.items = new ArrayList<Item>();
		items.add(item);
	}
	
	public int getId() {
		return this.id;
	}
	
	public float getInitPrice() {
		return this.initPrice;
	}
	
	public int getMinIncr() {
		return this.minIncr;
	}
	
	public void setLstOffer(Offer offer) {
		this.lstOffer = offer;
	}
	
	public LocalDateTime getDateTime() {
		return this.dateTime;
	}
	
	public Offer getLstOffer() {
		return this.lstOffer;
	}
	
	public void setItems(ArrayList<Item> items) {
		this.items = items;
	}
	
	public ArrayList<Item> getItems(){
		return this.items;
	}
	
	public boolean getFinished() {
		return this.finished;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public AuctionState getState() {
		return state;
	}

	public void setState(AuctionState state) {
		this.state = state;
	}
	
}