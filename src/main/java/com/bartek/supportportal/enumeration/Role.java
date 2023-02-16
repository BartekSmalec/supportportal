package com.bartek.supportportal.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import static com.bartek.supportportal.constant.Authority.*;

@AllArgsConstructor
@Getter
public enum Role {
    ROLE_USER(USER_AUTHORITIES),
    ROLE_HR(HR_AUTHORITIES),
    ROLE_MANAGER(MANAGER_AUTHORITIES),
    ROLE_ADMIN(ADMIN_AUTHORITIES),
    ROLE_SUPER_ADMIN(SUPER_ADMIN_AUTHORITIES);

    private String[] authorities;
}
