package it.uniroma3.siw.tool;

import java.util.Collection;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import it.uniroma3.siw.model.Credentials;

/* CLASSE USATA ESCLUSIVAMENTE PER FUNZIONI UTILI RIGUARDO L'AUTENTICAZIONE */
public class AuthUtil {

	private static final String ADMIN_REDIRECT = "admin/";

	/* CHECK IF GIVEN THE AUTHENTICATION THE USER HAS ADMIN ROLE */
	public static boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		if (!(authentication instanceof AnonymousAuthenticationToken))
			for (GrantedAuthority ga : authorities)
				if (ga.toString().equals(Credentials.ADMIN_ROLE))
					return true;
		return false;
	}

	/*
	 * PARSES THE LINK TO AN ADMIN TEMPLATE OR NOT IF AUTHENTICATED AS ADMIN (WORKS
	 * ONLY IF THE TEMPLATE HAS THE SAME NAME)
	 */
	public static String parseLink(String link) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		if (!(authentication instanceof AnonymousAuthenticationToken))
			for (GrantedAuthority ga : authorities)
				if (ga.toString().equals(Credentials.ADMIN_ROLE))
					return ADMIN_REDIRECT + link;
		return link;
	}

}
