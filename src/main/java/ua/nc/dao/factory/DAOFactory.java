package ua.nc.dao.factory;

import ua.nc.dao.RoleDAO;
import ua.nc.dao.UserDAO;
import ua.nc.dao.enums.DataBaseType;
import ua.nc.dao.postgresql.PostgreDAOFactory;

/**
 * Created by Pavel on 21.04.2016.
 */
public abstract class DAOFactory {
    public static DAOFactory getDAOFactory(DataBaseType type) {
        switch (type) {
            case POSTGRESQL: {
                return new PostgreDAOFactory();
            }
            default: {
                return new PostgreDAOFactory();
            }
        }
    }

    public abstract UserDAO getUserDAO(/*Role role*/);

    public abstract RoleDAO getRoleDAO();
}
