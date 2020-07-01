package com.example.payment;

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
	Long id;
	String eventType;
    Long seatNum;
    Long usage;
	
	@PostPersist @PostUpdate
	public void changeProduct() {
		PaymentPlaced paymentPlaced = new PaymentPlaced();
		paymentPlaced.setSeatNum(this.getSeatNum());
		paymentPlaced.setUsage(this.getUsage());
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
