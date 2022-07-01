package CafeMangementSystem.DAOs;

import CafeMangementSystem.Entities.Nhanvien;
import CafeMangementSystem.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureQuery;
import java.util.List;

public class NhanvienDAO implements DAO<Nhanvien> {
    private static NhanvienDAO instance;
    private NhanvienDAO() {}
    public static NhanvienDAO getInstance() {
        if (instance == null) {
            return new NhanvienDAO();
        }
        return instance;
    }

    @Override
    public int getMaxId() {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Number value = 0;

        try {
            session.getTransaction().begin();

            String sql = "select max(nv.manv) from " + Nhanvien.class.getName() + " nv";
            Query<Number> query = session.createQuery(sql);
            value = query.getSingleResult();

            if (value==null) {
                value = 0;
            }

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return value.intValue();
    }

    @Override
    public List<Nhanvien> getAll() {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        List<Nhanvien> nhanvienList = null;

        try {
            session.getTransaction().begin();

            String sql = "Select nv from " + Nhanvien.class.getName() + " nv";
            Query<Nhanvien> query = session.createQuery(sql);
            nhanvienList = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return nhanvienList;
    }

    @Override
    public boolean insert(Nhanvien obj) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            session.save(obj);

            session.flush();
            System.out.println("Nhân viên dược thêm vào DB:" + obj);

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return false;
    }

    @Override
    public Nhanvien get(int id) {
        Nhanvien nhanvien = null;

        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            String sql = "Select nv from " + Nhanvien.class.getName() + " nv where nv.manv = :manv";
            Query<Nhanvien> query = session.createQuery(sql);
            query.setParameter("manv",id);
            nhanvien = query.getSingleResult();

            // Kiểm tra trạng thái của nhanvien:
            // ==> true
            // System.out.println("Truoc khi commit session - nhanvien Persistent? " + session.contains(nhanvien));
            session.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return nhanvien;
    }

    @Override
    public boolean update(int id, Nhanvien newObj) {
        Nhanvien nhanvien = NhanvienDAO.getInstance().get(id);

        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            // Kiểm tra trạng thái của nhanvien:
            // ==> false
            // System.out.println("- nhanvien Persistent? " + session.contains(nhanvien));

            nhanvien.setALlNewValue(newObj); // set all new value from newObj

            session.update(nhanvien);
            session.flush();

            System.out.println("Nhan vien sau khi duoc update:" + nhanvien);

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return false;
    }

    @Override
    public boolean delete(Nhanvien obj) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            session.delete(obj);

            session.flush();
            session.getTransaction().commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return false;
    }

    public List<Nhanvien> findAnyLike(String pattern) {
        List<Nhanvien> nhanvienList = null;

        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            String sqlFindAnyName = "Select nv from " + Nhanvien.class.getName() + " nv" +
                    " where nv.tennv like :pattern or nv.dienthoai like :pattern or nv.diachi like :pattern";
            Query<Nhanvien> queryFindAnyName = session.createQuery(sqlFindAnyName);
            queryFindAnyName.setParameter("pattern",pattern);
            nhanvienList = queryFindAnyName.getResultList();

            session.getTransaction().commit();
        }catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return nhanvienList;
    }

    // get hashed password saved in DB
    public String getHashedPassword (String username) {
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();

        String hashedPassword = null;

        try {
            session.getTransaction().begin();

//            StoredProcedureQuery sp_getHashedPassword = session.createStoredProcedureQuery("getHashedPassword");
//            sp_getHashedPassword.registerStoredProcedureParameter("username",String.class, ParameterMode.IN);
//            sp_getHashedPassword.registerStoredProcedureParameter("hashedPassword", String.class, ParameterMode.OUT);
//
//            sp_getHashedPassword.setParameter("username", username);
//            sp_getHashedPassword.execute();
//
//            hashedPassword = (String) sp_getHashedPassword.getOutputParameterValue("hashedPassword");
//            System.out.println("Hashed password: " + hashedPassword);

            String sql = "select nv.matkhau from " + Nhanvien.class.getName() + " nv" +
                    " where nv.tendangnhap = :username";
            Query<String> query = session.createQuery(sql);
            query.setParameter("username",username);
            hashedPassword = query.getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return hashedPassword;
    }

    public Nhanvien getAccountForSession(String username, String password) {
        SessionFactory factory = HibernateUtils.getSessionFactory();
        Session session = factory.getCurrentSession();

        Nhanvien nhanvien = null;

        try {
            session.getTransaction().begin();

            String sql = "SELECT nv " +
                    "FROM " + Nhanvien.class.getName() + " nv  " +
                    "WHERE nv.tendangnhap = :usernameToCheck AND nv.matkhau = :passwordToCheck";

            Query<Nhanvien> query = session.createQuery(sql);
            query.setParameter("usernameToCheck", username);
            query.setParameter("passwordToCheck", password);
            nhanvien = query.getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return nhanvien;
    }

//    public Nhanvien get(String tendangnhap) {
//        SessionFactory factory = HibernateUtils.getSessionFactory();
//        Session session = factory.getCurrentSession();
//
//        Nhanvien nhanvien = null;
//
//        try {
//            session.getTransaction().begin();
//
//            String sql = "SELECT nv " +
//                    "FROM " + Nhanvien.class.getName() + " nv  " +
//                    "WHERE nv.tendangnhap = :usernameToCheck";
//
//            Query<Nhanvien> query = session.createQuery(sql);
//            query.setParameter("usernameToCheck", tendangnhap);
//            nhanvien = query.getSingleResult();
//
//            session.getTransaction().commit();
//        } catch (Exception e) {
//            e.printStackTrace();
//            session.getTransaction().rollback();
//        }
//
//        return nhanvien;
//    }
}
