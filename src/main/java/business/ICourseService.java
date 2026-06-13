package business;

import model.Course;

import java.util.List;
import java.util.Optional;

public interface ICourseService {

    List<Course> getAllCourses();

    Optional<Course> getCourseById(int id);

    /** Trả về thông báo lỗi nếu có, null nếu thành công */
    String addCourse(String name, String durationStr, String instructor);

    /** Cập nhật từng trường — trả về thông báo lỗi nếu có, null nếu thành công */
    String updateCourseName(int id, String newName);
    String updateCourseDuration(int id, String newDurationStr);
    String updateCourseInstructor(int id, String newInstructor);

    /** Trả về true nếu xóa thành công, false nếu không tìm thấy */
    boolean deleteCourse(int id);

    List<Course> searchCourseByName(String keyword);

    List<Course> getSortedCourses(String field, String direction);
}
