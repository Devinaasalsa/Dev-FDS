package hbm.fraudDetectionSystem.NotificationEngine.SpringLogic.NotificationType;

import hbm.fraudDetectionSystem.GeneralComponent.Domain.ResponseResourceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping("/notificationType")
public class NotificationTypeResource extends ResponseResourceEntity<NotificationType> {

    private NotificationTypeService notificationTypeService;

    @Autowired
    public NotificationTypeResource(NotificationTypeService notificationTypeService) {
        this.notificationTypeService = notificationTypeService;
    }

    @GetMapping("/list")
    private ResponseEntity<?>getAllNotificationType(){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            List<NotificationType> notificationTypes = notificationTypeService.getNotificationType();
                httpStatus = OK;
                httpMessage = "Notification Type Fetched Successfully";
            return responseWithListData(httpStatus, httpMessage, notificationTypes);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return responseWithListData(httpStatus, httpMessage, new ArrayList<>());
        }
    }

    @PostMapping("/addNotificationType")
    public ResponseEntity<?> addNotificationType(@RequestBody NotificationType notificationType){
        HttpStatus httpStatus;
        String httpMessage;
        try {
            NotificationType notifType = notificationTypeService.addNotificationType(notificationType.getNotificationType());
            httpStatus = OK;
            httpMessage = "Notification Type add Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @PostMapping("/updateNotificationType")
    public ResponseEntity<?> updateNotifType(@RequestParam("currentId") long currentId,
                                             @RequestBody NotificationType notificationType) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            NotificationType updateNotif = notificationTypeService.updateNotificationType(currentId, notificationType.getNotificationType());
            httpStatus = OK;
            httpMessage = "Notification Type Updated Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?>deleteNotif(@PathVariable("id") long id) {
        HttpStatus httpStatus;
        String httpMessage;
        try {
            notificationTypeService.deleteNotification(id);
            httpStatus = OK;
            httpMessage = "Notification Type Deleted Successfully";
            return response(httpStatus, httpMessage);
        } catch (Exception e){
            httpStatus = INTERNAL_SERVER_ERROR;
            httpMessage = e.getMessage();
            return response(httpStatus, httpMessage);
        }
    }
}
