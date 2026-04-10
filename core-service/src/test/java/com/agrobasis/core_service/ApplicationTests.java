package com.agrobasis.core_service;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@Disabled("Requires a real database connection that is not available in the unit test sandbox.")
class ApplicationTests {

	@Test
	void contextLoads() {
	}

}
