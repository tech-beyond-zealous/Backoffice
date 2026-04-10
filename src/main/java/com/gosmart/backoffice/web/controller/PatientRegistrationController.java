package com.gosmart.backoffice.web.controller;

import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.service.MedicalProviderService;
import com.gosmart.backoffice.service.PatientRegistrationService;
import com.gosmart.backoffice.service.ProtectedPageModelService;
import com.gosmart.backoffice.web.interceptor.AuthInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class PatientRegistrationController {
    private final ProtectedPageModelService protectedPageModelService;
    private final PatientRegistrationService patientRegistrationService;
    private final MedicalProviderService medicalProviderService;

    public PatientRegistrationController(
            ProtectedPageModelService protectedPageModelService,
            PatientRegistrationService patientRegistrationService,
            MedicalProviderService medicalProviderService
    ) {
        this.protectedPageModelService = protectedPageModelService;
        this.patientRegistrationService = patientRegistrationService;
        this.medicalProviderService = medicalProviderService;
    }

    @GetMapping("/patient/registration")
    public String registrationPage(HttpServletRequest request, Model model) {
        protectedPageModelService.apply(model, request, null);
        model.addAttribute("patients", patientRegistrationService.findAll());
        model.addAttribute("medicalProviders", medicalProviderService.findAll());
        return "patient-registration";
    }

    @PostMapping("/patient/save")
    @ResponseBody
    public PatientRegistration savePatient(@RequestBody PatientRegistration patient, HttpServletRequest request) {
        // Get logged-in user_id from auth interceptor attribute
        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }

        // Set create_by and modify_by automatically
        patient.setCreateBy(currentUserId);
        patient.setModifyBy(currentUserId);

        return patientRegistrationService.save(patient);
    }

    @PutMapping("/patient/{id}")
    @ResponseBody
    public PatientRegistration updatePatient(@PathVariable Long id, @RequestBody PatientRegistration patient, HttpServletRequest request) {
        Optional<PatientRegistration> existing = patientRegistrationService.findById(id);
        if (existing.isPresent()) {
            PatientRegistration toUpdate = existing.get();
            toUpdate.setName(patient.getName());
            toUpdate.setAge(patient.getAge());
            toUpdate.setGender(patient.getGender());
            toUpdate.setRace(patient.getRace());
            toUpdate.setIcPassportNo(patient.getIcPassportNo());
            toUpdate.setMobileNo(patient.getMobileNo());
            toUpdate.setEmergencyContactName(patient.getEmergencyContactName());
            toUpdate.setEmergencyContactNo(patient.getEmergencyContactNo());
            toUpdate.setRelationship(patient.getRelationship());
            toUpdate.setAddress(patient.getAddress());
            toUpdate.setArea(patient.getArea());
            toUpdate.setPostcode(patient.getPostcode());
            toUpdate.setCity(patient.getCity());
            toUpdate.setHasChronicDisease(patient.getHasChronicDisease());
            toUpdate.setChronicDisease(patient.getChronicDisease());
            toUpdate.setMedicalProviderId(patient.getMedicalProviderId());

            // Set modify_by automatically
            String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
            if (currentUserId == null) {
                throw new IllegalStateException("Logged-in user id is missing from request attributes");
            }
            toUpdate.setModifyBy(currentUserId);

            return patientRegistrationService.save(toUpdate);
        }
        return null;
    }

    @DeleteMapping("/patient/{id}")
    @ResponseBody
    public void deletePatient(@PathVariable Long id, HttpServletRequest request) {

        String currentUserId = (String) request.getAttribute(AuthInterceptor.REQ_ATTR_USER_ID);
        if (currentUserId == null) {
            throw new IllegalStateException("Logged-in user id is missing from request attributes");
        }

        patientRegistrationService.deleteById(id, currentUserId);
    }

    @GetMapping("/patient/{id}")
    @ResponseBody
    public Optional<PatientRegistration> getPatient(@PathVariable Long id) {
        return patientRegistrationService.findById(id);
    }

    @GetMapping("/patient/all")
    @ResponseBody
    public java.util.List<PatientRegistration> getAllPatients() {
        return patientRegistrationService.findAll();
    }
}
