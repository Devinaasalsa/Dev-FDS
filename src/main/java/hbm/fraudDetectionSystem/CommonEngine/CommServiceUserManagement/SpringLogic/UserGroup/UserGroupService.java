package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup;


import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupExistException;
import hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.exception.domain.UserGroupNotFoundException;

import java.util.List;
import java.util.Map;

public interface UserGroupService {
    UserGroup addUserGroup(String groupName) throws UserGroupNotFoundException, UserGroupExistException;
    List<UserGroup> getUserGroups();
    UserGroup updateUserGroup(String currentGroupName, String newGroupName) throws UserGroupNotFoundException, UserGroupExistException;
    void deleteUserGroup(long id);
    UserGroup findUserGroupByGroupName(String groupName);
    UserGroup findUserGroupById(long id);
    List<UserGroup> search(Map<String, Object> reqBody);
}
