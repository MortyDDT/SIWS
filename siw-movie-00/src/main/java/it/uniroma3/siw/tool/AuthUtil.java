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

	/*
	 * PARSES THE LINK TO AN ADMIN TEMPLATE OR NOT DEPENDING ON THE ROLE (WORKS
	 * ONLY IF THE TEMPLATE HAS THE SAME NAME)
	 */
	public static String parseLink(String link) {
		if (isAdmin())
			return ADMIN_REDIRECT + link;
		return link;
	}

	/* CHECKS IF THE AUTHENTICATED USER HAS ADMIN ROLE */
	public static boolean isAdmin() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

		if (isUserAuthenticated())
			for (GrantedAuthority ga : authorities)
				if (ga.toString().equals(Credentials.ADMIN_ROLE))
					return true;
		return false;
	}

	/* RETURNS THE USERNAME OF THE CURRENT LOGGED IN USER */
	public static String getCurrentUsername() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (isUserAuthenticated())
			return authentication.getName();
		return null;
	}

	/* RETURNS TRUE IF THERE'S AN USER LOGGED IN */
	public static boolean isUserAuthenticated() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken)
			return false;
		return true;
	}

}
