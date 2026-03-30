package com.gosmart.backoffice.service;

import com.gosmart.backoffice.repo.FunctionRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class MenuService {
    private static final Logger log = LoggerFactory.getLogger(MenuService.class);

    private final FunctionRepository functionRepository;

    public MenuService(FunctionRepository functionRepository) {
        this.functionRepository = functionRepository;
    }

    public List<MenuGroup> buildMenuGroups(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<FunctionRepository.MenuRow> rows = functionRepository.findMenuRowsByUserId(userId);
        log.debug("Menu load rows={} userId={}", rows.size(), userId);
        Map<Long, MenuGroupBuilder> groups = new LinkedHashMap<>();

        for (FunctionRepository.MenuRow row : rows) {
            MenuGroupBuilder group = groups.computeIfAbsent(
                    row.getGroupId(),
                    id -> new MenuGroupBuilder(id, row.getGroupName())
            );
            group.items.add(new MenuItem(row.getFunctionName(), row.getPath()));
        }

        List<MenuGroup> result = new ArrayList<>(groups.size());
        for (MenuGroupBuilder g : groups.values()) {
            result.add(new MenuGroup(g.groupId, g.groupName, List.copyOf(g.items)));
        }
        log.debug("Menu built groups={} userId={}", result.size(), userId);
        return result;
    }

    public record MenuGroup(Long groupId, String groupName, List<MenuItem> items) {
    }

    public record MenuItem(String name, String path) {
    }

    private static final class MenuGroupBuilder {
        private final Long groupId;
        private final String groupName;
        private final List<MenuItem> items = new ArrayList<>();

        private MenuGroupBuilder(Long groupId, String groupName) {
            this.groupId = groupId;
            this.groupName = groupName;
        }
    }
}
