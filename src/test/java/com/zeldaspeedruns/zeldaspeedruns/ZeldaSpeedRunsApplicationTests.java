package com.zeldaspeedruns.zeldaspeedruns;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.*;

@SpringBootTest
class ZeldaSpeedRunsApplicationTests {
    @Test
    void contextLoads() {
    }

    @Test
    void main_callsRun() {
        try (MockedStatic<SpringApplication> mockedStatic = mockStatic(SpringApplication.class)) {
            mockedStatic.when(() -> SpringApplication.run(eq(ZeldaSpeedRunsApplication.class), any())).thenReturn(null);

            ZeldaSpeedRunsApplication.main(new String[]{});

            mockedStatic.verify(() -> SpringApplication.run(eq(ZeldaSpeedRunsApplication.class), any()), times(1));
        }
    }
}
