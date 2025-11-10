package io.paymeter.assessment.applications.Config;

import io.paymeter.assessment.applications.config.Config;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Clock;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigTest {
    private AnnotationConfigApplicationContext context;

    @BeforeEach
    void setUp() {
        context = new AnnotationConfigApplicationContext(Config.class);
    }
    @Test
    void getClockSystemUTC() {
        Clock clockBean = context.getBean(Clock.class);
        assertNotNull(clockBean);
        ZoneId expectedZone = ZoneId.of("Z");
        assertEquals(expectedZone, clockBean.getZone());

        String[] beanNames = context.getBeanNamesForType(Clock.class);
        assertEquals(1, beanNames.length);
    }
}