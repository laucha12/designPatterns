package org.example.ai;

import java.util.HashMap;
import java.util.Map;

public class TeamDescriptionCacheProxy implements TeamDescriptionService{

    private final TeamDescriptionService wrappedService;
    private final Map<String, String> cache;

    public TeamDescriptionCacheProxy(TeamDescriptionService wrappedService) {
        this.wrappedService = wrappedService;
        this.cache = new HashMap<>();
    }

    @Override
    public String getTeamDescription(String teamName) {
        return cache.computeIfAbsent(teamName, wrappedService::getTeamDescription);
    }
}
