package com.zeldaspeedruns.zeldaspeedruns.security.userdetails;

import com.zeldaspeedruns.zeldaspeedruns.organizations.OrganizationMemberRepository;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUser;
import com.zeldaspeedruns.zeldaspeedruns.security.user.ZsrUserService;
import org.springframework.data.util.Streamable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ZsrUserDetailsService implements UserDetailsService {
    private final ZsrUserService userService;
    private final OrganizationMemberRepository memberRepository;

    public ZsrUserDetailsService(ZsrUserService userService, OrganizationMemberRepository memberRepository) {
        this.userService = userService;
        this.memberRepository = memberRepository;
    }

    @Override
    public ZsrUserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return load(userService.loadByUsername(username));
    }

    /**
     * Loads and constructs a user details principal from a user domain model.
     * @param zsrUser The user domain model.
     * @return Instantiated authentication principal.
     */
    public ZsrUserDetails load(ZsrUser zsrUser) {
        // Fetch all of our user's organization memberships and roles.
        var memberships = Streamable.of(memberRepository.findAllByUser(zsrUser)).toSet();
        return new ZsrUserDetails(zsrUser, memberships);
    }
}
