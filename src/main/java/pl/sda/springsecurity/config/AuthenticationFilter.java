package pl.sda.springsecurity.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
//4. Piszemy filtr
@Component
public class AuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;//(z pakietu spring security)

    public AuthenticationFilter(JwtService jwtService, UserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String jwtHeader = request.getHeader("Authorization");//pobieramy header(przychodzi z frontu)
        if (jwtHeader == null || !jwtHeader.startsWith("Bearer")) {// może być Basic lub Bearer -Łapiemy Bearer bo taki jest standard
            //Basic login:password kodowane do base64 - Bearer ma dodatkowo token
            filterChain.doFilter(request, response);//może być kilka filtrów np. do sprawdzania ip
            return;
        }
        String token = jwtHeader.substring(7);//usuwamy Bearer i spację bo chcemy sam token
        String userName = jwtService.extractUserName(token);//wyciągamy claima - pole w prostym obiekcie
        //Claims (my to tworzymy)
//        {
//            "sub": "1234567890", (coś unikalnego dla usera np id uuid lub username - ważny) u nas jest to username
//                "name": "John Doe",
//                "admin": true
//        }
//                                  (Sprawdzamy czy użytkownik już nie jest zautentkowany w inny sposób)
        if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
            if (jwtService.isValidForUser(token, userDetails)) {
                //ustawiamy w kontekscie że użytkownik to nasz użytkownik(ustawiamy go do kontekstu)                          nie ma loginu i hasła bo jest token
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                //dodajemy deteils i opakowujemy w WebAuthenticationDetailSource
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
            filterChain.doFilter(request, response);
        }
    }

}
