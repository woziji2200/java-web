package xyz.funny233.pod_class.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import xyz.funny233.pod_class.gateway.RPC;
import xyz.funny233.pod_class.gateway.RPC.User;
import xyz.funny233.pod_class.model.Class1Model;
import xyz.funny233.pod_class.service.ClassChooseService;
import xyz.funny233.pod_class.service.ClassService;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
public class ClassController {

    @Autowired
    ClassService classService;

    @Autowired
    RPC rpc;

    @GetMapping("/class")
    public ResponseEntity<Response> getCLass() {
        return ResponseEntity.ok().body(new Response(200, "success", classService.getAllClass()));
    }

    @GetMapping("/class/{id}")
    public ResponseEntity<Response> getClassById(@PathVariable long id) {
        var entity = classService.getClassById(id);
        if (entity == null) {
            return ResponseEntity.badRequest().body(new Response(400, "class not found", null));
        }
        return ResponseEntity.ok().body(new Response(200, "success", classService.getClassById(id)));
    }

    @PutMapping("/class/{id}")
    public ResponseEntity<Response> putClass(
            @PathVariable String id,
            @RequestBody Map<String, String> body,
            @RequestHeader("x-forward-role") int role) {
        if (role != 0) {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
        var entity = classService.getClassById(Long.parseLong(id));
        if (entity == null) {
            return ResponseEntity.badRequest().body(new Response(400, "class not found", null));
        }
        var name = body.get("name");
        var teacherUsername = body.get("teacherUsername");
        var credit = body.get("credit");
        if (name != null) {
            entity.setName(name);
        }
        if (teacherUsername != null) {
            try {
                User users[] = rpc.fetchUsers();
                for (int i = 0; i < users.length; i++) {
                    if (users[i].username.equals(teacherUsername)) {
                        entity.setTeacherId(users[i].userId);
                        break;
                    }
                }
            } catch (Exception e) {
                // TODO: handle exception
            }            
        }
        if (credit != null) {
            entity.setCredit(Integer.parseInt(credit));
        }
        classService.updateClass(entity);
        return ResponseEntity.ok().body(new Response(200, "success", entity));
    }

    @PostMapping("/class")
    public ResponseEntity<Response> postClass(
            @RequestBody Map<String, String> body,
            @RequestHeader("x-forward-role") int role) {
        if (role != 0) {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
        var entityTemp = classService.getClassByName(body.get("name"));
        if (entityTemp != null) {
            return ResponseEntity.badRequest().body(new Response(400, "class already exists", null));
        }

        var entity = new Class1Model();
        entity.setName(body.get("name"));
        // entity.setTeacherId(Integer.parseInt(body.get("teacherId")));
        try {
            User users[] = rpc.fetchUsers();
            boolean flag = false;
            for (int i = 0; i < users.length; i++) {
                // System.out.println(users[i].username + " " + body.get("teacherUsername"));
                if (users[i].username.equals(body.get("teacherUsername"))) {
                    entity.setTeacherId(users[i].id);
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                return ResponseEntity.badRequest().body(new Response(400, "teacher not exists", null));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new Response(400, "teacher not exists", null));
        }
        entity.setCredit(Integer.parseInt(body.get("credit")));
        classService.addClass(entity);
        return ResponseEntity.ok().body(new Response(200, "success", entity));
    }

    @Autowired
    private ClassChooseService classChooseService;

    @DeleteMapping("/class/{id}")
    public ResponseEntity<Response> deleteClass(@PathVariable long id,
            @RequestHeader("x-forward-role") int role) {
        if (role != 0) {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
        var entity = classService.getClassById(id);
        if (entity == null) {
            return ResponseEntity.badRequest().body(new Response(400, "class not found", null));
        }
        classService.deleteClass(id);
        classChooseService.deleteClass((int) id);
        return ResponseEntity.ok().body(new Response(200, "success", null));
    }


    @GetMapping("/teacher/{teacherId}")
    public ResponseEntity<Response> getTeacherById(@PathVariable int teacherId) {
        var entity = classService.getClassByTeacherId(teacherId);
        if (entity == null) {
            return ResponseEntity.badRequest().body(new Response(400, "teacher not found", null));
        }
        return ResponseEntity.ok().body(new Response(200, "success", entity));
    }

    

}
