package com.paytmmall.spellchecker.metrics;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.paytmmall.spellchecker.constants.APIPoints;
import com.paytmmall.spellchecker.util.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestMonitorFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(RequestMonitorFilter.class);

    @Autowired
    private MessageUtils messageUtils;

    @Autowired
    private MetricsAgent        metricsAgent;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String apiName = ((HttpServletRequest) request).getRequestURI();
            APIPoints apiPoints = APIPoints.deserialize(apiName);
            if (apiPoints == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND,
                        messageUtils.get(HttpServletResponse.SC_NOT_FOUND));
                return;
            }
            metricsAgent.incrementApiCount(apiName);
            long startTime = System.currentTimeMillis();
            filterChain.doFilter(request, response);
            long elapsedTime = System.currentTimeMillis() - startTime;
            metricsAgent.recordResponseCodeCount(apiName, response.getStatus());
            metricsAgent.recordExecutionTimeOfEvent(apiName, elapsedTime);
        } catch (Throwable t) {
            logger.error("Error in datadog request filtering", t);
        }
    }
}
