package com.oc.rapportmicroservice;

import com.oc.rapportmicroservice.controller.ReportController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class ReportMicroserviceApplicationTests {

	@Autowired
	private ReportController reportController;
	@Test
	void contextLoads() {
		assertThat(reportController).isNotNull();
	}

}
