package com.project.bdaytracker.service;

import com.project.bdaytracker.model.Member;
import com.project.bdaytracker.repository.MemberRepository;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MemberService {

    private final MemberRepository repository;
    private final List<String> quotes = List.of(
            "Wishing you a day full of laughter and cake!",
            "Birthdays are nature's way of telling us to eat more cake.",
            "Age is merely the number of years the world has been enjoying you.",
            "Smile, today is your birthday and that is reason enough!",
            "May your day be sprinkled with fun and your year with success.",
            "Keep calm and celebrate your birthday in style.",
            "Your birthday is the perfect excuse to eat dessert first.",
            "Another year older, another year wiser (or funnier).",
            "May your birthday be as awesome as you are.",
            "Birthdays are the universe's way of cheering for you.",
            "You bring sparkle to the team — happy birthday!",
            "Celebrate loudly today, the cake will forgive you tomorrow.",
            "Hope your birthday is full of team spirit and joyful surprises.",
            "You're a legend in the making — happy birthday!",
            "Here’s to a birthday filled with laughs and good company.",
            "Let the good times roll — it’s your special day!",
            "Birthdays are better when shared with great teammates.",
            "Another trip around the sun means more stories to tell.",
            "Bring the party mood — your birthday is the highlight.",
            "The team shines brighter because of you. Enjoy your birthday!"
    );

    public MemberService(MemberRepository repository) {
        this.repository = repository;
    }

    public List<Member> findAll() {
        return repository.findAll();
    }

    public Member create(Member member) {
        if (member.getName() == null || member.getName().isBlank()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (member.getBirthDate() == null) {
            throw new IllegalArgumentException("Birth date is required");
        }
        if (member.getTeam() == null || member.getTeam().isBlank()) {
            member.setTeam("Unassigned");
        }
        return repository.save(member);
    }

    public Member update(Long id, Member member) {
        return repository.findById(id).map(existing -> {
            existing.setName(member.getName());
            existing.setBirthDate(member.getBirthDate());
            existing.setTeam(member.getTeam());
            return repository.save(existing);
        }).orElseThrow(() -> new IllegalArgumentException("Member not found"));
    }

    public Optional<Member> findById(Long id) {
        return repository.findById(id);
    }

    public List<Member> findToday() {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(member -> sameMonthDay(member.getBirthDate(), today))
                .collect(Collectors.toList());
    }

    public List<Member> findCurrentWeek() {
        LocalDate today = LocalDate.now();
        LocalDate weekStart = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate weekEnd = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        return findAll().stream()
                .filter(member -> {
                    LocalDate nextBirthday = nextBirthday(member.getBirthDate(), today);
                    return !nextBirthday.isBefore(weekStart) && !nextBirthday.isAfter(weekEnd);
                })
                .collect(Collectors.toList());
    }

    public List<Member> findCurrentMonth() {
        LocalDate today = LocalDate.now();
        return findAll().stream()
                .filter(member -> nextBirthday(member.getBirthDate(), today).getMonth() == today.getMonth())
                .collect(Collectors.toList());
    }

    public boolean isWeekendBirthday(Member member) {
        LocalDate nextBirthday = nextBirthday(member.getBirthDate(), LocalDate.now());
        DayOfWeek day = nextBirthday.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    public Map<String, Object> createBanner(Member member) {
        var randomQuote = quotes.get(new Random().nextInt(quotes.size()));
        var nextBirthday = nextBirthday(member.getBirthDate(), LocalDate.now());
        var weekend = isWeekendBirthday(member);
        Map<String, Object> banner = new LinkedHashMap<>();
        banner.put("memberName", member.getName());
        banner.put("team", member.getTeam());
        banner.put("nextBirthday", nextBirthday.toString());
        banner.put("weekend", weekend);
        banner.put("quote", randomQuote);
        banner.put("message", "Happy birthday banner created for " + member.getName() + "!");
        return banner;
    }

    private boolean sameMonthDay(LocalDate birthDate, LocalDate compare) {
        return birthDate.getMonth() == compare.getMonth() && birthDate.getDayOfMonth() == compare.getDayOfMonth();
    }

    private LocalDate nextBirthday(LocalDate birthDate, LocalDate reference) {
        var candidate = LocalDate.of(reference.getYear(), birthDate.getMonth(), birthDate.getDayOfMonth());
        if (candidate.isBefore(reference)) {
            candidate = candidate.plusYears(1);
        }
        return candidate;
    }
}
