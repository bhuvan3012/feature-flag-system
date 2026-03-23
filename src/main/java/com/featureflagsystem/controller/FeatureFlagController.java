package com.yourpackage.controller;

import com.yourpackage.model.FeatureFlag;
import com.yourpackage.service.FeatureFlagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/flags")
public class FeatureFlagController {

    private final FeatureFlagService service;

    public FeatureFlagController(FeatureFlagService service) {
        this.service = service;
    }

    @PostMapping
    public FeatureFlag create(@RequestBody FeatureFlag flag) {
        return service.createFlag(flag);
    }

    @GetMapping
    public List<FeatureFlag> getAll() {
        return service.getAllFlags();
    }

    @GetMapping("/{name}")
    public FeatureFlag getByName(@PathVariable String name) {
        return service.getFlagByName(name);
    }

    @PutMapping("/{name}")
    public FeatureFlag update(@PathVariable String name, @RequestBody FeatureFlag flag) {
        return service.updateFlag(name, flag);
    }

    @DeleteMapping("/{name}")
    public String delete(@PathVariable String name) {
        service.deleteFlag(name);
        return "Deleted successfully";
    }
}