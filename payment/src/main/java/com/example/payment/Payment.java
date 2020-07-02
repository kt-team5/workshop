package com.example.payment;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;

import org.springframework.util.MimeTypeUtils;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Payment {
	@Id @GeneratedValue
	private long id;
	private int seatId;
	private int usages;
	private Date startTime;
	private boolean occupied;
	private String eventType;
	
	@PostPersist @PostUpdate
	public void changeProduct() {
		PaymentPlaced paymentPlaced = new PaymentPlaced();
		paymentPlaced.setSeatId(this.getSeatId());
		paymentPlaced.setUsages(this.getUsages());
		paymentPlaced.setStartTime(this.getStartTime());
		paymentPlaced.setOccupied(this.isOccupied());
	    ObjectMapper objectMapper = new ObjectMapper();
	    String json = null;

	    try {
	        json = objectMapper.writeValueAsString(paymentPlaced);
	    } catch (JsonProcessingException e) {
	        throw new RuntimeException("JSON format exception", e);
	    }
	    
	    Processor processor = PaymentApplication.applicationContext.getBean(Processor.class);
	    MessageChannel outputChannel = processor.output();

	    outputChannel.send(MessageBuilder
	            .withPayload(json)
	            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
	            .build());
	    
	    System.out.print("Payment class:"+json);
	}

	public boolean isOccupied() {
		return occupied;
	}

	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getSeatId() {
		return seatId;
	}

	public void setSeatId(int seatId) {
		this.seatId = seatId;
	}

	public int getUsages() {
		return usages;
	}

	public void setUsages(int usages) {
		this.usages = usages;
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


	
	
	
}
