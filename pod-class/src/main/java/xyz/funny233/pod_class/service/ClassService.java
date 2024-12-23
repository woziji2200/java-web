package xyz.funny233.pod_class.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.funny233.pod_class.gateway.RPC;
import xyz.funny233.pod_class.gateway.RPC.User;
import xyz.funny233.pod_class.model.Class1Model;
import xyz.funny233.pod_class.repository.ClassRepository;

@Component
public class ClassService {
    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private RPC rpc;

    public Class1Model[] getAllClass() {
        try {
            List<Class1Model> classList = classRepository.findAll();
            User users[] = rpc.fetchUsers();
            class classList2 extends Class1Model{
                public String teacherName;
            }
            for (int i = 0; i < classList.size(); i++) {
                for (int j = 0; j < users.length; j++) {
                    // System.out.println(classList.get(i).getTeacherId() + " " + users[j].id);
                    if (classList.get(i).getTeacherId() == users[j].id) {
                        classList2 temp = new classList2();
                        temp.setId(classList.get(i).getId());
                        temp.setName(classList.get(i).getName());
                        temp.setTeacherId(classList.get(i).getTeacherId());
                        temp.setCredit(classList.get(i).getCredit());
                        temp.teacherName = users[j].nickname;
                        classList.set(i, temp);
                    }
                }
            }

            return classList.toArray(new Class1Model[classList.size()]);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public Class1Model getClassById(long id) {
        var entity = classRepository.findById(id);
        System.out.println(entity);
        if (entity.isEmpty()) {
            return null;
        }
        return classRepository.findById(id).get();
    }

    public void addClass(Class1Model class1Model) {
        classRepository.save(class1Model);
    }

    public void updateClass(Class1Model class1Model) {
        classRepository.save(class1Model);
    }

    public void deleteClass(long id) {
        classRepository.deleteById(id);
    }

    public Class1Model getClassByName(String name) {
        return classRepository.findByName(name);
    }

    public Class1Model[] getClassByTeacherId(int teacherId) {
        return classRepository.findByTeacherId(teacherId);
    }
}
