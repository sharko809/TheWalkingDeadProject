package ua.nc.dao;

import ua.nc.dao.exception.DAOException;
import ua.nc.entity.Role;
import ua.nc.entity.User;

import java.util.Set;

/**
 * Created by Pavel on 21.04.2016.
 */
public interface UserDAO extends GenericDAO<User, Integer> {
    User findByEmail(String email) throws DAOException;

    void createUser(User user, Set<Role> roles) throws DAOException;

    void updateUser(User user) throws DAOException;
}
