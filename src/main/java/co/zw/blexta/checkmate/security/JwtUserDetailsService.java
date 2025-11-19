package co.zw.blexta.checkmate.security;

import co.zw.blexta.checkmate.auth.users.AuthUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class JwtUserDetailsService implements UserDetailsService {

	private final AuthUserRepository authUserRepository;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return authUserRepository.findByEmail(email).orElseThrow(() -> {
			return new UsernameNotFoundException("User not found: " + email);
		});

	}

}