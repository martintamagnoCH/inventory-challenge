package com.mercadolibre.inventory_challenge;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class InventoryChallengeApplicationTests {

	@Test
	void contextLoads() {
	}

    @Test
    void mainRuns() {
        InventoryChallengeApplication.main(new String[]{});
    }
}