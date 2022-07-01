package CafeMangementSystem.DAOs;

import CafeMangementSystem.Entities.Hoadon;
import CafeMangementSystem.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;


import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class HoadonDAO implements DAO<Hoadon> {
    private static HoadonDAO instance;
    private HoadonDAO() {}
    public static HoadonDAO getInstance() {
        if (instance == null) {
            instance = new HoadonDAO();
        }
        return instance;
    }

    @Override
    public int getMaxId() {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Number value = 0;

        try {
            session.getTransaction().begin();

            String sql = "select max(hd.mahoadon) from " + Hoadon.class.getName() + " hd";
            Query<Number> query = session.createQuery(sql);
            value = query.getSingleResult();

            if (value == null) {
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
    public List<Hoadon> getAll() {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        List<Hoadon> hoadonList = null;

        try {
            session.getTransaction().begin();

            String sql = "select hd from " + Hoadon.class.getName() + " hd";
            Query<Hoadon> query = session.createQuery(sql);
            hoadonList = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return hoadonList;
    }

    @Override
    public boolean insert(Hoadon obj) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            session.save(obj);

            session.flush();
            System.out.println("Hóa đơn mới dược thêm vào DB:" + obj);

            session.getTransaction().commit();

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return false;
    }

    @Override
    public Hoadon get(int id) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Hoadon hoadon = null;

        try {
            session.getTransaction().begin();

            String sql = "select hd from " + Hoadon.class.getName() + " hd where hd.id = :id";
            Query<Hoadon> query = session.createQuery(sql);
            query.setParameter("id",id);
            hoadon = query.getSingleResult();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return hoadon;
    }

    @Override
    public boolean update(int id, Hoadon newObj) {
//        Hoadon hoadon = HoadonDAO.getInstance().get(id);
//
//        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
//
//        try {
//            session.getTransaction().begin();
//
//            hoadon.setALlNewValue(newObj); // set all new value from newObj
//
//            session.update(hoadon);
//            session.flush();
//
//            System.out.println("Hoa don sau khi duoc update:" + hoadon);
//
//            session.getTransaction().commit();
//            return true;
//        } catch (Exception e) {
//            e.printStackTrace();
//            session.getTransaction().rollback();
//        }
        return false;
    }

    @Override
    public boolean delete(Hoadon obj) {
        return false;
    }

    public List<Hoadon> getAll(LocalDateTime fromDate, LocalDateTime toDate) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        List<Hoadon> hoadonList = null;

        try {
            session.getTransaction().begin();

            String sql = "select hd from " + Hoadon.class.getName() + " hd" +
                    " where :fromDay <=  hd.ngaygiaodich and hd.ngaygiaodich <= :toDay";
            Query<Hoadon> query = session.createQuery(sql);
            query.setParameter("fromDay",fromDate);
            query.setParameter("toDay",toDate);
            hoadonList = query.getResultList();

            session.getTransaction().commit();
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }

        return hoadonList;
    }
}
