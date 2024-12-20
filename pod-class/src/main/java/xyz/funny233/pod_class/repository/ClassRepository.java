package xyz.funny233.pod_class.repository;



import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import xyz.funny233.pod_class.model.Class1Model;



@Repository
@Component
public interface ClassRepository extends JpaRepository<Class1Model, Long>{
    Class1Model findByName(String name);
    Class1Model[] findByTeacherId(int teacherId);
}
