package xyz.funny233.pod_class.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.funny233.pod_class.model.Class1Model;
import xyz.funny233.pod_class.repository.ClassRepository;



@Component
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    public Class1Model[] getAllClass(){
        List<Class1Model> classList = classRepository.findAll();
        return classList.toArray(new Class1Model[classList.size()]);
    }

    public Class1Model getClassById(long id){
        var entity = classRepository.findById(id);
        System.out.println(entity);
        if (entity.isEmpty()) {
            return null;
        }
        return classRepository.findById(id).get();
    }

    public void addClass(Class1Model class1Model){
        classRepository.save(class1Model);
    }

    public void updateClass(Class1Model class1Model){
        classRepository.save(class1Model);
    }

    public void deleteClass(long id){
        classRepository.deleteById(id);
    }

    public Class1Model getClassByName(String name){
        return classRepository.findByName(name);
    }

    public Class1Model[] getClassByTeacherId(int teacherId){
        return classRepository.findByTeacherId(teacherId);
    }
}
