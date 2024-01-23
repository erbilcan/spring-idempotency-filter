package codes.erbilcan.springboot.idempotency.filter;

import codes.erbilcan.springboot.idempotency.exception.CacheNotFoundException;
import codes.erbilcan.springboot.idempotency.exception.IdempotencyHeaderNotFoundException;
import codes.erbilcan.springboot.idempotency.exception.TooManyRequestsForAnIdempotentEndpoint;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpMethod;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class IdempotencyFilter extends OncePerRequestFilter {

    private final String cacheName;

    private final String idempotencyHeaderName;

    private final CacheManager cacheManager;

    private final HandlerExceptionResolver handlerExceptionResolver;

    public IdempotencyFilter(CacheManager cacheManager,
                             HandlerExceptionResolver handlerExceptionResolver,
                             String cacheName,
                             String header) {
        this.cacheManager = cacheManager;
        this.handlerExceptionResolver = handlerExceptionResolver;

        this.cacheName = cacheName;
        this.idempotencyHeaderName = header;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        if (cacheName == null || idempotencyHeaderName == null) {
            throw new IllegalArgumentException("""
                Parameters cacheName and headerName cannot be found.
                You should activate @EnableIdempotence annotation in your configuration.
                """);
        }

        String idempotencyKey = request.getHeader(idempotencyHeaderName);

        try {
            if (idempotencyKey == null) {
                logger.info("Idempotency key cannot be found in header:" + idempotencyHeaderName);
                throw new IdempotencyHeaderNotFoundException("Idempotency header cannot be found!");
            }

            processIdempotentRequest(idempotencyKey);

        } catch (IdempotencyHeaderNotFoundException
                 | TooManyRequestsForAnIdempotentEndpoint
                 | CacheNotFoundException ex) {
            handlerExceptionResolver.resolveException(request, response, null, ex);
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        Objects.requireNonNull(request);
        Objects.requireNonNull(request.getMethod());
        return HttpMethod.GET.matches(request.getMethod())
                || HttpMethod.PUT.matches(request.getMethod())
                || HttpMethod.DELETE.matches(request.getMethod())
                || HttpMethod.TRACE.matches(request.getMethod())
                || HttpMethod.HEAD.matches(request.getMethod())
                || HttpMethod.OPTIONS.matches(request.getMethod());
    }

    private void processIdempotentRequest(String idempotencyKey)
            throws TooManyRequestsForAnIdempotentEndpoint, CacheNotFoundException {

        if (logger.isDebugEnabled()) {
            logger.debug("Processing Request...");
        }

        Cache cache = Optional.ofNullable(cacheManager.getCache(cacheName))
                .orElseThrow(() -> new CacheNotFoundException(
                        String.format("Cache cannot be found. Cache name: %s", cacheName)));

        Cache.ValueWrapper existingEntry = cache.get(idempotencyKey);

        if (existingEntry == null) {
            cache.putIfAbsent(idempotencyKey, idempotencyKey);
        } else {
            throw new TooManyRequestsForAnIdempotentEndpoint("Too many requests for an idempotent endpoint");
        }

    }

    public String getCacheName() {
        return cacheName;
    }

    public String getIdempotencyHeaderName() {
        return idempotencyHeaderName;
    }
}
