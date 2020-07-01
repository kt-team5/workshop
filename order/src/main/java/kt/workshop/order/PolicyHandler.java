package kt.workshop.order;

import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Service
public class PolicyHandler {
	
	@StreamListener(Processor.INPUT)
	public void onSeatFinished(@Payload String seatFinished){
		if(seatFinished.contentEquals("seatFinished")) {
		System.out.println("========start=========");
		System.out.println(seatFinished);
		System.out.println("======== end =========");
		}

}
