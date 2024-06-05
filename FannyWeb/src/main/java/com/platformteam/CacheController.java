package com.platformteam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.platformteam.service.CacheService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/cache")
public class CacheController {

    private static final Logger logger = LoggerFactory.getLogger(CacheController.class);

    private final CacheService cacheService;

    @Autowired
    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/put")
    public void put(@RequestParam String key, @RequestParam String value) {
        logger.info("PUT request received with key: {} and value: {}", key, value);
        cacheService.put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        logger.info("GET request received for key: {}", key);
        return cacheService.get(key);
    }

    @DeleteMapping("/remove")
    public void remove(@RequestParam String key) {
        logger.info("DELETE request received for key: {}", key);
        cacheService.remove(key);
    }
}
