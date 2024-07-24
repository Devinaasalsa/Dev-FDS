package hbm.fraudDetectionSystem.CommonEngine.CommServiceUserManagement.SpringLogic.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    User findAllById(long id);

    User findUserByUsername(String username);

    User findUserByEmail(String email);

    @Query(
            nativeQuery = true,
            value = "select * from t_user_config where type_id != 3 order by id asc"
    )
    List<User> findByOrderByIdAsc();

    List<User> findAllByUsernameNotIn(List<String> username);
}
