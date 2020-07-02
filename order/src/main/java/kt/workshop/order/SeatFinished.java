package kt.workshop.order;

import java.util.Date;

public class SeatFinished {
	String eventType;
    Long id;
    int seatId;
    Date startTime;
    int usages;
    boolean occupied;
	
	public SeatFinished() {
		this.eventType = this.getClass().getSimpleName();
	}
	public String getEventType() {
		return eventType;
	}
	public void setEventType(String eventType) {
		this.eventType = eventType;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public int getSeatId() {
		return seatId;
	}
	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}
	public Date getStartTime() {
		return startTime;
	}
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	public int getUsages() {
		return usages;
	}
	public void setUsages(int usages) {
		this.usages = usages;
	}
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
}
