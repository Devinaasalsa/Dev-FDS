package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.EmailNotification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.mail.MessagingException;

@RestController
@RequestMapping("/email")
public class EmailResource {
    @Autowired
    private EmailService emailService;

    @GetMapping("/sendEmail")
    public ResponseEntity<?>runEmail() throws MessagingException {
        return new ResponseEntity<>("Send Email", HttpStatus.OK);
    }
}
