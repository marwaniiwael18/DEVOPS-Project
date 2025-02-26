package tn.esprit.spring;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")  // Use the "test" profile
class GestionStationSkiApplicationTests {

	@Test
	void contextLoads() {
		// This test verifies that the Spring application context loads successfully.
		// No additional assertions are needed as the test will fail if the context fails to load.
	}

}