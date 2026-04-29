package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.domain.MedicalProviderEntity;
import com.gosmart.backoffice.dto.UserFunctionPermission;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class MedicalProviderController {

    private final ProtectedPageModelService protectedPageModelService;
    private final MedicalProviderService medicalProviderService;

    public MedicalProviderController(
            ProtectedPageModelService protectedPageModelService,
            MedicalProviderService medicalProviderService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.medicalProviderService = medicalProviderService;
    }

    @GetMapping("/medical-provider/registration")
    public String registrationPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        UserFunctionPermission permission = (UserFunctionPermission) request.getAttribute(AuthInterceptor.REQ_ATTR_PERMISSION);
        model.addAttribute("medicalProviders", medicalProviderService.findAll());
        model.addAttribute("permission", permission);
        model.addAttribute("requestPath", request.getRequestURI());
        model.addAttribute("functionCode", request.getAttribute(AuthInterceptor.REQ_ATTR_FUNCTION_CODE));
        return "medical-provider-registration";
    }

    @PostMapping("/medical-provider/save")
    @ResponseBody
    public MedicalProviderEntity saveMedicalProvider(@RequestBody MedicalProviderEntity medicalProvider, HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        medicalProvider.setCreateBy(currentUserId);
        return medicalProviderService.save(medicalProvider);
    }

    @PutMapping("/medical-provider/{id}")
    @ResponseBody
    public MedicalProviderEntity updateMedicalProvider(@PathVariable Integer id, @RequestBody MedicalProviderEntity medicalProvider, HttpServletRequest request) {
        Optional<MedicalProviderEntity> existing = medicalProviderService.findById(id);
        if (existing.isPresent()) {
            MedicalProviderEntity toUpdate = existing.get();
            // Update fields from the request body
            toUpdate.setCode(medicalProvider.getCode());
            toUpdate.setName(medicalProvider.getName());
            toUpdate.setStatus(medicalProvider.getStatus());

            return medicalProviderService.save(toUpdate);
        }
        return null;
    }

    @DeleteMapping("/medical-provider/{id}")
    @ResponseBody
    public void deleteMedicalProvider(@PathVariable Integer id, HttpServletRequest request) {
        medicalProviderService.softDelete(id);
    }

    @GetMapping("/medical-provider/{id}")
    @ResponseBody
    public Optional<MedicalProviderEntity> getMedicalProvider(@PathVariable Integer id) {
        return medicalProviderService.findById(id);
    }

    @GetMapping("/medical-provider/all")
    @ResponseBody
    public List<MedicalProviderEntity> getAllMedicalProviders(HttpServletRequest request) {
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }
        return medicalProviderService.findAccessibleByUserId(currentUserId);
    }
}
