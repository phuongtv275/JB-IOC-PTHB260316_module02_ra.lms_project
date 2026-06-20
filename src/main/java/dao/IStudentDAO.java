package dao;

import model.Student;

import java.util.List;
import java.util.Optional;

public interface IStudentDAO {

    Optional<Student> findUserByEmail(String email);

    /**
     * Tìm học viên theo email (dùng cho đăng nhập).
     */
    Optional<Student> findByEmail(String email);

    List<Student> findAll();

    Optional<Student> findById(int id);

    void save(Student student);

    void update(Student student);

    void deleteById(int id);

    /** Tìm theo tên, email, hoặc id — tương đối (ILIKE %keyword%) */
    List<Student> search(String keyword);

    List<Student> searchById(String id);

    List<Student> searchByName(String name);

    /** Sắp xếp: field = "name" | "id", direction = "ASC" | "DESC" */
    List<Student> findAllSorted(String field, String direction);

    /** Kiểm tra email đã tồn tại — dùng validate unique, excludeId để bỏ qua chính bản ghi đang sửa */
    boolean existsByEmail(String email, int excludeId);
}

