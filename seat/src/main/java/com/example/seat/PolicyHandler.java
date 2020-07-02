package com.example.seat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler {

	@Autowired
	SeatRepository seatRepository;
	
	@StreamListener(Processor.INPUT)
	public void onEventByString(@Payload PaymentPlaced paymentPlaced) {
		if(paymentPlaced.getEventType().contentEquals("PaymentPlaced")) {
			
			System.out.println("===============");
			System.out.println("자리 예약 및 결제 완료");

			Seat seat = new Seat();
			seat.setSeatId(paymentPlaced.getSeatId());
			seat.setStartTime(paymentPlaced.getStartTime());
			seat.setUsages(paymentPlaced.getUsages());
			seat.setOccupied(true);
			seatRepository.save(seat);

			System.out.println("===============");

		
		}
	}
}
