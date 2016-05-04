package ua.nc.service.user;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ua.nc.dao.RoleDAO;
import ua.nc.dao.UserDAO;
import ua.nc.dao.enums.DataBaseType;
import ua.nc.dao.exception.DAOException;
import ua.nc.dao.factory.DAOFactory;
import ua.nc.entity.Role;
import ua.nc.entity.User;
import ua.nc.service.MailService;
import ua.nc.service.MailServiceImpl;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Pavel on 18.04.2016.
 */
public class UserServiceImpl implements UserService {
    private DAOFactory daoFactory = DAOFactory.getDAOFactory(DataBaseType.POSTGRESQL);
    private UserDAO userDAO = daoFactory.getUserDAO();
    private RoleDAO roleDAO = daoFactory.getRoleDAO();
    private MailService mailService = new MailServiceImpl();

    @Override
    public User getUser(String email) {
        User user = null;
        try {
            user = userDAO.findByEmail(email);
            user.setRoles(roleDAO.findByEmail(email));
        } catch (DAOException e) {
            System.out.println("DB exception");
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        user.setPassword(encoder.encode(user.getPassword()));
        Set<Role> roles = new HashSet<>();
        try {
            roles.add(roleDAO.findByName("ROLE_STUDENT"));
            for (Role role : roles)
                System.out.println(role.getName());
            user.setRoles(roles);
            userDAO.createUser(user);
            roleDAO.setRoleToUser(user.getRoles(), user);
            mailService.sendMail(user.getEmail(), "Registration", "Welcome " + user.getName() + " ! \n NetCracker[TheWalkingDeadTeam] ");
            return user;
        } catch (DAOException e) {
            System.out.println("DB exception"); //toDo log4j
            return null;
        }


    }

    @Override
    public User recoverPass(User user) {
        String testPassword = RandomStringUtils.randomAlphanumeric(10);
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        try {
            user.setPassword(encoder.encode(testPassword));
            userDAO.updateUser(user);
            mailService.sendMail(user.getEmail(), "Password recovery", "Welcome " + user.getName() + " ! \n NetCracker[TheWalkingDeadTeam] " + user.getPassword());
            return user;
        } catch (DAOException e) {
            System.out.println("DB exception"); //toDo log4j
            return null;
        }
    }
}
