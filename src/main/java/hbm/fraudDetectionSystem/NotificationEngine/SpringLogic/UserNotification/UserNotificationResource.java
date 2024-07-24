package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.UserNotification;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/userNotification")
public class UserNotificationResource extends ResponseResourceEntity<UserNotification> {
    @Autowired
    private UserNotificationService notificationService;

    @GetMapping("{userId}/list")
    public ResponseEntity<?>findAllNotification(@PathVariable("userId") long userId){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<UserNotification> userNotifications = notificationService.listCaseNotification(userId);
            httpStatus = HttpStatus.OK;
            httpMessage = "Notification Fetched Succesfully";
            return responseWithListData(httpStatus, httpMessage, userNotifications);
        }catch (Exception e) {
            httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }

    }
}
