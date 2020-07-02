package com.example.seat;


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
	
	public static void main(String[] args) {
		applicationContext = SpringApplication.run(SeatApplication.class, args);
	}

	
	@StreamListener(Processor.INPUT)
	public void onEventByString(){
		
	}
}