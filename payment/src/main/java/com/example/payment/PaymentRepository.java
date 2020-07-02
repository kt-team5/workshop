package com.example.payment;


import org.springframework.data.repository.PagingAndSortingRepository;

public interface PaymentRepository extends PagingAndSortingRepository<Payment, Long> {
	//List<Payment> findById(String name);
}
