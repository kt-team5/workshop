# 개요 - PC방 서비스 개발
본 시스템은 소상공인의 대표적인 사업 업종 중 하나인 PC방 좌석 관리 서비스 개발을 위해 작성되었습니다.

![pc bang](https://user-images.githubusercontent.com/63759241/86201907-c2c16280-bb9b-11ea-8c1f-7ba12cf467d1.jpg)

현 시스템은 기존 업주들이 개별적으로 각 PC방의 좌석 및 사용량 관리를 위해 사용하던 POS의 대체 시스템을 개발을 최종 목표로 합니다.

# Table of contents

- [서비스 시나리오](#서비스-시나리오)
  - [요구사항 구현 결과](#요구사항-구현-결과)
  - [설계](#설계)
  - [구현](#구현)
    - [DDD 의 적용](#ddd-의-적용)
    - [폴리글랏 프로그래밍](#폴리글랏-프로그래밍)
    - [Gateway 적용](#Gateway-적용)
    - [UI 구현](#UI-구현)
  - [운영](#운영)
    - [CI/CD 설정](#cicd설정)
    - [동기식 호출 / 서킷 브레이킹 / 장애격리](#동기식-호출-서킷-브레이킹-장애격리)
    - [오토스케일 아웃](#오토스케일-아웃)
    - [무정지 재배포](#무정지-재배포)



## 서비스 시나리오

기능적 요구사항
1. 고객은 잔여 좌석을 바탕으로 원하는 좌석과 원하는 사용시간을 선택한다.
1. 고객이 결제한다.
1. 결제가 완료되면 선택이 Confirm되고 해당 좌석으로 정보가 기록된다.
1. 해당 정보에 대한 기록을 바탕으로 좌석은 사용시간 만큼 점유되고 기록된다.
1. 고객은 사용이 종료되면 사용종료 버튼을 클릭하여 사용을 종료할 수 있다.
1. 사용 종료 시 해당 좌석은 사용 가능 좌석으로 변경된다.
1. PC방 사장님은 언제든지 사용가능 좌석과 사용중인 좌석에 대한 조회를 할 수 있다.

비기능적 요구사항
1. 트랜잭션
    1. 결제가 되지 않은 예약건은 아예 거래가 성립되지 않아야 한다  Sync 호출
1. 장애격리
    1. 상점관리 기능이 수행되지 않더라도 주문은 365일 24시간 받을 수 있어야 한다  Async (event-driven), Eventual Consistency
    1. 결제시스템이 과중되면 사용자를 잠시동안 받지 않고 결제를 잠시후에 하도록 유도한다  Circuit breaker, fallback
    1. 빈 좌석이 있을 경우 고객이 해당 좌석을 점유하지 못하도록 하며 다시 선택하도록 변경
1. 성능
    1. 고객이 자주 상점관리에서 확인할 수 있는 배달상태를 주문시스템(프론트엔드)에서 확인할 수 있어야 한다  CQRS



# 설계
## DDD 설계를 위한 이벤트 스토밍 결과물
* 칠판을 활용한 결과물 :
![KakaoTalk_Photo_2020-07-01-11-43-03](https://user-images.githubusercontent.com/63759241/86197291-0ca44b80-bb90-11ea-899d-56c22a6e46ab.jpeg)

* MSAez Tool을 활용한 결과물 정리 :
### 이벤트 도출
<img width="666" alt="Screen Shot 2020-07-01 at 1 39 40 PM" src="https://user-images.githubusercontent.com/63759241/86207212-fb1b6d80-bba8-11ea-8b0e-ef5a3205f277.png">

### 엑터,커맨드 부착하여 읽기 좋게
<img width="706" alt="Screen Shot 2020-07-01 at 1 41 36 PM" src="https://user-images.githubusercontent.com/63759241/86207210-fb1b6d80-bba8-11ea-8594-7e11b968bd9d.png">

### 어그리게잇 묶기
<img width="784" alt="Screen Shot 2020-07-01 at 1 44 31 PM" src="https://user-images.githubusercontent.com/63759241/86207208-f9ea4080-bba8-11ea-8834-7627f8b81e36.png">

### 바운디드 컨텍스트로 묶기
<img width="948" alt="Screen Shot 2020-07-01 at 1 50 13 PM" src="https://user-images.githubusercontent.com/63759241/86207206-f9ea4080-bba8-11ea-98e2-9e9b1f9b752b.png">

### 폴리시 부착
<img width="928" alt="Screen Shot 2020-07-01 at 1 58 34 PM" src="https://user-images.githubusercontent.com/63759241/86207202-f951aa00-bba8-11ea-879a-87173f3e31ec.png">

### 폴리시 이동 및 정리
<img width="955" alt="Screen Shot 2020-07-01 at 2 02 06 PM" src="https://user-images.githubusercontent.com/63759241/86207200-f8b91380-bba8-11ea-99b0-df1730635cda.png">

### View 모델 추가 및 완성
![image](https://user-images.githubusercontent.com/63759370/86445194-ee377f00-bd4c-11ea-85ff-d58137c9020e.png)


## 헥사고날 아키텍처 다이어그램 도출
![image](https://user-images.githubusercontent.com/63759370/86440311-3488e000-bd45-11ea-8f03-f0b452c1a41f.png)



## 설계 대상
위의 설계를 통해 도출된 테이블은 총 세 개입니다.
예약 관리, 결제, 좌석의 세 가지 패키지로 구성된 서비스를 개발합니다.

아래의 내용을 통해 변수 정리와 각 패키지의 역할에 대해 정의합니다.


### 예약 및 좌석 관리 시스템
 - 변수 정리
   - 거래 ID : Long orderId;
   - 사용 좌석 : int seatId;
   - 시작 시간 : Date startTime;
   - 사용량 : int usages // PC방 사용량을 뜻하며 분 단위로 구성;
   - 사용 여부 : boolean occupied // True일 경우 사용중인 좌석;

 - 역할 정의
 


### 결제
 - 변수 정리
   - 결제 ID : long id;
   - 사용 좌석 : int seatId;
   - 사용량 : int usages;
   - 시작 시간 : Date startTime;
   - 사용 여부 :boolean occupied;
   - String eventType;

### 좌석
 - 변수 정리
   - 사용 좌석 : int seatId;
   - 사용량 : int usages;
   - 시작 시간 : Date startTime;
   - 사용 여부 :boolean occupied;
   - 남은 시간 : int times


## 요구사항 구현 결과



# 구현

분석/설계 단계에서 도출된 MSA는 총 3개로 아래와 같다. 

|MSA|기능|Port|조회 API|
|:---:|:---:|:--:|:---------------:|
|Order|PC방 좌석 예약 및 관리|8081|http://localhost:8081/orders|
|Payment|결제 관리|8086|http://localhost:8086/payments|
|Seat|PC방 각 좌석 관리|8082|http://localhost:8082/seats|

## DDD의 적용

- 각 서비스내에 도출된 핵심 Aggregate Root 객체를 Entity 로 선언하였다: (예시는 Seat 마이크로 서비스). 이때 가능한 현업에서 사용하는 언어 (유비쿼터스 랭귀지)를 그대로 사용하려고 노력했다. 하지만, 일부 구현에 있어서 영문이 아닌 경우는 실행이 불가능한 경우가 있기 때문에 계속 사용할 방법은 아닌것 같다. (Maven pom.xml, Kafka의 topic id, FeignClient 의 서비스 id 등은 한글로 식별자를 사용하는 경우 오류가 발생하는 것을 확인하였다)

```
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
		int min = (int)(diff / 60000);
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


```
- Entity Pattern 과 Repository Pattern 을 적용하여 JPA 를 통하여 다양한 데이터소스 유형 (RDB or NoSQL) 에 대한 별도의 처리가 없도록 데이터 접근 어댑터를 자동 생성하기 위하여 SpringData REST 의 RestRepository 를 적용하였다
```
package com.example.seat;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface SeatRepository extends PagingAndSortingRepository<Seat, Integer> {
}

```
- 적용 후 REST API 의 테스트

### order 서비스의 좌석 예약 처리
```
http localhost:8081/orders seatId=1 usages=100 occupied=true 
```
![image](https://user-images.githubusercontent.com/63759370/86439210-1b7f2f80-bd43-11ea-9eb6-53102cd78fbb.png)


### payment 서비스에서 결제 정보 확인
```
http localhost:8086/payments/2
```
![image](https://user-images.githubusercontent.com/63759370/86439361-68fb9c80-bd43-11ea-82f8-8a571ba41e5d.png)

### 좌석(좌석1번) 상태 확인
```
http localhost:8082/seats/1
```
![image](https://user-images.githubusercontent.com/63759370/86439482-99433b00-bd43-11ea-8f82-fd9a60a9f4ec.png)

### Web UI 상태 확인
```
http://localhost:8082/index.html
```
![image](https://user-images.githubusercontent.com/63759306/86443918-eaa2f880-bd4a-11ea-9111-99725218cd8a.png)


## 폴리글랏 프로그래밍

Seat 서비스만 Mysql을 사용하였다. pom.xml, application.yml 를 통해 데이터베이스 제품의 설정을 적용하였다.


- pom.xml에 mysql dependency 적용
```
 		<dependency>
			<groupId>mysql</groupId>
			<artifactId>mysql-connector-java</artifactId>
			<scope>runtime</scope>
		</dependency>
		
<!-- 			
		<dependency>
			<groupId>com.h2database</groupId>
			<artifactId>h2</artifactId>
			<scope>runtime</scope>
		</dependency>				
 -->		

```

- application.yml에 db 설정
```
spring:
  jpa:
    hibernate:
      naming:
        physical-strategy: org.hibernate.boot.model.naming.PhysicalNamingStrategyStandardImpl
      ddl-auto: update
    properties:
      hibernate:
        show_sql: true
        format_sql: true
        dialect: org.hibernate.dialect.MySQL57Dialect
  datasource:
    url: jdbc:mysql://localhost:3306/mysql?serverTimezone=Asia/Seoul
    username: root
    password: root12345
    driverClassName: com.mysql.cj.jdbc.Driver
```

- DB 연결
![image](https://user-images.githubusercontent.com/63759370/86432080-90e20480-bd31-11ea-9e7e-ccca6539270f.png)


## Gateway 적용
```
spring:
  profiles: default
  cloud:
    gateway:
      routes:
        - id: order
          uri: http://localhost:8081
          predicates:
            - Path=/orders/**
        - id: seat
          uri: http://localhost:8082
          predicates:
            - Path=/seats/**
        - id: payment
          uri: http://localhost:8086
          predicates:
            - Path=/payments/**

---

spring:
  profiles: docker
  cloud:
    gateway:
      routes:
        - id: order
          uri: http://order5:8080
          predicates:
            - Path=/orders/**
        - id: seat
          uri: http://seat5:8080
          predicates:
            - Path=/seats/ui/index.html
        - id: pay
          uri: http://payment5:8080
          predicates:
            - Path=/payments/**
```

## UI 구현
- order 서비스 : PC방 자리 예약 및 취소, 사용 가능 시간 표출
![image](https://user-images.githubusercontent.com/63759370/86432426-9d1a9180-bd32-11ea-97ac-020daaa39063.png)

- seat 서비스 : 각 PC방 자리 화면으로 현재 기준 사용 가능 시간 표출
![image](https://user-images.githubusercontent.com/63759370/86432460-c89d7c00-bd32-11ea-85ea-c3bfcc312650.png)






## 카프카 설정
- topic : pcroom
- group :
 예약 : order
 결제 : payment
 좌석 : seat





# 운영

## CI/CD 설정
각 구현체들은 각자의 source repository 에 구성되었고, pipeline build script 는 각 프로젝트 폴더 이하에 cloudbuild.yml 에 포함되었다.

