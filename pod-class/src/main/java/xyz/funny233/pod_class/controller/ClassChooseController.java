package xyz.funny233.pod_class.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import xyz.funny233.pod_class.service.ClassChooseService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;


@RestController
public class ClassChooseController {
    @Autowired
    private ClassChooseService classChooseService;

    @GetMapping("/choose")
    public ResponseEntity<Response> getChoosedClass(
            @RequestHeader("x-forward-role") int role,
            @RequestHeader("x-forward-id") int userId) {
        if (role == 2) {
            return ResponseEntity.ok().body(new Response(200, "success", classChooseService.getChoosedClass(userId)));
        } else {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
    }

    @PutMapping("/choose")
    public ResponseEntity<Response> putMethodName(
            @RequestHeader("x-forward-role") int role,
            @RequestHeader("x-forward-id") int userId,
            @RequestBody Map<String, String> body) {
        if (role == 2) {
            int classId = Integer.parseInt(body.get("classId"));
            String result = classChooseService.chooseClass(userId, classId);
            if (!result.equals("success")) {
                return ResponseEntity.badRequest().body(new Response(400, result, null));
            }
            return ResponseEntity.ok().body(new Response(200, "success", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
    }

    @DeleteMapping("/choose")
    public ResponseEntity<Response> deleteMethodName(
            @RequestHeader("x-forward-role") int role,
            @RequestHeader("x-forward-id") int userId,
            @RequestParam int classId) {
        if (role == 2) {
            String result = classChooseService.cancelClass(userId, classId);
            if (!result.equals("success")) {
                return ResponseEntity.badRequest().body(new Response(400, result, null));
            }
            return ResponseEntity.ok().body(new Response(200, "success", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
    }


    @GetMapping("/choose/teacher")
    public ResponseEntity<Response> getTeacherClass(
            @RequestHeader("x-forward-role") int role,
            @RequestHeader("x-forward-id") int userId) {
        if (role == 1) {
            return ResponseEntity.ok().body(new Response(200, "success", classChooseService.getTeacherClass(userId)));
        } else if (role == 0){
            return ResponseEntity.ok().body(new Response(200, "success", classChooseService.getTeacherClassAll()));
        }  else {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
    }

    @PostMapping("/choose/score")
    public ResponseEntity<Response> changeScore(
            @RequestHeader("x-forward-role") int role,
            @RequestHeader("x-forward-id") int userId,
            @RequestBody Map<String, String> body) {
        if (role == 1 || role == 0) {
            int classId = Integer.parseInt(body.get("classId"));
            int studentId = Integer.parseInt(body.get("studentId"));
            int score = Integer.parseInt(body.get("score"));
            String result = classChooseService.setScore(userId, classId, studentId, score);
            if (!result.equals("success")) {
                return ResponseEntity.badRequest().body(new Response(400, result, null));
            }
            return ResponseEntity.ok().body(new Response(200, "success", null));
        } else {
            return ResponseEntity.badRequest().body(new Response(400, "permission denied", null));
        }
    }
    

}
