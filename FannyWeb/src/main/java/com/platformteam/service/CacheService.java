package com.platformteam.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cache")
public class CacheService {

    private final CacheService cacheService;

    @Autowired
    public CacheService(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @PostMapping("/put")
    public void put(@RequestParam String key, @RequestParam String value) {
        cacheService.put(key, value);
    }

    @GetMapping("/get")
    public String get(@RequestParam String key) {
        return cacheService.get(key);
    }

    @DeleteMapping("/remove")
    public void remove(@RequestParam String key) {
        cacheService.remove(key);
    }
}
