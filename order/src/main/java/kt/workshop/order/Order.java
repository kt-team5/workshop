package kt.workshop.order;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
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
	
	Order() {
		startTime = new Date();
	}
	
	// 해당 좌석이 있는 지 확인하고, availability를 확인 및 처리한다. 
	@PrePersist
    private void orderCheck(){
		
		SimpleDateFormat format1 = new SimpleDateFormat ( "yyyy-MM-dd HH:mm:ss");
		System.out.println("========="+format1.format(startTime));
		
        RestTemplate restTemplate = OrderApplication.applicationContext.getBean(RestTemplate.class);
        Environment env = OrderApplication.applicationContext.getEnvironment();

        if( seatId < 1 || seatId > MAX_SEAT ){
            throw new RuntimeException("선택한 좌석은 없는 좌석입니다.");
        }

        // 1. 주문에 대한 해당 좌석 조회 - API
        String seatUrl = env.getProperty("api.url.seat") + "/seats/" + seatId;
        boolean available = true;
        try {
	        URL url = new URL(seatUrl);
	        URLConnection con = url.openConnection();
	        HttpURLConnection exitCode = (HttpURLConnection)con;
	        System.out.println("========= exitCode.getResponseCode() "+exitCode.getResponseCode());
	        if(exitCode.getResponseCode() == 200) {
	        	
			    ResponseEntity<String> seatEntity = restTemplate.getForEntity(seatUrl, String.class);
			    JsonParser parser = new JsonParser();
			    JsonObject jsonObject = parser.parse(seatEntity.getBody()).getAsJsonObject();
			    
				if( jsonObject.get("occupied").getAsBoolean() == true ){
					available = false;
				}
	        }

        } catch (Exception e){
          	e.printStackTrace(); 
            //throw e; //최상위 클래스가 아니라면 무조건 던져주자
        } finally {
	        if(available == false)
	        	throw new RuntimeException("해당 좌석은 사용 중입니다.");
        }
    }


	@PostPersist 
	public void postToPay() {
		//결제 요청 post
		System.out.println("=================test");
        RestTemplate restTemplate = OrderApplication.applicationContext.getBean(RestTemplate.class);
        Environment env = OrderApplication.applicationContext.getEnvironment();
        
        String payUrl = env.getProperty("api.url.pay");
        Payment payment = new Payment();
        payment.setSeatId(this.getSeatId());
        //payment.setStartTime(this.getStartTime());
        payment.setStartTime(new Date());
        payment.setUsage(this.getUsage());
        payment.setOccupied(true);
       
        //Res 가 필요한 경우 응답 받음
        //ResponseEntity<String> response = 
        restTemplate.postForEntity(payUrl, payment ,String.class);
//        if( response.getStatusCode() == HttpStatus.CREATED){
//
//        }
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

