package com.paytmmall.spellchecker.metrics;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.StatsDClientErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;


@Component
public class MetricsAgent {

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsAgent.class);
    private StatsDClient prometheusClient;

    @Value("${datadog.prefix}")
    private String DATADOG_PREFIX;

    @Value("${datadog.hostname}")
    private String DATADOG_HOSTNAME;

    @Value("${datadog.port}")
    private int DATADOG_PORT;
    @Value("${env}")
    private String search_spell_checker_env;

    @PostConstruct
    public void init() {
        StatsDClientErrorHandler errorHandler = new StatsDClientErrorHandler() {

            @Override
            public void handle(Exception exception) {
                LOGGER.error("Error in submitting Datadog metrics over wire :{}",
                        exception.getMessage());

            }
        };
        // message will be rejected if internal linkedblockingqueue is full (>queueSize) and there
        // is no trace of such events.
        // For trace we need to override send method but can't override class as
        // NonBlockingStatsDClient is final

        prometheusClient =
                new NonBlockingStatsDClient(DATADOG_PREFIX, DATADOG_HOSTNAME,
                        8130,
                        new String[]{"application:" + DATADOG_PREFIX,
                                "environment:" + search_spell_checker_env},
                        errorHandler);
    }

    /**
     * Used to record execution time of api
     */
    public void recordExecutionTimeOfApi(String apiName, long timeTaken) {

        prometheusClient.recordExecutionTime("api_execution_time", timeTaken, "api_name:" + apiName,
                "environment:" + search_spell_checker_env);
    }

    /**
     * Used to increase counter of api
     */
    public void incrementApiCount(String apiName) {

        prometheusClient.increment("api_count", "api_name:" + apiName,
                "environment:" + search_spell_checker_env);
    }

    /**
     * Used to record HTTP code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode) {

        prometheusClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "environment:" + search_spell_checker_env);
    }

    public void recordResponseCodeCount(String event, int httpCode) {
        prometheusClient.increment("stat.responsecode", "event:" + event, "http_code:" + httpCode);

    }

    /**
     * Used to record ERROR code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode, String responseCode) {

        prometheusClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "response_code:" + responseCode, "environment:" + search_spell_checker_env);
    }

    /**
     * Used to increase counter of function, mostly used via Aspect
     *
     * @param string
     */
    public void incrementFnCount(String fnName, String fnType) {

        prometheusClient.increment("fn_count", "fn_name:" + fnName, "fn_type:" + fnType,
                "environment:" + search_spell_checker_env);
    }

    public void recordPSPDataCounts(String eventName, long delta, String range) {

        prometheusClient.count(eventName, delta, "code:" + eventName,
                "environment:" + search_spell_checker_env, "range:" + range,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordMetricsEvents(String eventName) {

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_spell_checker_env);
    }


    /**
     * Used to record execution time of api
     */
    public void recordExecutionTimeOfEvent(String eventName, long timeTaken) {
        prometheusClient.recordExecutionTime("stats.latency", timeTaken, "event:" + eventName);
    }

    public enum Metric {
        SUCCESS, FAILURE, PENDING, HIT
    }
}
