package com.huiguli.redisbackend.utils;

import com.huiguli.redisbackend.dto.UserDTO;
import com.huiguli.redisbackend.entity.User;

/**
 * @author huiguli
 */
public class UserHolder {
    private static final ThreadLocal<UserDTO> tl = new ThreadLocal<>();

    public static void saveUser(UserDTO user){
        tl.set(user);
    }

    public static UserDTO getUser(){
        return tl.get();
    }

    public static void removeUser(){
        tl.remove();
    }
}
