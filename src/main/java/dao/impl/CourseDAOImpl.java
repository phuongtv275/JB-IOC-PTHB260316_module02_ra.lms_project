package dao.impl;

import dao.ICourseDAO;
import model.Course;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CourseDAOImpl implements ICourseDAO {

    // ── findAll ───────────────────────────────────────────────────

    @Override
    public List<Course> findAll() {
        List<Course> list = new ArrayList<>();
        String sql = "call get_courses(?)";
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
            System.err.println("[CourseDAO] findAll lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findById ──────────────────────────────────────────────────

    @Override
    public Optional<Course> findById(int id) {
        String sql = "call search_course_by_id(?, ?)";
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
            System.err.println("[CourseDAO] findById lỗi: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ── save ──────────────────────────────────────────────────────

    @Override
    public void save(Course course) {
        String sql = "call add_course(?, ?, ?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setString(1, course.getName());
            cstmt.setInt(2, course.getDuration());
            cstmt.setString(3, course.getInstructor());
            cstmt.registerOutParameter(4, java.sql.Types.INTEGER);
            cstmt.executeUpdate();

            int generatedId = cstmt.getInt(4);
            course.setId(generatedId);
            System.out.println("[CourseDAO] Thêm khóa học thành công, id=" + course.getId());
        } catch (SQLException e) {
            System.err.println("[CourseDAO] save lỗi: " + e.getMessage());
        }
    }

    // ── update ────────────────────────────────────────────────────

    @Override
    public void update(Course course) {
        String sql = "call update_course(?, ?, ?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, course.getId());
            cstmt.setString(2, course.getName());
            cstmt.setInt(3, course.getDuration());
            cstmt.setString(4, course.getInstructor());
            int rows = cstmt.executeUpdate();
            System.out.println("[CourseDAO] update — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[CourseDAO] update lỗi: " + e.getMessage());
        }
    }

    // ── deleteById ────────────────────────────────────────────────

    @Override
    public void deleteById(int id) {
        String sql = "call delete_course(?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, id);
            int rows = cstmt.executeUpdate();
            System.out.println("[CourseDAO] delete id=" + id + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[CourseDAO] deleteById lỗi: " + e.getMessage());
        }
    }

    // ── searchByName ──────────────────────────────────────────────

    @Override
    public List<Course> searchByName(String keyword) {
        List<Course> list = new ArrayList<>();
        String sql = "call search_course_by_name(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, "%" + keyword + "%");
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[CourseDAO] searchByName lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findAllSorted ─────────────────────────────────────────────

    @Override
    public List<Course> findAllSorted(String field, String direction) {
        // Whitelist để tránh SQL injection
        String safeField = field.equalsIgnoreCase("name") ? "name" : "id";
        String safeDir   = direction.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        List<Course> list = new ArrayList<>();
        String sql = "SELECT id, name, duration, instructor, created_at FROM course ORDER BY " + safeField + " " + safeDir;
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[CourseDAO] findAllSorted lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── Row mapper ────────────────────────────────────────────────

    private Course mapRow(ResultSet rs) throws SQLException {
        Course c = new Course();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setDuration(rs.getInt("duration"));
        c.setInstructor(rs.getString("instructor"));
        c.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return c;
    }
}
