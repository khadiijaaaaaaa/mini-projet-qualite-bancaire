package com.hendisantika.onlinebanking.service.UserServiceImpl;

import com.hendisantika.onlinebanking.entity.User;
import com.hendisantika.onlinebanking.repository.RoleDao;
import com.hendisantika.onlinebanking.repository.UserDao;
import com.hendisantika.onlinebanking.security.UserRole;
import com.hendisantika.onlinebanking.service.AccountService;
import com.hendisantika.onlinebanking.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Transactional
@Primary
@Service("userServiceImpl")
public class UserServiceImpl implements UserService {

    private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final UserDao userDao;
    private final RoleDao roleDao;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;

    public UserServiceImpl(
            UserDao userDao,
            RoleDao roleDao,
            BCryptPasswordEncoder passwordEncoder,
            AccountService accountService
    ) {
        this.userDao = userDao;
        this.roleDao = roleDao;
        this.passwordEncoder = passwordEncoder;
        this.accountService = accountService;
    }

    @Override
    public void save(User user) {
        userDao.save(user);
    }

    @Override
    public User findByUsername(String username) {
        return userDao.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    @Override
    public User createUser(User user, Set<UserRole> userRoles) {
        User localUser = userDao.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("User with username {} already exist. Nothing will be done.", user.getUsername());
            return localUser;
        }

        // encrypt password
        String encryptedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encryptedPassword);

        // save roles
        for (UserRole ur : userRoles) {
            roleDao.save(ur.getRole());
        }

        user.getUserRoles().addAll(userRoles);

        // create accounts
        user.setPrimaryAccount(accountService.createPrimaryAccount());
        user.setSavingsAccount(accountService.createSavingsAccount());

        return userDao.save(user);
    }

    @Override
    public boolean checkUserExists(String username, String email) {
        return checkUsernameExists(username) || checkEmailExists(email);
    }

    @Override
    public boolean checkUsernameExists(String username) {
        return findByUsername(username) != null;
    }

    @Override
    public boolean checkEmailExists(String email) {
        return findByEmail(email) != null;
    }

    @Override
    public User saveUser(User user) {
        return userDao.save(user);
    }

    @Override
    public List<User> findUserList() {
        return userDao.findAll();
    }

    @Override
    public void enableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(true);
        userDao.save(user);
    }

    @Override
    public void disableUser(String username) {
        User user = findByUsername(username);
        user.setEnabled(false);
        userDao.save(user);
        LOG.info("User {} is disabled.", username);
    }
}
