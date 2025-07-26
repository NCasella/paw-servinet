package ar.edu.itba.paw.webapp.auth.filters;

import io.jsonwebtoken.*;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.function.Function;

@Component
@PropertySource("classpath:application.properties")
public class JwtUtil {

    @Value("${jwt.key}")
    private String SECRET_KEY;

    private static final int TOKEN_DURATION=1000*60*60*12; //ms

    public String extractUsername(String token) {
        try {
            return extractClaim(token, Claims::getSubject);
        }
        catch (ExpiredJwtException | SignatureException| MalformedJwtException| UnsupportedJwtException | IllegalArgumentException e){
            return null;
        }
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token).getBody();
    }

    public String generateToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_DURATION))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        String username=extractUsername(token);
        return username!=null && username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractClaim(token, Claims::getExpiration).before(new Date());
    }
}
