package io.paymeter.assessment.infrastructure.entrypoints.status;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

	@GetMapping("/")
	public String index() {
		return "OK";
	}

}
