package com.example.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;

public class PolicyHandler {

	@Autowired
	SeatRepository seatRepository;
	
	@StreamListener(Processor.INPUT)
	public void onEventByString(@Payload PaymentPlaced paymentPlaced) {
		if(paymentPlaced.getEventType().contentEquals("PaymentPlaced")) {
			
			//Optional<Seat> seatById = seatRepository.findById(paymentPlaced.getSeatId());
			
			System.out.println("===============");
			System.out.println("자리 예약 및 결제 완료");
			
			Seat seat = new Seat();
			seat.setSeatId(paymentPlaced.getSeatId());
			seat.setStartTime(paymentPlaced.getStartTime());
			seat.setUsage(paymentPlaced.getUsage());
			seat.setOccupied(true);
			
			seatRepository.save(seat);
			System.out.println("===============");
			
		}
		
	}
}
