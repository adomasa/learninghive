package distributed.monolith.learninghive.security;

import distributed.monolith.learninghive.model.constants.Paths;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
	private final JwtTokenProvider jwtTokenProvider;

	//todo potential security issue. Node should be defined in cors config
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
		corsConfiguration.addAllowedMethod("OPTIONS");
		corsConfiguration.addAllowedMethod("PUT");
		corsConfiguration.addAllowedMethod("DELETE");
		corsConfiguration.addExposedHeader("Access-Control-Allow-Origin");
		source.registerCorsConfiguration("/**", corsConfiguration);
		return source;
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		http.cors()
				.and()
				.csrf().disable() //redundant thing for jwt token
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.addFilterBefore(new JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class)
				.authorizeRequests()
//				.antMatchers(Paths.ACCOUNT_LOGIN).permitAll()
//				.antMatchers(Paths.ACCOUNT_REGISTER).permitAll()
//				.antMatchers(Paths.ACCOUNT_REFRESH).permitAll()
				.anyRequest().authenticated()
				.and()
				.exceptionHandling()
				.authenticationEntryPoint((request, response, authException) -> {
					// we can point explicitly to register/login/refresh URL
					response.setHeader("WWW-Authenticate", "Bearer");
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
				});
	}

	@Override
	public void configure(WebSecurity web) {
		// todo temporary workaround since jwt security filter  goes on all requests despite config
		web.ignoring().antMatchers(
				Paths.ACCOUNT_LOGIN,
				Paths.ACCOUNT_REGISTER,
				Paths.ACCOUNT_REFRESH
		);
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}
