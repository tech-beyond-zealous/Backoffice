package com.gosmart.backoffice.service;

import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.repo.UserFunctionRepository;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class PermissionService {
    private final UserFunctionRepository userFunctionRepository;

    public PermissionService(UserFunctionRepository userFunctionRepository) {
        this.userFunctionRepository = userFunctionRepository;
    }

    public UserFunctionPermission resolve(HttpServletRequest request, String userId, String functionPath) {
        UserFunctionPermission permission =
                (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        if (permission != null) {
            return permission;
        }

        return userFunctionRepository.findFunctionPermissionRow(userId, functionPath)
                .filter(row -> isYes(row.getCanView()))
                .map(row -> new UserFunctionPermission(
                        isYes(row.getCanCreate()),
                        isYes(row.getCanEdit()),
                        isYes(row.getCanDelete()),
                        true
                ))
                .orElse(null);
    }

    public void requireCreate(HttpServletRequest request, String userId, String functionPath, String message) {
        UserFunctionPermission permission = resolve(request, userId, functionPath);
        if (permission == null || !permission.isCreate()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    public void requireEdit(HttpServletRequest request, String userId, String functionPath, String message) {
        UserFunctionPermission permission = resolve(request, userId, functionPath);
        if (permission == null || !permission.isEdit()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    public void requireDelete(HttpServletRequest request, String userId, String functionPath, String message) {
        UserFunctionPermission permission = resolve(request, userId, functionPath);
        if (permission == null || !permission.isDelete()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    public void requireView(HttpServletRequest request, String userId, String functionPath, String message) {
        UserFunctionPermission permission = resolve(request, userId, functionPath);
        if (permission == null || !permission.isView()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, message);
        }
    }

    private static boolean isYes(String value) {
        return value != null && "Y".equalsIgnoreCase(value.trim());
    }
}
