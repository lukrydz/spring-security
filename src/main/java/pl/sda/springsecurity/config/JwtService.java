package pl.sda.springsecurity.config;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private String secret = "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970";
//można przenieść secret do app.properties

    //metoda wyciąga z tokena konkretnego claima
    public <T> T extractClaim(String token, Function<Claims, T> claimsTFunction) {
        final Claims claims = extractAll(token);//wyciagamy claimsy
        return claimsTFunction.apply(claims);//używamy funkcji na wyciągniętych claimsach(np. linijka 40(getSubject))
    }

    private Claims extractAll(String token) {
        return Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token).getBody();
    }

    private Key getSigningKey() {
        byte[] bytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(bytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    public boolean isValidForUser(String token, UserDetails userDetails) {
        return extractUserName(token).equals(userDetails.getUsername()) && !isTokenExpired(token);
        //pobieramy usera z bazy i porównujemy username z username z tokena
    }

    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    private String generateToken(Map<String, Object> claimsMap, UserDetails userDetails) {
        return Jwts.builder()
                .setClaims(claimsMap)//możana dodać claimsy na zasadzie klucz wartość  w hashmapie
                .setSubject(userDetails.getUsername())//na podstawie username identyfikujemy usera - tu ustawiamy username
                .setExpiration(new Date(new Date().getTime() + 60 * 60 * 24 * 1000))//getTime w milisekundach
                .setIssuedAt(new Date())
                //zapisujemy jakiego kodowania używamy do kodowania podpisu(kodowanie i hashowanie)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    // po rozkodawaiu tokena dostajemy 3 claimy

    //eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJLcnp5c2llayIsImV4cCI6MTY3NjY1NjY5MCwiaWF0IjoxNjc2NTcwMjkwfQ.qNm90CNUnHGqFssmEusSmZiCnecGXJssAl1fYt60bCE
//        header
//    {
//        "alg": "HS256"
//    }

//       payload
//    {
//        "sub": "Krzysiek",
//            "exp": 1676656690,
//            "iat": 1676570290
//    }


    //signature - secret kodowany w base64
}
