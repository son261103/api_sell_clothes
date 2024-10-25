package com.example.api_sell_clothes.Security;

import com.example.api_sell_clothes.Service.JwtService;
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

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    // jwtService được dùng để xử lý các logic liên quan đến JWT như lấy thông tin username, kiểm tra tính hợp lệ của token.

    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws IOException, ServletException {

        final String authHeader = request.getHeader("Authorization");
        // Lấy giá trị của header Authorization từ request (thường chứa token JWT).

        final String jwt;
        final String username;

        // Kiểm tra nếu header Authorization không tồn tại hoặc không bắt đầu bằng "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Kết thúc nếu không có token JWT.
        }

        // Trích xuất JWT từ header Authorization (bỏ qua tiền tố "Bearer ")
        jwt = authHeader.substring(7);

        // Lấy username từ token bằng phương thức extractUsername của jwtService
        username = jwtService.extractUsername(jwt);

        // Kiểm tra nếu username không null và chưa có người dùng nào được xác thực trong SecurityContext
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Tải thông tin người dùng từ UserDetailsService dựa trên username
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // Kiểm tra token có hợp lệ không bằng phương thức isTokenValid của jwtService
            if (jwtService.isTokenValid(jwt)) {
                // Tạo đối tượng UsernamePasswordAuthenticationToken để xác thực người dùng
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                // Đặt đối tượng xác thực vào SecurityContext
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // Tiếp tục chuyển yêu cầu qua filter chain (các bộ lọc tiếp theo)
        filterChain.doFilter(request, response);
    }
}
