package uk.gov.laa.gpfd.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.web.filter.OncePerRequestFilter;
import uk.gov.laa.gpfd.utils.RequestLogUtils;

import java.io.IOException;

@Slf4j
public class RequestContextFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        RequestLogUtils.putRequestContext(request);
        try {
            filterChain.doFilter(request, response);
        } finally {
            RequestLogUtils.clearContext();
        }
    }
}
