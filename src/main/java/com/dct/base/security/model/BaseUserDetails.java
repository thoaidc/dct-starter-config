package com.dct.base.security.model;

import com.dct.base.entity.IAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class BaseUserDetails extends User {

    private final IAccount account;
    private final Set<String> authorities = new HashSet<>();

    public BaseUserDetails(IAccount account,
                           Collection<? extends GrantedAuthority> authorities,
                           boolean accountEnabled,
                           boolean accountNonExpired,
                           boolean accountNonLocked) {
        super(
            account.getUsername(),
            account.getPassword(),
            accountEnabled,
            accountNonExpired,
            true,
            accountNonLocked,
            authorities
        );

        this.account = account;
        this.authorities.addAll(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
    }

    public IAccount getAccount() {
        return account;
    }

    public Set<String> getSetAuthorities() {
        return authorities;
    }

    public static final class Builder {
        private IAccount account;
        private Collection<? extends GrantedAuthority> authorities;
        private boolean accountEnabled = true;
        private boolean accountNonExpired = true;
        private boolean accountNonLocked = true;

        public Builder account(IAccount account) {
            this.account = account;
            return this;
        }

        public Builder authorities(Collection<? extends GrantedAuthority> authorities) {
            this.authorities = authorities;
            return this;
        }

        public Builder enabled(boolean accountEnabled) {
            this.accountEnabled = accountEnabled;
            return this;
        }

        public Builder nonExpired(boolean accountNonExpired) {
            this.accountNonExpired = accountNonExpired;
            return this;
        }

        public Builder nonLocked(boolean accountNonLocked) {
            this.accountNonLocked = accountNonLocked;
            return this;
        }

        public BaseUserDetails build() {
            return new BaseUserDetails(account, authorities, accountEnabled, accountNonExpired, accountNonLocked);
        }
    }
}
