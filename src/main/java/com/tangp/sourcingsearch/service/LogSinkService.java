package com.tangp.sourcingsearch.service;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static ch.qos.logback.core.util.FileSize.GB_COEFFICIENT;
import static ch.qos.logback.core.util.FileSize.MB_COEFFICIENT;

@Component
@Slf4j
public class LogSinkService implements InitializingBean {

    @Value("${log.root.path:/data/applogs/log-service/received}")
    private String logRootPath;

    @Value("${max.history:30}")
    private int maxHistory;

    @Value("${max.file.size.mb:1000}")
    private int fileSizeMB;

    public void flushReceivedJson(JSONObject json) {
        if (null == json || !json.containsKey("url")) {
            return;
        }
        String url = json.getString("url");
        if (url == null) {
            log.error("JSON contains no 'url':{}", json);
            return;
        }
        try {
            URL urlObj = new URL(url);
            getLogger(urlObj.getPath()).info(json.toJSONString());
        } catch (MalformedURLException e) {
            log.error("Invalid URL:" + url, e);
            return;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        File rootPath = new File(logRootPath);
        if (!rootPath.isDirectory()) {
            rootPath.mkdirs();
        }
    }

    private void ensureAppAtIpAppender(String urlPath) {
        if (urlPath == null) {
            throw new RuntimeException("urlPath is null");
        }
        String key = buildKey(urlPath);
        if (LOGGER_CONCURRENT_MAP.containsKey(key)) {
            return;
        }
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender rollingFileAppender = new RollingFileAppender();
        rollingFileAppender.setContext(lc);
        rollingFileAppender.setName("AppenderFor_" + key);
        rollingFileAppender.setFile(String.format("%s/%s.json", logRootPath, key));
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();
        policy.setMaxHistory(maxHistory);
        policy.setContext(lc);
        policy.setTotalSizeCap(new FileSize(GB_COEFFICIENT * 1));
        policy.setMaxFileSize(new FileSize(MB_COEFFICIENT * fileSizeMB));
        policy.setFileNamePattern(logRootPath + "/archived/" + key + ".%d{yyyy-MM-dd}.%i.json.gz");
        policy.setParent(rollingFileAppender);
        rollingFileAppender.setRollingPolicy(policy);
        ch.qos.logback.classic.PatternLayout layout = new ch.qos.logback.classic.PatternLayout();
        layout.setPattern("%msg%n");
        layout.setContext(lc);
        layout.start();
        policy.start();
        rollingFileAppender.setLayout(layout);
        rollingFileAppender.start();


        Logger logger = (Logger) LoggerFactory.getLogger(key);
        logger.addAppender(rollingFileAppender);
        logger.setLevel(Level.DEBUG);
        logger.setAdditive(false);

        LOGGER_CONCURRENT_MAP.putIfAbsent(key, logger);
    }

    private Logger getLogger(String urlPath) {
        ensureAppAtIpAppender(urlPath);
        return LOGGER_CONCURRENT_MAP.get(buildKey(urlPath));
    }

    private static String buildKey(String urlPath) {
        return urlPath.replaceAll("\\/", "_");
    }

    private static final ConcurrentMap<String, Logger> LOGGER_CONCURRENT_MAP
            = new ConcurrentHashMap<>();
}
