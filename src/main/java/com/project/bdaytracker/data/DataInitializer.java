package com.project.bdaytracker.data;

import com.project.bdaytracker.model.Member;
import com.project.bdaytracker.repository.MemberRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository repository;

    public DataInitializer(MemberRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }

        repository.save(new Member("Arjun", LocalDate.of(1993, 7, 22), "Engineering"));
        repository.save(new Member("Meera", LocalDate.of(1990, 7, 25), "Design"));
        repository.save(new Member("Kavita", LocalDate.of(1995, 7, 26), "Marketing"));
        repository.save(new Member("Rahul", LocalDate.of(1992, 8, 5), "Product"));
        repository.save(new Member("Neha", LocalDate.of(1989, 8, 2), "Operations"));
    }
}
