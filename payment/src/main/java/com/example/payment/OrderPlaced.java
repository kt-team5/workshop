package com.example.payment;


	public class OrderPlaced {
	String eventType;
    Long seatNum;
    Long usage;

    public OrderPlaced(){
        this.eventType = OrderPlaced.class.getSimpleName();
    }

	public String getEventType() {
		return eventType;
	}

	public void setEventType(String eventType) {
		this.eventType = eventType;
	}

	public Long getSeatNum() {
		return seatNum;
	}

	public void setSeatNum(Long seatNum) {
		this.seatNum = seatNum;
	}

	public Long getUsage() {
		return usage;
	}

	public void setUsage(Long usage) {
		this.usage = usage;
	}
    
    
    
}
