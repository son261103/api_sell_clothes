package com.example.api_sell_clothes.Service;

import com.example.api_sell_clothes.Entity.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-token.expiration}")
    private long refreshExpiration;

    private final UserDetailsService userDetailsService;

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return (List<String>) claims.getOrDefault("roles", new ArrayList<>());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    public String generateToken(Users user) {
        return generateToken(new HashMap<>(), user);
    }

    public String generateToken(Map<String, Object> extraClaims, Users user) {
        return buildToken(extraClaims, user, jwtExpiration);
    }

    public String generateRefreshToken(Users user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }

    private String buildToken(Map<String, Object> extraClaims, Users user, long expiration) {
        // Add roles to claims
        List<String> roles = user.getRoles().stream()
                .map(role -> "ROLE_" + role.getRoleName())
                .collect(Collectors.toList());
        extraClaims.put("roles", roles);

        // Add user details to claims
        extraClaims.put("userId", user.getUserId());
        extraClaims.put("email", user.getEmail());
        extraClaims.put("fullName", user.getFullName());

        return Jwts.builder()
                .setClaims(extraClaims)
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isTokenValid(String token) {
        try {
            final String username = extractUsername(token);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Verify username matches
            if (!username.equals(userDetails.getUsername())) {
                return false;
            }

            // Verify token is not expired
            if (isTokenExpired(token)) {
                return false;
            }

            // Verify roles match
            List<String> tokenRoles = extractRoles(token);
            List<String> userRoles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList());

            return new HashSet<>(tokenRoles).equals(new HashSet<>(userRoles));

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public void invalidateToken(String token) {
        // In a production environment, you might want to:
        // 1. Add the token to a blacklist
        // 2. Store it in Redis/database with expiration
        // 3. Implement token revocation
        System.out.println("Token đã bị vô hiệu hóa / Token invalidated: " + token);
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(getSignInKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            throw new JwtException("Token JWT đã hết hạn / JWT token has expired", e);
        } catch (UnsupportedJwtException e) {
            throw new JwtException("Token JWT không được hỗ trợ / Unsupported JWT token", e);
        } catch (MalformedJwtException e) {
            throw new JwtException("Token JWT không hợp lệ / Invalid JWT token", e);
        } catch (SignatureException e) {
            throw new JwtException("Chữ ký JWT không hợp lệ / Invalid JWT signature", e);
        } catch (IllegalArgumentException e) {
            throw new JwtException("Chuỗi claims JWT rỗng / JWT claims string is empty", e);
        }
    }

    private SecretKey getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Long extractUserId(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return claims.get("userId", Long.class);
        } catch (Exception e) {
            return null;
        }
    }

    public Map<String, Object> extractAllUserDetails(String token) {
        try {
            Claims claims = extractAllClaims(token);
            Map<String, Object> userDetails = new HashMap<>();
            userDetails.put("userId", claims.get("userId"));
            userDetails.put("username", claims.getSubject());
            userDetails.put("email", claims.get("email"));
            userDetails.put("fullName", claims.get("fullName"));
            userDetails.put("roles", claims.get("roles"));
            return userDetails;
        } catch (Exception e) {
            return new HashMap<>();
        }
    }

    public long getExpirationTime() {
        return jwtExpiration;
    }

    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
}