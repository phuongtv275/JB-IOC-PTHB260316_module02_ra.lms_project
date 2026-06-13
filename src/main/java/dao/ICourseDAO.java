package dao;

import model.Course;

import java.util.List;
import java.util.Optional;

public interface ICourseDAO {

    List<Course> findAll();

    Optional<Course> findById(int id);

    void save(Course course);

    void update(Course course);

    void deleteById(int id);

    /** Tìm theo tên — tương đối (ILIKE %keyword%) */
    List<Course> searchByName(String keyword);

    /** Sắp xếp: field = "name" | "id", direction = "ASC" | "DESC" */
    List<Course> findAllSorted(String field, String direction);
}
