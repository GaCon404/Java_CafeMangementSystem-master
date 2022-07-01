package CafeMangementSystem.DAOs;

import CafeMangementSystem.Entities.LichsuGiamon;
import CafeMangementSystem.Utils.HibernateUtils;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;

import java.util.List;

public class LichsuGiamonDAO{
    private LichsuGiamonDAO() {
    }
    private static class InnerDAO{
        private static LichsuGiamonDAO DAO = new LichsuGiamonDAO();
    }

    public static LichsuGiamonDAO getInstance(){
        return InnerDAO.DAO;
    }

    public List<LichsuGiamon> getLS_FromMonmenuID (int idMonmenu){
        Session session = HibernateUtils.getSessionFactory().openSession();
        List<LichsuGiamon> list = null;
        try{
            session.getTransaction().begin();
            String hql = "select ls from LichsuGiamon ls where ls.mamon = :idMonmenu order by ls.thoidiemcapnhat desc";
            Query query = session.createQuery(hql);
            query.setParameter("idMonmenu", idMonmenu);
            list = query.list();
            session.getTransaction().commit();
        }catch(Exception ex){
            ex.printStackTrace(System.err);
            session.getTransaction().rollback();
        }
        session.close();
        return list;
    }
}
