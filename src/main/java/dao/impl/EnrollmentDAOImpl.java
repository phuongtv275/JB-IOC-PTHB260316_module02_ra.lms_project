package dao.impl;

import dao.IEnrollmentDAO;
import enums.EnrollmentStatus;
import model.Course;
import model.Enrollment;
import utils.DBUtil;

import java.sql.*;
import java.util.*;

public class EnrollmentDAOImpl implements IEnrollmentDAO {

    // ── findById ──────────────────────────────────────────────────

    @Override
    public Optional<Enrollment> findById(int id) {
        String sql = "call search_enrollment_by_id(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setInt(2, id);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] findById lỗi: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ── findByStudentId ───────────────────────────────────────────

    @Override
    public List<Enrollment> findByStudentId(int studentId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "call search_enrollment_by_student_id(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setInt(2, studentId);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] findByStudentId lỗi: " + e.getMessage());
        }
        return list;
    }

    @Override
    public List<Enrollment> getEnrollments() {
        List<Enrollment> list = new ArrayList<>();
        String sql = "call get_all_enrollments(?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] getEnrollments lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findByStudentIdSorted ─────────────────────────────────────

    @Override
    public List<Enrollment> findByStudentIdSorted(int studentId, String field, String direction) {
        String safeField = field.equalsIgnoreCase("courseName") ? "course_name" : "registered_at";
        String safeDir    = direction.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        List<Enrollment> list = new ArrayList<>();
        String sql = "SELECT * FROM vw_enrollment_join_student_course" +
                " WHERE student_id = ? ORDER BY " + safeField + " " + safeDir;
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] findByStudentIdSorted lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── existsByStudentAndCourse ──────────────────────────────────

    @Override
    public boolean existsByStudentAndCourse(int studentId, int courseId) {
        String sql = "call check_student_enrolled_course(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            boolean exists = false;
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setInt(2, studentId);
                cstmt.setInt(3, courseId);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    exists = rs.next();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            return exists;
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] existsByStudentAndCourse lỗi: " + e.getMessage());
            return false;
        }
    }

    // ── save ──────────────────────────────────────────────────────

    @Override
    public void save(Enrollment enrollment) {
        String sql = "call add_enrollment(?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, enrollment.getStudentId());
            cstmt.setInt(2, enrollment.getCourseId());
            cstmt.setString(3, enrollment.getStatus().name());
            cstmt.registerOutParameter(4, Types.INTEGER);
            cstmt.executeUpdate();

            int generatedId = cstmt.getInt(4);
            enrollment.setId(generatedId);
            System.out.println("[EnrollmentDAO] Đăng ký thành công, id=" + enrollment.getId());
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] save lỗi: " + e.getMessage());
            throw new RuntimeException("Không thể đăng ký khóa học.", e);
        }
    }

    // ── updateStatus ──────────────────────────────────────────────

    @Override
    public void updateStatus(int id, EnrollmentStatus status) {
        String sql = "call update_enrollment_status(?, ?)";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, id);
            cstmt.setString(2, status.name());
            int rows = cstmt.executeUpdate();
            System.out.println("[EnrollmentDAO] updateStatus id=" + id + " -> " + status
                    + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] updateStatus lỗi: " + e.getMessage());
            throw new RuntimeException("Không thể cập nhật trạng thái đăng ký.", e);
        }
    }

    // ── deleteById ────────────────────────────────────────────────

    @Override
    public void deleteById(int id) {
        String sql = "call delete_enroll_by_id(?)";
        try (Connection conn = DBUtil.getConnection();
             CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setInt(1, id);
            int rows = cstmt.executeUpdate();
            System.out.println("[EnrollmentDAO] deleteById id=" + id + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] deleteById lỗi: " + e.getMessage());
            throw new RuntimeException("Không thể xóa đăng ký.", e);
        }
    }

    // ── findByCourseId (Admin xem SV đăng ký theo khóa) ─────────────

    @Override
    public List<Enrollment> findByCourseId(int courseId) {
        List<Enrollment> list = new ArrayList<>();
        String sql = "call search_course_by_course_id_join_enrollment_student(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setInt(2, courseId);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] findByCourseId lỗi: " + e.getMessage());
        }
        return list;
    }
    // ── countAllCourses ───────────────────────────────────────────

    @Override
    public int countAllCourses() {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall("{? = call fn_count_all_courses()}")) {
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();

                int total = cs.getInt(1);
                conn.commit();
                conn.setAutoCommit(true);
                return total;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] countAllCourses lỗi: " + e.getMessage());
            return 0;
        }
    }

    // ── countAllStudents ──────────────────────────────────────────

    @Override
    public int countAllStudents() {
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall("{? = call fn_count_all_students()}")) {
                cs.registerOutParameter(1, Types.INTEGER);
                cs.execute();

                int total = cs.getInt(1);
                conn.commit();
                conn.setAutoCommit(true);
                return total;
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] countAllStudents lỗi: " + e.getMessage());
            return 0;
        }
    }

    // ── countStudentsPerCourse ────────────────────────────────────

    @Override
    public Map<Integer, Long> countStudentsPerCourse() {
        Map<Integer, Long> result = new LinkedHashMap<>();
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall("{? = call fn_count_students_per_course()}")) {
                cs.registerOutParameter(1, Types.REF_CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        result.put(rs.getInt("id"), rs.getLong("cnt"));
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] countStudentsPerCourse lỗi: " + e.getMessage());
        }
        return result;
    }

    // ── topCoursesByEnrollment ────────────────────────────────────

    @Override
    public List<Object[]> topCoursesByEnrollment(int limit) {
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall("{? = call fn_top_courses_by_enrollment(?)}")) {
                cs.registerOutParameter(1, Types.REF_CURSOR);
                cs.setInt(2, limit);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        result.add(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getLong("cnt")});
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] topCoursesByEnrollment lỗi: " + e.getMessage());
        }
        return result;
    }

    // ── coursesWithEnrollmentAbove ────────────────────────────────

    @Override
    public List<Object[]> coursesWithEnrollmentAbove(int threshold) {
        List<Object[]> result = new ArrayList<>();
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cs = conn.prepareCall("{? = call fn_courses_with_enrollment_above(?)}")) {
                cs.registerOutParameter(1, Types.REF_CURSOR);
                cs.setInt(2, threshold);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        result.add(new Object[]{
                                rs.getInt("id"),
                                rs.getString("name"),
                                rs.getLong("cnt")
                        });
                    }
                }
                conn.commit();
                conn.setAutoCommit(true);
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] coursesWithEnrollmentAbove lỗi: " + e.getMessage());
        }
        return result;
    }

    // ── Row mapper ────────────────────────────────────────────────

    private Enrollment mapRow(ResultSet rs) throws SQLException {
        Enrollment e = new Enrollment();
        e.setId(rs.getInt("id"));
        e.setStudentId(rs.getInt("student_id"));
        e.setCourseId(rs.getInt("course_id"));
        e.setInstructor(rs.getString("instructor"));
        e.setRegisteredAt(rs.getTimestamp("registered_at").toLocalDateTime());
        e.setStatus(EnrollmentStatus.valueOf(rs.getString("status")));
        e.setCourseName(rs.getString("course_name"));
        e.setStudentName(rs.getString("student_name"));
        return e;
    }
}
