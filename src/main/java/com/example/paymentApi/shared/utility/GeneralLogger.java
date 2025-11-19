package com.example.paymentApi.shared.utility;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class GeneralLogger{
    private Logger logger = LoggerFactory.getLogger(GeneralLogger.class);

    public GeneralLogger getLogger(Class clazz) {
        logger = LoggerFactory.getLogger(clazz);

        return this;
    }

    public void log(String message) {
        this.logger.info(message);
    }


}
