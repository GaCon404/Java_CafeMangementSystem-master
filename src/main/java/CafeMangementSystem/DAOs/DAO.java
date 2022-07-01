package CafeMangementSystem.DAOs;

import java.util.List;

public interface DAO<T> {
    int getMaxId();

    List<T> getAll(); // R

    boolean insert(T obj); // C

    T get(int id); // R

    boolean update(int id, T newObj); // U

    boolean delete(T obj); // D
}
