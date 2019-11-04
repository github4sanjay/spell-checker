package com.paytmmall.spellchecker.grafana;


import javax.annotation.PostConstruct;

import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;
import com.timgroup.statsd.StatsDClientErrorHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Contains the custom metrics to be pushed to datadog
 *
 * @author himanshujain
 * @version 1.0
 * @created 17-09-2018
 */

@Component
public class MetricsAgent {

    public enum Metric {
        SUCCESS, FAILURE, PENDING, HIT
    }

    private StatsDClient metricClient;

    private StatsDClient prometheusClient;

    @Value("${datadog.prefix}")
    private String DATADOG_PREFIX;

    @Value("${datadog.hostname}")
    private String DATADOG_HOSTNAME;

    @Value("${datadog.port}")
    private int DATADOG_PORT;

    private static final Logger LOGGER = LoggerFactory.getLogger(MetricsAgent.class);


    @Value("${search.env}")
    private String search_es_feeder_env;

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
        metricClient =
                new NonBlockingStatsDClient(DATADOG_PREFIX, DATADOG_HOSTNAME,
                        DATADOG_PORT,
                        new String[]{"application:" + DATADOG_PREFIX,
                                "environment:" + search_es_feeder_env},
                        errorHandler);

        prometheusClient =
                new NonBlockingStatsDClient(DATADOG_PREFIX, DATADOG_HOSTNAME,
                        8130,
                        new String[]{"application:" + DATADOG_PREFIX,
                                "environment:" + search_es_feeder_env},
                        errorHandler);
    }


    /**
     * Used to record execution time of api
     */
    public void recordExecutionTimeOfApi(String apiName, long timeTaken) {
        metricClient.recordExecutionTime("api_execution_time", timeTaken, "api_name:" + apiName,
                "environment:" + search_es_feeder_env);

        prometheusClient.recordExecutionTime("api_execution_time", timeTaken, "api_name:" + apiName,
                "environment:" + search_es_feeder_env);
    }

    /**
     * Used to increase counter of api
     */
    public void incrementApiCount(String apiName) {
        metricClient.increment("api_count", "api_name:" + apiName,
                "environment:" + search_es_feeder_env);

        prometheusClient.increment("api_count", "api_name:" + apiName,
                "environment:" + search_es_feeder_env);
    }

    /**
     * Used to record HTTP code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode) {
        metricClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "environment:" + search_es_feeder_env);

        prometheusClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "environment:" + search_es_feeder_env);
    }

    /**
     * Used to record ERROR code of api
     */
    public void recordResponseCodeCount(String apiName, String httpCode, String responseCode) {
        metricClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "response_code:" + responseCode, "environment:" + search_es_feeder_env);

        prometheusClient.increment("response_code_count", "api_count:" + apiName,
                "http_code:" + httpCode,
                "response_code:" + responseCode, "environment:" + search_es_feeder_env);
    }

    /**
     * Used to increase counter of function, mostly used via Aspect
     *
     * @param string
     */
    public void incrementFnCount(String fnName, String fnType) {
        metricClient.increment("fn_count", "fn_name:" + fnName, "fn_type:" + fnType,
                "environment:" + search_es_feeder_env);

        prometheusClient.increment("fn_count", "fn_name:" + fnName, "fn_type:" + fnType,
                "environment:" + search_es_feeder_env);
    }


    
    public void recordPSPDataCounts(String eventName, long delta, String range) {
        metricClient.count(eventName, delta, "code:" + eventName,
                "environment:" + search_es_feeder_env, "range:" + range,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.count(eventName, delta, "code:" + eventName,
                "environment:" + search_es_feeder_env, "range:" + range,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordScrollTimings(String eventName, long timeTaken) {
        metricClient.recordExecutionTime(eventName, timeTaken, "code:" + eventName,
                "environment:" + search_es_feeder_env);

        prometheusClient.recordExecutionTime(eventName, timeTaken, "code:" + eventName,
                "environment:" + search_es_feeder_env);
    }

    public void recordKafkaPSPConsumeCounts(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordMySQLFeedCounts(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordPSPESEvent(String eventName, long delta) {
        metricClient.count(eventName, delta, "code:" + eventName,
                "environment:" + search_es_feeder_env);

        prometheusClient.count(eventName, delta, "code:" + eventName,
                "environment:" + search_es_feeder_env);
    }

    public void recordPSPKafkaGenericError(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env);

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env);
    }

    public void recordMySQLTimings(String apiName, long timeTaken) {
        metricClient.recordExecutionTime("psp_mysql_timings_" + apiName, timeTaken,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.recordExecutionTime("psp_mysql_timings_" + apiName, timeTaken,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordKafkaProcessingTimings(String apiName, long timeTaken) {
        metricClient.recordExecutionTime("psp_message_processing_timings", timeTaken,
                "api_name:" + apiName, "environment:" + search_es_feeder_env);

        prometheusClient.recordExecutionTime("psp_message_processing_timings", timeTaken,
                "api_name:" + apiName, "environment:" + search_es_feeder_env);
    }

    public void recordWorkerThreadError(String eventName, String range) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env, "range:" + range,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env, "range:" + range,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordMetricsEvents(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env);

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env);
    }

    public void recordRawSearchDocumentsCount(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordProcessedDocPushCount(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordUpdateDocPushCount(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }

    public void recordProcessedDocIngestionTime(long timeTaken) {
        metricClient.recordExecutionTime("es_batch_message_timings", timeTaken,
                "environment:" + search_es_feeder_env);

        prometheusClient.recordExecutionTime("es_batch_message_timings", timeTaken,
                "environment:" + search_es_feeder_env);
    }

    public void recordDeleteDocPushCount(String eventName) {
        metricClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());

        prometheusClient.increment(eventName, "code:" + eventName,
                "environment:" + search_es_feeder_env,
                "threadName:" + Thread.currentThread().getName());
    }
}
