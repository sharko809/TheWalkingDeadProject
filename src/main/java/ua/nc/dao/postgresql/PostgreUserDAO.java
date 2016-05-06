package ua.nc.dao.postgresql;

import org.apache.log4j.Logger;
import ua.nc.dao.AbstractPostgreDAO;
import ua.nc.dao.UserDAO;
import ua.nc.dao.exception.DAOException;
import ua.nc.entity.Role;
import ua.nc.entity.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;


/**
 * Created by Pavel on 21.04.2016.
 */
public class PostgreUserDAO extends AbstractPostgreDAO<User, Integer> implements UserDAO {
    private static final Logger LOGGER = Logger.getLogger(PostgreUserDAO.class);
    private static final String SQL_UPDATE_USER = "UPDATE public.user SET password = ? WHERE user_id = ?";
    private final String FIND_BY_EMAIL = "SELECT * FROM public.user u WHERE u.email = ?";
    private final String CREATE_USER = "INSERT INTO public.user(name, email, password) VALUES (?, ?, ?)";
    private final String SET_ROLE_TO_USER = "INSERT INTO public.user_role(role_id, user_id) SELECT ?, user_id FROM public.user u WHERE u.email=?";
    public PostgreUserDAO(Connection connection) {
        super(connection);
    }

    @Override
    public User findByEmail(String email) throws DAOException {
        String sql = FIND_BY_EMAIL;
        PersistUser user = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);
            resultSet = statement.executeQuery();
            resultSet.next();
            user = new PersistUser();
            user.setId(resultSet.getInt("user_id"));
            user.setName(resultSet.getString("name"));
            user.setEmail(resultSet.getString("email"));
            user.setPassword(resultSet.getString("password"));
            LOGGER.debug("Find user " + user.getName() + " byEmail");
        } catch (SQLException e) {
            LOGGER.info("User with " + email + " not find in DB");
            throw new DAOException(e);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (Exception e) {
                throw new DAOException(e);
            }
        }
        return user;
    }

    @Override
    public void createUser(User user, Set<Role> roles) throws DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection.setAutoCommit(false);
            statement = connection.prepareStatement(CREATE_USER);
            statement.setString(1, user.getName());
            statement.setString(2, user.getEmail());
            statement.setString(3, user.getPassword());
            statement.executeUpdate();
            if (!roles.isEmpty()) {
                statement = connection.prepareStatement(SET_ROLE_TO_USER);
                for (Role role : roles) {
                    statement.setInt(1, role.getId());
                    statement.setString(2, user.getEmail());
                    statement.addBatch();
                }
                statement.executeBatch();
            }
            connection.commit();
        } catch (SQLException e) {
            LOGGER.debug("User with name" + user.getName() + "  not created");
            try {
                connection.rollback();
            } catch (SQLException e1) {
                LOGGER.warn("Unable to rollback transaction");
                throw new DAOException(e1);
            }
            throw new DAOException(e);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (Exception e) {
                throw new DAOException(e);
            }
        }
    }

    @Override
    public User create(User object) throws DAOException {
        return null;
    }

    @Override
    public String getSelectQuery() {
        return null;
    }

    @Override
    public String getCreateQuery() {
        return null;
    }

    @Override
    public String getUpdateQuery() {
        return null;
    }

    @Override
    public String getAllQuery() {
        return null;
    }

    @Override
    protected List<User> parseResultSet(ResultSet rs) throws DAOException {
        return null;
    }

    @Override
    protected void prepareStatementForInsert(PreparedStatement statement, User object) throws DAOException {

    }

    @Override
    protected void prepareStatementForUpdate(PreparedStatement statement, User object) throws DAOException {

    }



    @Override
    public User persist(User object) throws DAOException {
        return null;
    }

    @Override
    public User read(Integer key) throws DAOException {
        return null;
    }

    @Override
    public void update(User object) throws DAOException {

    }

    @Override
    public List<User> getAll() throws DAOException {
        return null;
    }

    /**
     * Updates user with new password
     *
     * @param user
     * @throws DAOException
     */
    @Override
    public void updateUser(User user) throws DAOException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            statement = connection.prepareStatement(SQL_UPDATE_USER);
            statement.setString(1, user.getPassword());
            statement.setInt(2, user.getId());
            statement.executeUpdate();
        } catch (SQLException e) {
            LOGGER.info("User:" + user.getName() + "  not updated");
            throw new DAOException(e);
        } finally {
            try {
                if (resultSet != null)
                    resultSet.close();
                if (statement != null)
                    statement.close();
            } catch (Exception e) {
                throw new DAOException(e);
            }
        }
    }

    private class PersistUser extends User {
        public PersistUser(Integer id, String name, String email, String password, Set<Role> roles) {
            super(id, name, email, password, roles);
        }

        public PersistUser() {
        }

        @Override
        public void setId(Integer id) {
            super.setId(id);
        }
    }


}
