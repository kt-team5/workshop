package kt.workshop.order;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@Service
public class SpringView {
	@Autowired
	OrderRepository orderRepository;
	
	@GetMapping("/getInfo")
    public String getSeatInfo() {		
		//System.out.println("======= getSeatInfo() ========");

		ObjectMapper mapper = new ObjectMapper();
	    String json = null;

	    try {
	        json = mapper.writeValueAsString(orderRepository.findAll());
	    } catch (JsonProcessingException e) {
	        throw new RuntimeException("JSON format exception", e);
	    }
	    
	    return json;
    }
	
	//@PostMapping("/setInfo")
	@GetMapping("/setInfo")
	public String setSeatInfo(String seatId,int usages,String occupied) {
		try {
			Order p = new Order();

	     	if(occupied.equals("true")) {
	     		System.out.println("1 ======== setSeatInfo() ======="+seatId+"/"+usages+"/"+occupied);
		    	p.setOccupied(true);
		    	p.setUsages(usages);
		    	p.setSeatId(Integer.parseInt(seatId));
		    	Date date = new Date();
		    	p.setStartTime(date);
		    	orderRepository.save(p);
		    	return "SUCCESS";
	     	}
	     	else {
	     		System.out.println("2 ======== setSeatInfo() ======="+seatId+"/"+usages+"/"+occupied);
	     		p.setOccupied(false);
		    	p.setUsages(0);
		    	p.setSeatId(Integer.parseInt(seatId));
		    	Date date = new Date();
		    	p.setStartTime(date);
		    	orderRepository.save(p);
		    	return "SUCCESS";
	     	}
     	}
		catch(Exception e) {
			e.printStackTrace(); 
			System.out.println("3 ======== setSeatInfo() ======="+seatId+"/"+usages+"/"+occupied);
			return "FAIL";
		}
		
	}
	 
}



//if(seatId != 0 ) {
//	// 주문 성공시 재고량 변경
//	Optional<Payment> paymentById = paymentRepository.findById(seatId);
//	Payment p = paymentById.get();
//	
//	System.out.println("======== start =======");
//	Payment payment = new Payment();
//	payment.setId(paymentById.());
//	p.setName(orderCancelled.getProductName());
//	p.setStock( p.getStock() + orderCancelled.getQty());
//	productRepository.save(p);
//	System.out.println("getUsages"+p.getUsages());
//	System.out.println("======= end ========");
//}
//SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d yyyy : HH:mm:ss:SSS z");
//Date now = new Date();
//String dateStr = dateFormat.format( now );
//return dateStr;


