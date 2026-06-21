package dao.impl;

import dao.IStudentDAO;
import enums.Role;
import model.Student;
import utils.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class StudentDAOImpl implements IStudentDAO {

    // ── findUserByEmail ───────────────────────────────────────────────────

    @Override
    public Optional<Student> findUserByEmail(String email) {
        String sql = "call search_user_by_email(?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, email);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Lỗi findByEmail: " + e.getMessage());
        }

        return Optional.empty();
    }

    // ── findByEmail ───────────────────────────────────────────────────

    @Override
    public Optional<Student> findByEmail(String email) {
        String sql = "call search_student_by_email(?, ?)";

        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, email);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    if (rs.next()) return Optional.of(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] Lỗi findByEmail: " + e.getMessage());
        }

        return Optional.empty();
    }

    // ── findAll ───────────────────────────────────────────────────

    @Override
    public List<Student> findAll() {
        List<Student> list = new ArrayList<>();
        String sql = "call get_students(?)";
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
            System.err.println("[StudentDAO] findAll lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findById ──────────────────────────────────────────────────

    @Override
    public Optional<Student> findById(int id) {
        String sql = "call search_student_by_id(?, ?)";
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
            System.err.println("[StudentDAO] findById lỗi: " + e.getMessage());
        }
        return Optional.empty();
    }

    // ── save ──────────────────────────────────────────────────────

    @Override
    public void save(Student student) {
        String sql = "call add_student(?, ?, ?, ?, ?, ?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setString(1, student.getName());
            cstmt.setDate(2, Date.valueOf(student.getDob()));
            cstmt.setString(3, student.getEmail());
            cstmt.setBoolean(4, student.isSex());
            cstmt.setString(5, student.getPhone());
            cstmt.setString(6, student.getPassword());
            cstmt.registerOutParameter(7, java.sql.Types.INTEGER);
            cstmt.executeUpdate();

            int generatedId = cstmt.getInt(7);
            student.setId(generatedId);
            System.out.println("[StudentDAO] Thêm sinh viên thành công, id=" + student.getId());
        } catch (SQLException e) {
            System.err.println("[StudentDAO] save lỗi: " + e.getMessage());
        }
    }

    // ── update ────────────────────────────────────────────────────

    @Override
    public void update(Student student) {
        String sql = "call update_student(?, ?, ?, ?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, student.getId());
            cstmt.setString(2, student.getName());
            cstmt.setDate(3, Date.valueOf(student.getDob()));
            cstmt.setBoolean(4, student.isSex());
            cstmt.setString(5, student.getPhone());
            int rows = cstmt.executeUpdate();
            System.out.println("[StudentDAO] update — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] update lỗi: " + e.getMessage());
        }
    }

    // ── deleteById ────────────────────────────────────────────────

    @Override
    public void deleteById(int id) {
        String sql = "call delete_student(?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, id);
            int rows = cstmt.executeUpdate();
            System.out.println("[StudentDAO] delete id=" + id + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] deleteById lỗi: " + e.getMessage());
        }
    }

    // ── search (tên / email / id) ────────────────────────────────

    @Override
    public List<Student> search(String keyword) {
        List<Student> list = new ArrayList<>();
        String sql = "call search_student_by_name_or_email_or_id(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, keyword);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] search lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findById ───────────────────────────────────────────────────

    @Override
    public List<Student> searchById(String id) {
        List<Student> list = new ArrayList<>();
        String sql = "call search_student_by_id(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, id);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] search lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findByName ───────────────────────────────────────────────────

    @Override
    public List<Student> searchByName(String name) {
        List<Student> list = new ArrayList<>();
        String sql = "call search_student_by_name(?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, name);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    while (rs.next()) list.add(mapRow(rs));
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] search lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── findAllSorted ─────────────────────────────────────────────

    @Override
    public List<Student> findAllSorted(String field, String direction) {
        // Whitelist để tránh SQL injection
        String safeField = field.equalsIgnoreCase("name") ? "name" : "id";
        String safeDir    = direction.equalsIgnoreCase("DESC") ? "DESC" : "ASC";

        List<Student> list = new ArrayList<>();
        String sql = "SELECT id, name, dob, email, sex, phone, role, password, created_at " +
                "FROM student " +
                "WHERE role = 'STUDENT'" +
                "ORDER BY " + safeField + " " + safeDir;
        try (PreparedStatement ps = DBUtil.getConnection().prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        } catch (SQLException e) {
            System.err.println("[StudentDAO] findAllSorted lỗi: " + e.getMessage());
        }
        return list;
    }

    // ── existsByEmail ─────────────────────────────────────────────

    @Override
    public boolean existsByEmail(String email, int excludeId) {
        String sql = "call check_email_exists(?, ?, ?)";
        try (Connection conn = DBUtil.getConnection()) {
            conn.setAutoCommit(false);
            boolean exists = false;
            try (CallableStatement cstmt = conn.prepareCall(sql)) {
                cstmt.registerOutParameter(1, Types.REF_CURSOR);
                cstmt.setString(2, email);
                cstmt.setInt(3, excludeId);
                cstmt.execute();
                try (ResultSet rs = (ResultSet) cstmt.getObject(1)) {
                    exists = rs.next();
                }
            }
            conn.commit();
            conn.setAutoCommit(true);
            return exists;
        } catch (SQLException e) {
            System.err.println("[StudentDAO] existsByEmail lỗi: " + e.getMessage());
        }
        return false;
    }

    // ── updatePassword ────────────────────────────────────────────

    @Override
    public void updatePassword(int id, String newHashedPassword) {
        String sql = "call update_student_password(?, ?)";
        try (CallableStatement cstmt = DBUtil.getConnection().prepareCall(sql)) {
            cstmt.setInt(1, id);
            cstmt.setString(2, newHashedPassword);

            int rows = cstmt.executeUpdate();
            System.out.println("[StudentDAO] updatePassword id=" + id + " — rows affected: " + rows);
        } catch (SQLException e) {
            System.err.println("[StudentDAO] updatePassword lỗi: " + e.getMessage());
        }
    }

    // ── Row mapper ────────────────────────────────────────────────

    private Student mapRow(ResultSet rs) throws SQLException {
        Student s = new Student();
        s.setId(rs.getInt("id"));
        s.setName(rs.getString("name"));
        s.setDob(rs.getDate("dob").toLocalDate());
        s.setEmail(rs.getString("email"));
        s.setSex(rs.getBoolean("sex"));
        s.setPhone(rs.getString("phone"));
        s.setRole(Role.valueOf(rs.getString("role")));
        s.setPassword(rs.getString("password"));
        s.setCreatedAt(rs.getDate("created_at").toLocalDate());
        return s;
    }
}
