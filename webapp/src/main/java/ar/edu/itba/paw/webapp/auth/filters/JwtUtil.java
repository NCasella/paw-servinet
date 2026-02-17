package ar.edu.itba.paw.webapp.auth.filters;

import ar.edu.itba.paw.webapp.auth.ServinetAuthUserDetails;
import io.jsonwebtoken.*;

import io.jsonwebtoken.security.SignatureException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Component
@PropertySource("classpath:application.properties")
public class JwtUtil {

    @Autowired
    private UserDetailsService userDetailsService;
    @Value("${jwt.key}")
    private String SECRET_KEY;

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

        Long userId = null;
        if (userDetails instanceof ServinetAuthUserDetails servinetUser) {
            userId = servinetUser.getUserId();
        }
        List<String> roles = userDetails.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .claim("id", userId)
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtTypes.ACCESS_TOKEN.tokenDuration))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    public String generateRefreshToken(UserDetails userDetails) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtTypes.REFRESH_TOKEN.tokenDuration))
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
    public UserDetails extractUserDetails(String token){
        String username= this.extractUsername(token);
        if(username==null){
            return null;
        }
        try{
            return userDetailsService.loadUserByUsername(username);
        }catch (UsernameNotFoundException e){
            return null;
        }
    }
    public UserDetails loginUser(String jwtToken){
        UserDetails userDetails= this.extractUserDetails(jwtToken);
        if(userDetails!=null && this.validateToken(jwtToken,userDetails) && userDetails.isEnabled()){
            UsernamePasswordAuthenticationToken authToken=new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authToken);
        }
        return userDetails;
    }
    private enum JwtTypes{
        ACCESS_TOKEN(1000*60*60*12),
        REFRESH_TOKEN(1000*60*60*24*7);

        private final int tokenDuration;
        JwtTypes(int tokenDuration){this.tokenDuration=tokenDuration;}
    }
}
