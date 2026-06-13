package business.impl;

import business.ICourseService;
import dao.ICourseDAO;
import dao.impl.CourseDAOImpl;
import model.Course;

import java.util.List;
import java.util.Optional;

public class CourseServiceImpl implements ICourseService {

    private final ICourseDAO courseDAO;

    public CourseServiceImpl() {
        this.courseDAO = new CourseDAOImpl();
    }

    // ── getAllCourses ─────────────────────────────────────────────

    @Override
    public List<Course> getAllCourses() {
        return courseDAO.findAll();
    }

    // ── getCourseById ─────────────────────────────────────────────

    @Override
    public Optional<Course> getCourseById(int id) {
        return courseDAO.findById(id);
    }

    // ── addCourse ─────────────────────────────────────────────────

    @Override
    public String addCourse(String name, String durationStr, String instructor) {
        // Validate không để trống
        if (name == null || name.isBlank())
            return "Tên khóa học không được để trống.";
        if (durationStr == null || durationStr.isBlank())
            return "Thời lượng không được để trống.";
        if (instructor == null || instructor.isBlank())
            return "Tên giảng viên không được để trống.";

        // Validate duration là số nguyên dương
        int duration;
        try {
            duration = Integer.parseInt(durationStr.trim());
        } catch (NumberFormatException e) {
            return "Thời lượng phải là số nguyên.";
        }
        if (duration <= 0)
            return "Thời lượng phải lớn hơn 0.";

        Course course = new Course();
        course.setName(name.trim());
        course.setDuration(duration);
        course.setInstructor(instructor.trim());
        courseDAO.save(course);
        return null;
    }

    // ── updateCourseName ──────────────────────────────────────────

    @Override
    public String updateCourseName(int id, String newName) {
        if (newName == null || newName.isBlank())
            return "Tên khóa học không được để trống.";

        Optional<Course> opt = courseDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy khóa học với id=" + id;

        Course course = opt.get();
        course.setName(newName.trim());
        courseDAO.update(course);
        return null;
    }

    // ── updateCourseDuration ──────────────────────────────────────

    @Override
    public String updateCourseDuration(int id, String newDurationStr) {
        if (newDurationStr == null || newDurationStr.isBlank())
            return "Thời lượng không được để trống.";

        int duration;
        try {
            duration = Integer.parseInt(newDurationStr.trim());
        } catch (NumberFormatException e) {
            return "Thời lượng phải là số nguyên.";
        }
        if (duration <= 0)
            return "Thời lượng phải lớn hơn 0.";

        Optional<Course> opt = courseDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy khóa học với id=" + id;

        Course course = opt.get();
        course.setDuration(duration);
        courseDAO.update(course);
        return null;
    }

    // ── updateCourseInstructor ────────────────────────────────────

    @Override
    public String updateCourseInstructor(int id, String newInstructor) {
        if (newInstructor == null || newInstructor.isBlank())
            return "Tên giảng viên không được để trống.";

        Optional<Course> opt = courseDAO.findById(id);
        if (opt.isEmpty()) return "Không tìm thấy khóa học với id=" + id;

        Course course = opt.get();
        course.setInstructor(newInstructor.trim());
        courseDAO.update(course);
        return null;
    }

    // ── deleteCourse ──────────────────────────────────────────────

    @Override
    public boolean deleteCourse(int id) {
        Optional<Course> opt = courseDAO.findById(id);
        if (opt.isEmpty()) return false;
        courseDAO.deleteById(id);
        return true;
    }

    // ── searchCourseByName ────────────────────────────────────────

    @Override
    public List<Course> searchCourseByName(String keyword) {
        if (keyword == null || keyword.isBlank()) return courseDAO.findAll();
        return courseDAO.searchByName(keyword.trim());
    }

    // ── getSortedCourses ──────────────────────────────────────────

    @Override
    public List<Course> getSortedCourses(String field, String direction) {
        return courseDAO.findAllSorted(field, direction);
    }
}
