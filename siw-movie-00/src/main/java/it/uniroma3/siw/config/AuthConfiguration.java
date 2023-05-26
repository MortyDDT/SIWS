package it.uniroma3.siw.config;

import static it.uniroma3.siw.model.Credentials.ADMIN_ROLE;
//import static it.uniroma3.siw.model.Credentials.DEFAULT_ROLE;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@SuppressWarnings("deprecation")
@Configuration
@EnableWebSecurity
public class AuthConfiguration extends WebSecurityConfigurerAdapter {

	/**
	 * La sorgente dati (che contiene le credenziali) è
	 * iniettata automaticamente
	 */
	@Autowired
	DataSource datasource;

	/**
	 * Questo metodo contiene le impostazioni della configurazione
	 * di autenticatzione e autorizzazione.
	 */
	@Override
	protected void configure(HttpSecurity http) throws Exception {

		String[] staticResources = {
				"/css/**",
				"/images/**",
				"/fonts/**",
				"/scripts/**",
				"favicon.ico"
		};

		String[] publicGetMethods = {
				"/",
				"/index",
				"/login",
				"/register",
				"/movies",
				"/movies/{id}",
				"/movie",
				"/artists",
				"/artists/{id}",
				"/artist",
				"/formSearchMovies"
		};

		String[] publicPostMethods = {
				"/login",
				"/register",
				"/searchMovies"
		};

		http
			// AUTORIZZAZIONE: qui definiamo chi può accedere a cosa
			.authorizeRequests(authorizeRequests -> authorizeRequests
				.antMatchers(staticResources).permitAll() // tutti possono accedere a publicPost/GetMethods e staticResources
				.antMatchers(HttpMethod.GET, publicGetMethods).permitAll()
				.antMatchers(HttpMethod.POST, publicPostMethods).permitAll()
				.antMatchers(HttpMethod.GET, "/admin/**").hasAnyAuthority(ADMIN_ROLE) // solo utenti ADMIN possono accedere a risorse con path /admin/**
				.antMatchers(HttpMethod.POST, "/admin/**").hasAnyAuthority(ADMIN_ROLE)
				.anyRequest().authenticated() // per accedere a tutte le altre risorse devi essere autenticato
			)
			.exceptionHandling(exceptionHandling -> exceptionHandling
				.accessDeniedPage("/index")
			)
			// LOGIN: qui definiamo come è gestita l'autenticazione
			.formLogin(formLogin -> formLogin // usiamo il protocollo formlogin
				.loginPage("/login") // la pagina di login si trova a /login
				.defaultSuccessUrl("/success", true) // se il login ha successo, si viene rediretti al path /default
			)
			// LOGOUT: qui definiamo il logout
			.logout(logout -> logout
				.logoutUrl("/logout") // il logout è attivato con una richiesta GET a "/logout"
				.logoutSuccessUrl("/") // in caso di successo, si viene reindirizzati alla home
				.invalidateHttpSession(true)
				.deleteCookies("JSESSIONID")
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.clearAuthentication(true).permitAll()
			);

	}

	/**
	 * Questo metodo definisce le query SQL per ottenere username e password
	 */
	@Override
	public void configure(AuthenticationManagerBuilder auth) throws Exception {
		auth.jdbcAuthentication()
			.dataSource(this.datasource)
			.authoritiesByUsernameQuery("SELECT username, role FROM credentials WHERE username=?") // query per recuperare username e ruolo
			.usersByUsernameQuery("SELECT username, password, 1 as enabled FROM credentials WHERE username=?"); // query per username e password. enabled specifica se l'utente user è abilitato o no (va sempre a true)
	}

	/**
	 * Questo metodo definisce il componente "passwordEncoder",
	 * usato per criptare e decriptare la password nella sorgente dati.
	 */
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
