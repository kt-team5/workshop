package com.example.seat;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
public class Seat {

//	@Id @GeneratedValue
//    Long id;
    int seatId;
    Date startTime;
    int usage;
    boolean occupied;
    
    
   // 사용 중지 버튼을 클릭
    @PostPersist  
    public void useStopped() {
    	
    	System.out.println("사용 중지 버튼 클릭 event 발생");
    	
    	SeatFinished seatFinished = new SeatFinished();
//    	seatFinished.setId(this.getId());
    	seatFinished.setSeatId(this.getSeatId());
    	seatFinished.setOccupied(this.isOccupied());
    	    	
    	ObjectMapper objectMapper = new ObjectMapper();
    	String json = null;
    	
        try {
            json = objectMapper.writeValueAsString(seatFinished);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON format exception", e);
        }

        Processor processor = SeatApplication.applicationContext.getBean(Processor.class);
        MessageChannel outputChannel = processor.output();

        outputChannel.send(MessageBuilder
                .withPayload(json)
                .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
                .build());
    
    }


//	public Long getId() {
//		return id;
//	}
//	public void setId(Long id) {
//		this.id = id;
//	}
	public boolean isOccupied() {
		return occupied;
	}
	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
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
	public int getUsage() {
		return usage;
	}

	public void setUsage(int usage) {
		this.usage = usage;
	}
    

	
}
