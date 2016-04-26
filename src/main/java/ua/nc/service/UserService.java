package ua.nc.service;

import org.springframework.stereotype.Service;
import ua.nc.entity.User;

/**
 * Created by Pavel on 18.04.2016.
 */
public interface UserService {
    User getUser(String email);

    User createUser(User user);
}