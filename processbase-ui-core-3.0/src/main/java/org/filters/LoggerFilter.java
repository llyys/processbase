package org.filters;

import org.apache.commons.lang.time.StopWatch;
import org.springframework.web.filter.OncePerRequestFilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicLong;

public class LoggerFilter extends OncePerRequestFilter {

    /** The slf4j logger used by the class */
    private static final transient Logger LOG = LoggerFactory.getLogger(LoggerFilter.class);

    private static final AtomicLong COUNTER = new AtomicLong();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        try {
            synchronized (this) {
                COUNTER.incrementAndGet();
                MDC.put("REQUEST_ID", COUNTER.toString());
            }

            String requestUri = request.getRequestURI();
            LOG.info("Request begins {}", requestUri);

            StopWatch watch = new StopWatch();
            filterChain.doFilter(request, response);
            watch.stop();

            LOG.info("Request {} ended, took {}", requestUri, watch.getSplitTime());
        } finally {
            MDC.remove("REQUEST_ID");
        }
    }

}