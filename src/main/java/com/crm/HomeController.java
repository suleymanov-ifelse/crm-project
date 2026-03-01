package com.crm;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Корневой и приветственный эндпоинты.
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "CRM. API: GET /api/customers";
    }

    @GetMapping("/api/hello")
    public String hello() {
        return "CRM готов!";
    }
}
