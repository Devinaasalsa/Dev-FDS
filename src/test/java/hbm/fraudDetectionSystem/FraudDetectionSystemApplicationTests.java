package hbm.fraudDetectionSystem;

import hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification.EmailServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class FraudDetectionSystemApplicationTests {

	protected final EmailServiceImpl emailService;

	@Autowired
	FraudDetectionSystemApplicationTests(EmailServiceImpl emailService) {
		this.emailService = emailService;
	}

	@Test
	void contextLoads() {
		this.emailService.sendDefaultPass("apriyan.pranata@bankntbsyariah.co.id", "apriyan", "testPassword");
	}

	@Test
	void testEmail() {
		this.emailService.sendDefaultPass("chikko45putra@gmail.com", "apriyan", "testPassword");
	}
}
