package org.example.expert.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.common.exception.AdminException;
import org.example.expert.domain.user.enums.UserRole;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;
import java.io.InvalidClassException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter implements Filter {

    private final JwtUtil jwtUtil;
    private static final String[] WHITE_LIST = {"/","/auth"};

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        Filter.super.init(filterConfig);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String url = httpRequest.getRequestURI();

        if (!isWhiteList(url)) {
            chain.doFilter(request, response);
            return;
        }

        String bearerJwt = httpRequest.getHeader("Authorization");

        if (bearerJwt == null) {
            throw new InvalidRequestException("JWT 토큰이 필요합니다.");
        }

        String jwt = jwtUtil.substringToken(bearerJwt);

        //데이터 처리와 유효성 검사 책임분리 필요. 그지같아서 일단 패스하고 exception 처리하기로 함.
        try {
            // JWT 유효성 검사와 claims 추출을 왜 여기서 한번에?
            Claims claims = jwtUtil.extractClaims(jwt);
            if (claims == null) {
                throw new InvalidClassException("잘못된 JWT 토큰입니다.");
            }

            UserRole userRole = UserRole.valueOf(claims.get("userRole", String.class));

            //과연 setAttribute로 끝내는 게 맞는가? set을 필터에..? 쓰지 말고 서비스에서 쓸 것이 아닌가?
            httpRequest.setAttribute("userId", Long.parseLong(claims.getSubject()));
            httpRequest.setAttribute("email", claims.get("email"));
            httpRequest.setAttribute("userRole", claims.get("userRole"));

            /*
            관리자 권한은 화이트 리스트에 추가할 수 없는 고유권한이므로 url.startsWith으로 관리 ? 일단 겁나 이상한 건 확실하다.
             */
            if (!UserRole.ADMIN.equals(userRole)) {
                throw new AdminException("관리자 권한이 없습니다.");
            }

            if (url.startsWith("/admin")) {
                chain.doFilter(request, response);
                return;
            }

            chain.doFilter(request, response);
        } catch (SecurityException | MalformedJwtException e) {
            log.error("Invalid JWT signature, 유효하지 않는 JWT 서명 입니다.", e);
            throw new AuthException("유효하지 않는 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.error("Expired JWT token, 만료된 JWT token 입니다.", e);
            throw new AuthException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.error("Unsupported JWT token, 지원되지 않는 JWT 토큰 입니다.", e);
            throw new InvalidClassException("지원되지 않는 JWT 토큰입니다.");
        } catch (Exception e) {
            log.error("Invalid JWT token, 유효하지 않는 JWT 토큰 입니다.", e);
            throw new InvalidClassException("유효하지 않는 JWT 토큰입니다.");
        }
    }

    @Override
    public void destroy() {
        Filter.super.destroy();
    }

    private boolean isWhiteList(String requsetURI) {
        return PatternMatchUtils.simpleMatch(WHITE_LIST, requsetURI);
    }

}
