package com.project.bdaytracker.controller;

import com.project.bdaytracker.model.Member;
import com.project.bdaytracker.service.MemberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class MemberController {

    private final MemberService service;

    public MemberController(MemberService service) {
        this.service = service;
    }

    @GetMapping("/members")
    public List<Member> getAllMembers() {
        return service.findAll();
    }

    @PostMapping("/members")
    public ResponseEntity<Member> addMember(@RequestBody Member member) {
        var saved = service.create(member);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/members/{id}")
    public ResponseEntity<Member> updateMember(@PathVariable Long id, @RequestBody Member member) {
        try {
            var updated = service.update(id, member);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/members/today")
    public List<Member> getTodaysBirthdays() {
        return service.findToday();
    }

    @GetMapping("/members/week")
    public List<Member> getThisWeeksBirthdays() {
        return service.findCurrentWeek();
    }

    @GetMapping("/members/month")
    public List<Member> getThisMonthsBirthdays() {
        return service.findCurrentMonth();
    }

    @GetMapping("/banner/{id}")
    public ResponseEntity<Map<String, Object>> createBanner(@PathVariable Long id) {
        return service.findById(id)
                .map(service::createBanner)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
