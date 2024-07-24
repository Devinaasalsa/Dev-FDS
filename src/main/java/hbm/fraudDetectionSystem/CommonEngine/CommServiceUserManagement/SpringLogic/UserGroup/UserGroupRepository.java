package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.UserGroup;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    UserGroup findUserGroupByGroupName(String groupName);

    UserGroup findUserGroupById(long id);

    List<UserGroup> findByOrderByIdAsc();
}
