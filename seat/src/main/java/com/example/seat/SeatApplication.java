package com.example.seat;


import java.util.Iterator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.ApplicationContext;

@SpringBootApplication
@EnableBinding(Processor.class)
public class SeatApplication {

	public static ApplicationContext applicationContext;
	public static long CHECK_PERIOD = 60000; //60초
	
	public static void main(String[] args) {
		applicationContext = SpringApplication.run(SeatApplication.class, args);
		
		
		Thread task = new Thread(new Runnable() {
			@Override
			public void run() {
				while(true) {
					//System.out.println("======= Thread.run()");
					// 각 seat의 time expire 여부 체크
					SeatRepository seatRepository = applicationContext.getBean(SeatRepository.class);
					
					Iterable<Seat> seatList = seatRepository.findAll();
					for (Iterator<Seat> i = seatList.iterator(); i.hasNext(); ) {
					    Seat seat = (Seat) i.next();
					    
					    seat.checkTimer();
					}
					try {
						Thread.sleep(CHECK_PERIOD);
					} catch (Exception e) {}
				}
			}
		});
	
		task.start();
	}

	
	@StreamListener(Processor.INPUT)
	public void onEventByString(){
		
	}
}
