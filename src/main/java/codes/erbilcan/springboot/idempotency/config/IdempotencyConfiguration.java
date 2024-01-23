package codes.erbilcan.springboot.idempotency.config;

import codes.erbilcan.springboot.idempotency.exception.IdempotencyHeaderNotFoundException;
import codes.erbilcan.springboot.idempotency.exception.TooManyRequestsForAnIdempotentEndpoint;
import codes.erbilcan.springboot.idempotency.filter.IdempotencyFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cache.CacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Configuration
public class IdempotencyConfiguration {

    @Value("${idempotency.cachename}")
    private String cacheName;

    @Value("${idempotency.header}")
    private String header;

    @Bean
    @ConditionalOnProperty({"idempotency.cachename", "idempotency.header"})
    public IdempotencyFilter createFilter(CacheManager cacheManager,
                                          @Qualifier("handlerExceptionResolver") HandlerExceptionResolver handlerExceptionResolver) {

        return new IdempotencyFilter(cacheManager,
                handlerExceptionResolver,
                cacheName,
                header);
    }

    @Bean
    public FilterRegistrationBean<IdempotencyFilter> idempotencyFilterRegistration(IdempotencyFilter filter) {

        FilterRegistrationBean<IdempotencyFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(filter);
        return registrationBean;
    }

    @RestControllerAdvice
    private class IdempotencyExceptionHandler extends ResponseEntityExceptionHandler {
        @ExceptionHandler(value
                = { IdempotencyHeaderNotFoundException.class })
        protected ResponseEntity<Object> handleConflict(
                IdempotencyHeaderNotFoundException ex, WebRequest request) {

            String bodyOfResponse = "Header not found";
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.BAD_REQUEST, request);
        }

        @ExceptionHandler(value
                = { TooManyRequestsForAnIdempotentEndpoint.class })
        protected ResponseEntity<Object> handleTooManyRequestsError(
                TooManyRequestsForAnIdempotentEndpoint ex, WebRequest request) {

            String bodyOfResponse = "Too many requests";
            return handleExceptionInternal(ex, bodyOfResponse,
                    new HttpHeaders(), HttpStatus.TOO_MANY_REQUESTS, request);
        }
    }

    public String getCacheName() {
        return cacheName;
    }

    public void setCacheName(String cacheName) {
        this.cacheName = cacheName;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }
}