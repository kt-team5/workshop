package com.example.seat;

import java.util.Date;

public class PaymentPlaced {
	int seatId;
    int usage;
    Date startTime;
    boolean occupied;
    String eventType;

    public PaymentPlaced(){
        this.eventType = PaymentPlaced.class.getSimpleName();
    }

	public int getSeatId() {
		return seatId;
	}

	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}

	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	
}
