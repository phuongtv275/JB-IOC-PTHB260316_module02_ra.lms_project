package dao.impl;

import dao.IEnrollmentDAO;
import enums.EnrollmentStatus;
import model.Course;
import model.Enrollment;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql)) {
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
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, enrollment.getStudentId());
            cstmt.setInt(2, enrollment.getCourseId());
            cstmt.setString(3, enrollment.getStatus().name());
            cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            cstmt.executeUpdate();

            int generatedId = cstmt.getInt(4);
            enrollment.setId(generatedId);
            System.out.println("[EnrollmentDAO] Đăng ký thành công, id=" + enrollment.getId());
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] save lỗi: " + e.getMessage());
        }
    }

    // ── updateStatus ──────────────────────────────────────────────

    @Override
    public void updateStatus(int id, EnrollmentStatus status) {
        String sql = "call update_enrollment_status(?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, id);
            cstmt.setString(2, status.name());
            int rows = cstmt.executeUpdate();
            System.out.println("[EnrollmentDAO] updateStatus id=" + id + " -> " + status
                    + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] updateStatus lỗi: " + e.getMessage());
        }
    }

    // ── deleteById ────────────────────────────────────────────────

    @Override
    public void deleteById(int id) {
        String sql = "call delete_enroll_by_id(?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, id);
            int rows = cstmt.executeUpdate();
            System.out.println("[EnrollmentDAO] deleteById id=" + id + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[EnrollmentDAO] deleteById lỗi: " + e.getMessage());
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
