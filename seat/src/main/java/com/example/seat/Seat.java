package com.example.seat;

import java.util.Date;
import java.util.Optional;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.Table;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Entity
@Table(name="seat_table")
public class Seat {

	@Id
    int seatId;
    Date startTime;
    int usages;
    boolean occupied;
    
   // 사용 중지 버튼을 클릭
    @PostUpdate  
    public void useStopped() {
    	System.out.println("이벤트탈까요 안탈까요");
    	
    	if(this.isOccupied() == false) {
    		// 사용 중지 버튼 클릭 event 발생
    		// this 좌석을 데이터 테이블에서 삭제하도록...
			SeatRepository seatRepository = SeatApplication.applicationContext.getBean(SeatRepository.class);
	    	seatRepository.deleteById(this.getSeatId());

    		System.out.println("사용 중지 버튼 클릭 event 발생");
    		// this 좌석을 삭제하는 않게 : original
//    		SeatFinished seatFinished = new SeatFinished();
//
//    		seatFinished.setSeatId(this.getSeatId());
//    		seatFinished.setUsages(0);				// 초기화
//    		seatFinished.setStartTime(null);		// 초기화
    		
    		
    		// SeatFinished 발생하지 않도록 함
//    		ObjectMapper objectMapper = new ObjectMapper();
//    		String json = null;
//    		
//    		try {
//    			json = objectMapper.writeValueAsString(seatFinished);
//    		} catch (JsonProcessingException e) {
//    			throw new RuntimeException("JSON format exception", e);
//    		}
//    		
//    		Processor processor = SeatApplication.applicationContext.getBean(Processor.class);
//    		MessageChannel outputChannel = processor.output();
//    		
//    		outputChannel.send(MessageBuilder
//    				.withPayload(json)
//    				.setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
//    				.build());
//    		
//    		System.out.print("Seat class:"+json);

    	}else {
    		System.out.println("좌석 occupied 값"+this.isOccupied());
    	}
  
    }

    // 각 seat의 time expire 여부 체크
    public void checkTimer() {
    	
    	if(isOccupied() == false) {
    		return;
    	}

		Date currentTime = new Date();
		long diff = currentTime.getTime() - getStartTime().getTime();
		int min = (int)(diff / 60000); // 몇 분이 지났는가
//		int min = (int)(diff / 1000); // 몇 초 지났는가 : 시험용
		System.out.println("====="+min+"분 지남");
		
		
		// 사용시간 만료됨
		if(min >= getUsages()) {
			System.out.println("====="+min+" 지남"+"사용시간 "+getUsages());
			SeatRepository seatRepository = SeatApplication.applicationContext.getBean(SeatRepository.class);
			
	    	seatRepository.deleteById(this.getSeatId());
		}
     	
    }
    
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
	public int getUsages() {
		return usages;
	}

	public void setUsages(int usages) {
		this.usages = usages;
	}
    

	
}
