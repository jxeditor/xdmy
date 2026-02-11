package com.xdmy.dao.inter;

import com.xdmy.domain.User;

import java.util.List;

public interface IAdminDao {
    List<User> verifyLogin(User user);
}
