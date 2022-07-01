package CafeMangementSystem.DAOs;

import CafeMangementSystem.Entities.ChitietHoadon;
import CafeMangementSystem.Entities.Hoadon;
import CafeMangementSystem.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.query.Query;

import java.util.List;

public class ChitietHoadonDAO implements DAO<ChitietHoadon> {
    private static ChitietHoadonDAO instance;
    private ChitietHoadonDAO() {}
    public static ChitietHoadonDAO getInstance() {
        if (instance == null) {
            return new ChitietHoadonDAO();
        }
        return instance;
    }

    @Override
    public int getMaxId() {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();
        Number value = 0;

        try {
            session.getTransaction().begin();

            String sql = "select max(cthd.mahoadon) from " + ChitietHoadon.class.getName() + " cthd";
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
    public List<ChitietHoadon> getAll() {
        return null;
    }

    @Override
    public boolean insert(ChitietHoadon obj) {
        Session session = HibernateUtils.getSessionFactory().getCurrentSession();

        try {
            session.getTransaction().begin();

            session.save(obj);

            session.flush();
            System.out.println("Chi tiết hóa đơn dược thêm vào DB:" + obj);

            session.getTransaction().commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            session.getTransaction().rollback();
        }
        return false;
    }

    @Override
    public ChitietHoadon get(int id) {
        return null;
    }

    @Override
    public boolean update(int id, ChitietHoadon newObj) {
        return false;
    }

    @Override
    public boolean delete(ChitietHoadon obj) {
        return false;
    }
}
