package xyz.funny233.pod_class.model;


import org.springframework.stereotype.Component;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Class1Model {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private long id;
    private String name;
    private int teacherId;
    private int credit;

    public void setId(long id) {
        this.id = id;
    }

    public long getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getTeacherId() {
        return teacherId;
    }
    public void setTeacherId(int teacherId) {
        this.teacherId = teacherId;
    }
    public int getCredit() {
        return credit;
    }
    public void setCredit(int credit) {
        this.credit = credit;
    }
}
