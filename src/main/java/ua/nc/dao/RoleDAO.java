package ua.nc.dao;

import org.apache.log4j.Logger;
import sun.rmi.runtime.Log;
import ua.nc.dao.exception.DAOException;
import ua.nc.dao.pool.ConnectionPool;
import ua.nc.entity.Role;
import ua.nc.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Set;

/**
 * Created by Neltarion on 23.04.2016.
 */
public abstract class RoleDAO {

    private static final Logger LOGGER = Logger.getLogger(RoleDAO.class);

    public abstract Role findByName(String name) throws DAOException;

    public abstract Set<Role> findByEmail(String email) throws DAOException;

    public abstract void setRoleToUser (Set<Role> roles, User user) throws DAOException;

    public void closeConnStmt(ConnectionPool connectionPool, Connection connection, PreparedStatement preparedStatement, ResultSet resultSet) {
        try {
            if (resultSet != null)
                resultSet.close();
            if (preparedStatement != null)
                preparedStatement.close();
            if (connection != null)
                connectionPool.putConnection(connection);
        } catch (SQLException e) {
            LOGGER.error(e);
        } catch (DAOException e) {
            LOGGER.error(e);
        }
    }

}
