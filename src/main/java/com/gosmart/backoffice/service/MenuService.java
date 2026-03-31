package com.gosmart.backoffice.service;

import com.gosmart.backoffice.repo.UserFunctionRepository;
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

    private final UserFunctionRepository userFunctionRepository;

    public MenuService(UserFunctionRepository userFunctionRepository) {
        this.userFunctionRepository = userFunctionRepository;
    }

    public List<ApplicationSystem> buildApplicationSystems(String userId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<UserFunctionRepository.ApplicationSystemRow> rows = userFunctionRepository.findApplicationSystemsByUserId(userId);
        log.debug("Application systems load rows={} userId={}", rows.size(), userId);

        List<ApplicationSystem> result = new ArrayList<>(rows.size());
        for (UserFunctionRepository.ApplicationSystemRow row : rows) {
            result.add(new ApplicationSystem(row.getApplicationSystemId(), row.getSystemCode(), row.getSystemName()));
        }
        log.debug("Application systems built systems={} userId={}", result.size(), userId);
        return result;
    }

    public List<MenuGroup> buildMenuGroups(String userId) {
        return buildMenuGroups(userId, null);
    }

    public List<MenuGroup> buildMenuGroups(String userId, Long applicationSystemId) {
        if (userId == null || userId.isBlank()) {
            return List.of();
        }

        List<UserFunctionRepository.MenuRow> rows = userFunctionRepository.findMenuRowsByUserId(userId, applicationSystemId);
        log.debug("Menu load rows={} userId={}", rows.size(), userId);
        Map<Long, MenuGroupBuilder> groups = new LinkedHashMap<>();

        for (UserFunctionRepository.MenuRow row : rows) {
            MenuGroupBuilder group = groups.computeIfAbsent(
                    row.getGroupId(),
                    id -> new MenuGroupBuilder(id, row.getGroupName())
            );
            group.items.add(new MenuItem(row.getFunctionId(), row.getFunctionCode(), row.getFunctionName(), row.getPath()));
        }

        List<MenuGroup> result = new ArrayList<>(groups.size());
        for (MenuGroupBuilder g : groups.values()) {
            result.add(new MenuGroup(g.groupId, g.groupName, List.copyOf(g.items)));
        }
        log.debug("Menu built groups={} userId={}", result.size(), userId);
        return result;
    }

    public record ApplicationSystem(Long applicationSystemId, String systemCode, String systemName) {
    }

    public record MenuGroup(Long groupId, String groupName, List<MenuItem> items) {
    }

    public record MenuItem(Long functionId, String functionCode, String name, String path) {
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
