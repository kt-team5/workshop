package kt.workshop.order;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;


@SpringBootApplication
@EnableBinding(Processor.class)
public class OrderApplication {
	public static ApplicationContext applicationContext;
	public static void main(String[] args) {
		applicationContext = SpringApplication.run(OrderApplication.class, args);
	}
	
	@StreamListener(Processor.INPUT)
	public void onEventByString(){
		
	}
	
	/**
	 * 1. RestTemplate 을 쓰려면 spring-boot-starter-web 디펜던시를 추가하고, 아래를 추가하는 방법이 있음 
	 */
	@Bean
	public RestTemplate restTemplate() {
	    return new RestTemplate();
	}

}
