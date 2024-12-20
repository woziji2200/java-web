package xyz.funny233.pod_class.service;

import java.lang.classfile.ClassModel;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import xyz.funny233.pod_class.gateway.RPC;
import xyz.funny233.pod_class.gateway.RPC.User;
import xyz.funny233.pod_class.model.Class1Model;
import xyz.funny233.pod_class.model.ClassChooseModel;
import xyz.funny233.pod_class.repository.ClassChooseRepository;
import xyz.funny233.pod_class.repository.ClassRepository;

@Component
public class ClassChooseService {
    @Autowired
    private ClassChooseRepository classChooseRepository;

    @Autowired
    private ClassRepository classRepository;

    @Autowired
    private RPC rpc;

    public class ClassChooseWithClassInfo extends ClassChooseModel {
        public String name;
        public int teacherId;
    }

    public ClassChooseWithClassInfo[] getChoosedClass(int userId) {
        // return classChooseRepository.findByStudentId(userId);
        ClassChooseModel[] models = classChooseRepository.findByStudentId(userId);
        ClassChooseWithClassInfo[] result = new ClassChooseWithClassInfo[models.length];
        for(int i = 0; i < models.length; i++) {
            result[i] = new ClassChooseWithClassInfo();
            result[i].setClassId(models[i].getClassId());
            result[i].setStudentId(models[i].getStudentId());
            Optional<xyz.funny233.pod_class.model.Class1Model> classInfo = classRepository.findById((long)models[i].getClassId());
            if(classInfo.isPresent()) {
                result[i].name = classInfo.get().getName();
                result[i].teacherId = classInfo.get().getTeacherId();
            }
        }
        return result;
    }

    public String chooseClass(int userId, int classId) {

        if(classChooseRepository.findByStudentIdAndClassId(userId, classId) != null) {
            return "课程已选";
        }
        // System.out.println((long)classId);
        if(!classRepository.findById((long)classId).isPresent()) {
            return "不存在的课程";
        }
        ClassChooseModel model = new ClassChooseModel();
        model.setStudentId(userId);
        model.setClassId(classId);
        classChooseRepository.save(model);
        return "success";
    }


    public String cancelClass(int userId, int classId) {
        ClassChooseModel model = classChooseRepository.findByStudentIdAndClassId(userId, classId);
        if(model == null) {
            return "未选课";
        }
        classChooseRepository.delete(model);
        return "success";
    }

    // 返回老师教的课程中的学生信息
    public class student {
        public long studentId;
        // public String name;
        public int score;
        public String nickname;
        public String avatar;
        public String username;
    }
    public class choosedTeacher{
        public long classId;
        public String name;
        public student[] students; 
    }
    
    public choosedTeacher[] getTeacherClass(int teacherId) {
        User user[];
        try {
            user = rpc.fetchUsers();
        } catch (Exception e) {
            return null;
        }
        
        Class1Model[] classes = classRepository.findByTeacherId(teacherId);
        choosedTeacher[] result = new choosedTeacher[classes.length];
        for(int i = 0; i < classes.length; i++) {
            result[i] = new choosedTeacher();
            result[i].classId = classes[i].getId();
            result[i].name = classes[i].getName();
            ClassChooseModel[] students = classChooseRepository.findByClassId(classes[i].getId());
            
            result[i].students = new student[students.length];
            for(int j = 0; j < students.length; j++) {
                result[i].students[j] = new student();
                result[i].students[j].studentId = students[j].getStudentId();
                result[i].students[j].score = students[j].getScore();
                for(int k = 0; k < user.length; k++) {
                    if(user[k].id == students[j].getStudentId()) {
                        result[i].students[j].nickname = user[k].nickname;
                        result[i].students[j].avatar = user[k].avatar;
                        result[i].students[j].username = user[k].username;
                        break;
                    }
                }
            }
        }
        // System.out.println(result);
        return result;
    }

    public class teacher {
        public long teacherId;
        public String teacherName;
        public choosedTeacher[] classes;
    }
    public teacher[] getTeacherClassAll(){
        User user[];
        try {
            user = rpc.fetchUsers();
        } catch (Exception e) {
            return null;
        }
        Class1Model[] classes = classRepository.findAll().toArray(new Class1Model[0]);
        teacher[] result = new teacher[classes.length];
        for(int i = 0; i < classes.length; i++) {
            result[i] = new teacher();
            result[i].teacherId = classes[i].getTeacherId();
            result[i].teacherName = "";
            for(int j = 0; j < user.length; j++) {
                if(user[j].id == classes[i].getTeacherId()) {
                    result[i].teacherName = user[j].nickname;
                    break;
                }
            }
            
            ClassChooseModel[] students = classChooseRepository.findByClassId(classes[i].getId());
            
            result[i].classes = new choosedTeacher[1];
            result[i].classes[0] = new choosedTeacher();
            result[i].classes[0].classId = classes[i].getId();
            result[i].classes[0].name = classes[i].getName();
            result[i].classes[0].students = new student[students.length];
            for(int j = 0; j < students.length; j++) {
                result[i].classes[0].students[j] = new student();
                result[i].classes[0].students[j].studentId = students[j].getStudentId();
                result[i].classes[0].students[j].score = students[j].getScore();
                for(int k = 0; k < user.length; k++) {
                    if(user[k].id == students[j].getStudentId()) {
                        result[i].classes[0].students[j].nickname = user[k].nickname;
                        result[i].classes[0].students[j].avatar = user[k].avatar;
                        result[i].classes[0].students[j].username = user[k].username;
                        break;
                    }
                }
            }
        }
        return result;
    }


    public String setScore(int teacherId, int classId, int studentId, int score) {
        ClassChooseModel model = classChooseRepository.findByStudentIdAndClassId(studentId, classId);
        if(model == null) {
            return "未选课";
        }
        model.setScore(score);
        classChooseRepository.save(model);
        return "success";
    }

}
