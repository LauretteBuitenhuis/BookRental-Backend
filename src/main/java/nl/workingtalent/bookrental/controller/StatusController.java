package nl.workingtalent.bookrental.controller;

import java.util.Date;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatusController {

	// @RequestMapping(value="status")
	@GetMapping("status")
	public String status() {
		return "OK: " + new Date();
	}
	
}
