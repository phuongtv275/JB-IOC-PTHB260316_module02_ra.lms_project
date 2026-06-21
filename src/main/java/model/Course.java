package model;

import java.time.LocalDate;

public class Course {
    private int id;
    private String name;
    private int duration;
    private String instructor;
    private LocalDate createdAt;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public LocalDate getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDate createdAt) {
        this.createdAt = createdAt;
    }

    public Course(int id, String name, int duration, String instructor, LocalDate createdAt) {
        this.id = id;
        this.name = name;
        this.duration = duration;
        this.instructor = instructor;
        this.createdAt = createdAt;
    }

    public Course() {
    }

    @Override
    public String toString() {
        return String.format("Course{id=%d, name='%s', duration=%dh, instructor='%s'}",
                id, name, duration, instructor);
    }
}
