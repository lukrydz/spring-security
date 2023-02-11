package pl.sda.springsecurity.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.function.Function;

@Service
public class JwtService {

    private String secret ="404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";


    public <T> T  extractClaim (String token, Function<Claims, T> claimsTFunction){
        final Claims claims = extractAll(token);
        return claimsTFunction.apply(claims);
    }
    private Claims extractAll(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey(){
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }
    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }
}
