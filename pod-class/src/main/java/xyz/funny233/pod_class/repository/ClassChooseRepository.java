package xyz.funny233.pod_class.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import xyz.funny233.pod_class.model.ClassChooseModel;

public interface ClassChooseRepository extends JpaRepository<ClassChooseModel, Long> {
    ClassChooseModel[] findByStudentId(long studentId);
    ClassChooseModel[] findByClassId(long classId);
    ClassChooseModel findByStudentIdAndClassId(long studentId, long classId);
    void deleteByStudentIdAndClassId(long studentId, long classId);
    void deleteByStudentId(long studentId);
    void deleteByClassId(long classId);
}
