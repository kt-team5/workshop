package kt.workshop.order;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.Table;

import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHeaders;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

@Entity
@Table(name="order_table")
public class Order {

	@Id	@GeneratedValue
	Long orderId;
	int seatId;
	Date startTime;
	int usage;  //minutes
	boolean occupied;
	
	static int MAX_SEAT = 10;  // PC방 좌석 수
	
	
	// 해당 좌석이 있는 지 확인하고, availability를 확인한다. 
	// 해당 좌석 가능 여부를 리턴한다.
	@PrePersist
    private void orderCheck(){
        RestTemplate restTemplate = OrderApplication.applicationContext.getBean(RestTemplate.class);
        Environment env = OrderApplication.applicationContext.getEnvironment();

//        if( productId == null ){
//            throw new RuntimeException();
//        }
//
//         // 1. 주문에 대한 상품 조회 - API
//        String productUrl = env.getProperty("api.url.product") + "/products/" + productId;
//
//        ResponseEntity<String> productEntity = restTemplate.getForEntity(productUrl, String.class);
//        JsonParser parser = new JsonParser();
//        JsonObject jsonObject = parser.parse(productEntity.getBody()).getAsJsonObject();
//
//       if( jsonObject.get("stock").getAsInt() < getQty()){
//            throw new RuntimeException();
//        }
    }


	@PostPersist 
	public void eventPublish() {
		//결제 요청 post

        RestTemplate restTemplate = OrderApplication.applicationContext.getBean(RestTemplate.class);
        Environment env = OrderApplication.applicationContext.getEnvironment();
        
        String payUrl = env.getProperty("api.url.pay");
        Payment payment = new Payment();
        payment.setSeatNum(this.getSeatId());
        payment.setStartTime(this.getStartTime());
        payment.setUsage(this.getUsage());
        payment.setOccupied(true);
       
        //Res 가 필요한 경우 응답 받음
        //ResponseEntity<String> response = 
        restTemplate.postForEntity(payUrl, payment ,String.class);
//        if( response.getStatusCode() == HttpStatus.CREATED){
//
//        }
		
		
		
//		OrderPlaced orderPlaced = new OrderPlaced();
//		orderPlaced.setOrderId(this.getOrderId());
//		orderPlaced.setSeatId(this.getSeatId());
//		orderPlaced.setStart(this.getStart());
//		orderPlaced.setTime(this.getTime());
//		ObjectMapper objectMapper = new ObjectMapper();
//		String json = null;
//
//		try {
//			json = objectMapper.writeValueAsString(orderPlaced);
//		} catch (JsonProcessingException e) {
//			throw new RuntimeException("JSON format exception", e);
//		}
//		//System.out.println(json);
//		
//	    Processor processor = OrderApplication.applicationContext.getBean(Processor.class);
//	    MessageChannel outputChannel = processor.output();
//
//	    outputChannel.send(MessageBuilder
//	            .withPayload(json)
//	            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
//	            .build());
	}
	
	
//	@PostUpdate 
//	public void cancelOrder() {
//		
//		if(orderStatus != null && getOrderStatus().equals("cancel")) {
//			
//			//주문세부내용 조회
//			OrderRepository orderRepository = OrderApplication.applicationContext.getBean(OrderRepository.class);
//	
//			orderRepository.findById(this.getOrderId()).ifPresent(
//				order -> {
//						OrderCancelled orderCancelled = new OrderCancelled();
//						orderCancelled.setOrderId(order.getOrderId());
//						orderCancelled.setProductId(order.getProductId());
//						orderCancelled.setQty(order.getQty());
//						orderCancelled.setProductName(order.getProductName());
//						orderCancelled.setOrderStatus("cancel");
//						ObjectMapper objectMapper = new ObjectMapper();
//						String json = null;
//				
//						try {
//							json = objectMapper.writeValueAsString(orderCancelled);
//						} catch (JsonProcessingException e) {
//							throw new RuntimeException("JSON format exception", e);
//						}
//						//System.out.println(json);
//						
//					    Processor processor = OrderApplication.applicationContext.getBean(Processor.class);
//					    MessageChannel outputChannel = processor.output();
//				
//					    outputChannel.send(MessageBuilder
//					            .withPayload(json)
//					            .setHeader(MessageHeaders.CONTENT_TYPE, MimeTypeUtils.APPLICATION_JSON)
//					            .build());
//						}
//				);
//		}
//	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
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


	public boolean isOccupied() {
		return occupied;
	}


	public void setOccupied(boolean occupied) {
		this.occupied = occupied;
	}
	
}
