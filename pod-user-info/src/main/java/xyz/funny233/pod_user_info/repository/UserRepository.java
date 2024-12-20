package xyz.funny233.pod_user_info.repository;

import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import xyz.funny233.pod_user_info.model.UserModel;

@Repository
public interface UserRepository extends JpaRepository<UserModel, Long>{
        
    UserModel findByUserId(int userId);
}
