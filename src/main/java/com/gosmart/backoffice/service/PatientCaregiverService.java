package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.CaregiverEntity;
import com.gosmart.backoffice.domain.MedicalProviderEntity;
import com.gosmart.backoffice.domain.PatientCaregiverEntity;
import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.dto.PatientCaregiverSaveRequest;
import com.gosmart.backoffice.dto.PatientCaregiverView;
import com.gosmart.backoffice.repo.CaregiverRepository;
import com.gosmart.backoffice.repo.MedicalProviderRepository;
import com.gosmart.backoffice.repo.PatientCaregiverRepository;
import com.gosmart.backoffice.repo.PatientRegistrationRepository;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientCaregiverService {
    private static final DateTimeFormatter AUDIT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final PatientCaregiverRepository patientCaregiverRepository;
    private final MedicalProviderRepository medicalProviderRepository;
    private final PatientRegistrationRepository patientRegistrationRepository;
    private final CaregiverRepository caregiverRepository;

    public PatientCaregiverService(
            PatientCaregiverRepository patientCaregiverRepository,
            MedicalProviderRepository medicalProviderRepository,
            PatientRegistrationRepository patientRegistrationRepository,
            CaregiverRepository caregiverRepository
    ) {
        this.patientCaregiverRepository = patientCaregiverRepository;
        this.medicalProviderRepository = medicalProviderRepository;
        this.patientRegistrationRepository = patientRegistrationRepository;
        this.caregiverRepository = caregiverRepository;
    }

    public List<PatientCaregiverView> findAllCurrentAssignments() {
        return buildViews(patientCaregiverRepository.findAll());
    }

    public Optional<PatientCaregiverView> saveAssignments(PatientCaregiverSaveRequest request, String currentUserId) {
        Integer medicalProviderId = requirePositive(request.getMedicalProviderId(), "Please select a medical provider.");
        Integer patientId = requirePositive(request.getPatientId(), "Please select a patient.");
        List<Integer> caregiverIds = normalizeCaregiverIds(request.getCaregiverIds());
        String status = normalizeStatus(request.getStatus());

        validateDuplicatePatientAssignment(medicalProviderId, patientId);

        MedicalProviderEntity provider = medicalProviderRepository.findById(medicalProviderId)
                .filter(item -> "A".equalsIgnoreCase(item.getStatus()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid medical provider selected."));

        PatientRegistration patient = patientRegistrationRepository.findById(Long.valueOf(patientId))
                .filter(item -> "A".equalsIgnoreCase(item.getStatus()))
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient selected."));

        Integer patientProviderId = patient.getMedicalProviderId() == null ? null : Math.toIntExact(patient.getMedicalProviderId());
        if (!medicalProviderId.equals(patientProviderId)) {
            throw new IllegalArgumentException("Selected patient does not belong to the selected medical provider.");
        }

        List<CaregiverEntity> activeProviderCaregivers = caregiverRepository.findAll().stream()
                .filter(item -> item.getMedicalProviderId() != null && medicalProviderId.equals(item.getMedicalProviderId()))
                .filter(item -> "A".equalsIgnoreCase(item.getStatus()))
                .sorted(Comparator.comparing(item -> safeString(item.getName()), String.CASE_INSENSITIVE_ORDER))
                .toList();

        if (activeProviderCaregivers.isEmpty()) {
            throw new IllegalArgumentException("No active caregivers found for the selected medical provider.");
        }

        Set<Integer> allowedCaregiverIds = activeProviderCaregivers.stream()
                .map(CaregiverEntity::getId)
                .filter(item -> item != null)
                .map(Math::toIntExact)
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (!allowedCaregiverIds.containsAll(caregiverIds)) {
            throw new IllegalArgumentException("Selected caregivers must belong to the selected medical provider.");
        }

        List<PatientCaregiverEntity> existingRecords =
                patientCaregiverRepository.findByMedicalProviderIdAndPatientId(medicalProviderId, patientId);
        Map<Integer, PatientCaregiverEntity> existingByCaregiverId = existingRecords.stream()
                .collect(Collectors.toMap(
                        PatientCaregiverEntity::getCaregiverId,
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        for (Integer caregiverId : allowedCaregiverIds) {
            boolean selected = caregiverIds.contains(caregiverId);
            PatientCaregiverEntity record = existingByCaregiverId.get(caregiverId);

            if (selected) {
                if (record == null) {
                    record = new PatientCaregiverEntity();
                    record.setMedicalProviderId(provider.getId());
                    record.setPatientId(patientId);
                    record.setCaregiverId(caregiverId);
                    record.setCreateBy(currentUserId);
                }
                record.setStatus(status);
                record.setModifyBy(currentUserId);
                patientCaregiverRepository.save(record);
            } else if (record != null && !"D".equalsIgnoreCase(record.getStatus())) {
                record.setStatus("D");
                record.setModifyBy(currentUserId);
                patientCaregiverRepository.save(record);
            }
        }

        if ("D".equals(status)) {
            return Optional.empty();
        }
        return findCurrentAssignment(medicalProviderId, patientId);
    }

    private void validateDuplicatePatientAssignment(Integer medicalProviderId, Integer patientId) {
        List<PatientCaregiverEntity> existingRecords = patientCaregiverRepository.findByPatientId(patientId).stream()
                .filter(item -> !"D".equalsIgnoreCase(item.getStatus()))
                .toList();
        if (existingRecords.isEmpty()) {
            return;
        }

        boolean sameAssignmentGroup = existingRecords.stream()
                .allMatch(item -> medicalProviderId.equals(item.getMedicalProviderId()));
        if (!sameAssignmentGroup) {
            throw new IllegalArgumentException("Patient record already exists in the patient caregiver list.");
        }
    }

    public void deleteAssignmentGroup(Integer id, String currentUserId) {
        PatientCaregiverEntity seed = patientCaregiverRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid patient caregiver assignment selected."));
        List<PatientCaregiverEntity> records = patientCaregiverRepository.findByMedicalProviderIdAndPatientId(
                seed.getMedicalProviderId(),
                seed.getPatientId()
        );
        for (PatientCaregiverEntity record : records) {
            if (!"D".equalsIgnoreCase(record.getStatus())) {
                record.setStatus("D");
                record.setModifyBy(currentUserId);
                patientCaregiverRepository.save(record);
            }
        }
    }

    public Optional<PatientCaregiverView> findCurrentAssignment(Integer medicalProviderId, Integer patientId) {
        return findAllCurrentAssignments().stream()
                .filter(item -> medicalProviderId.equals(item.getMedicalProviderId()) && patientId.equals(item.getPatientId()))
                .findFirst();
    }

    private List<PatientCaregiverView> buildViews(Collection<PatientCaregiverEntity> records) {
        Map<Integer, PatientRegistration> patientsById = patientRegistrationRepository.findAll().stream()
                .collect(Collectors.toMap(
                        item -> Math.toIntExact(item.getId()),
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
        Map<Integer, CaregiverEntity> caregiversById = caregiverRepository.findAll().stream()
                .filter(item -> item.getId() != null)
                .collect(Collectors.toMap(
                        item -> Math.toIntExact(item.getId()),
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
        Map<Integer, MedicalProviderEntity> providersById = medicalProviderRepository.findAll().stream()
                .collect(Collectors.toMap(
                        MedicalProviderEntity::getId,
                        item -> item,
                        (left, right) -> left,
                        LinkedHashMap::new
                ));

        Map<GroupKey, List<PatientCaregiverEntity>> grouped = new LinkedHashMap<>();
        for (PatientCaregiverEntity record : records) {
            GroupKey key = new GroupKey(record.getMedicalProviderId(), record.getPatientId());
            grouped.computeIfAbsent(key, ignored -> new ArrayList<>()).add(record);
        }

        return grouped.entrySet().stream()
                .map(entry -> toView(entry.getKey(), entry.getValue(), patientsById, caregiversById, providersById))
                .filter(item -> item != null)
                .sorted(Comparator.comparing(PatientCaregiverView::getModifiedDateTime, Comparator.nullsLast(String::compareTo)).reversed())
                .toList();
    }

    private PatientCaregiverView toView(
            GroupKey key,
            List<PatientCaregiverEntity> records,
            Map<Integer, PatientRegistration> patientsById,
            Map<Integer, CaregiverEntity> caregiversById,
            Map<Integer, MedicalProviderEntity> providersById
    ) {
        records.sort(Comparator.comparing(PatientCaregiverEntity::getId));

        List<PatientCaregiverEntity> activeCaregiverRecords = records.stream()
                .filter(item -> {
                    if ("D".equalsIgnoreCase(item.getStatus())) {
                        return false;
                    }
                    CaregiverEntity caregiver = caregiversById.get(item.getCaregiverId());
                    return caregiver != null && "A".equalsIgnoreCase(caregiver.getStatus());
                })
                .sorted(Comparator.comparing(PatientCaregiverEntity::getId))
                .toList();

        if (activeCaregiverRecords.isEmpty()) {
            return null;
        }

        PatientCaregiverEntity first = activeCaregiverRecords.get(0);
        PatientRegistration patient = patientsById.get(key.patientId());
        if (patient == null || !"A".equalsIgnoreCase(patient.getStatus())) {
            return null;
        }
        MedicalProviderEntity provider = providersById.get(key.medicalProviderId());

        List<Integer> caregiverIds = activeCaregiverRecords.stream()
                .map(PatientCaregiverEntity::getCaregiverId)
                .sorted()
                .toList();

        String caregiverNames = activeCaregiverRecords.stream()
                .map(item -> caregiversById.get(item.getCaregiverId()))
                .filter(item -> item != null)
                .map(CaregiverEntity::getName)
                .filter(item -> item != null && !item.isBlank())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(Collectors.joining(", "));

        LocalDateTime latestModifyDt = activeCaregiverRecords.stream()
                .map(PatientCaregiverEntity::getModifyDt)
                .filter(item -> item != null)
                .max(LocalDateTime::compareTo)
                .orElse(first.getCreateDt());

        String latestModifyBy = activeCaregiverRecords.stream()
                .sorted(Comparator.comparing(PatientCaregiverEntity::getModifyDt, Comparator.nullsFirst(LocalDateTime::compareTo)).reversed())
                .map(PatientCaregiverEntity::getModifyBy)
                .filter(item -> item != null && !item.isBlank())
                .findFirst()
                .orElse(first.getCreateBy());

        return new PatientCaregiverView(
                first.getId(),
                key.medicalProviderId(),
                key.patientId(),
                formatMedicalProvider(provider),
                patient.getName() != null ? patient.getName() : "-",
                patient.getIcPassportNo() != null ? patient.getIcPassportNo() : "-",
                caregiverIds,
                caregiverNames.isBlank() ? "-" : caregiverNames,
                safeStatus(first.getStatus()),
                formatAuditDateTime(first.getCreateDt()),
                formatAuditUser(first.getCreateBy()),
                formatAuditDateTime(latestModifyDt),
                formatAuditUser(latestModifyBy)
        );
    }

    private Integer requirePositive(Integer value, String message) {
        if (value == null || value <= 0) {
            throw new IllegalArgumentException(message);
        }
        return value;
    }

    private List<Integer> normalizeCaregiverIds(Collection<Integer> caregiverIds) {
        if (caregiverIds == null || caregiverIds.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one caregiver.");
        }
        List<Integer> normalized = caregiverIds.stream()
                .filter(item -> item != null && item > 0)
                .distinct()
                .toList();
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("Please select at least one caregiver.");
        }
        return normalized;
    }

    private String normalizeStatus(String status) {
        String normalized = status == null ? "" : status.trim().toUpperCase(Locale.ROOT);
        if (!List.of("A", "X", "D").contains(normalized)) {
            throw new IllegalArgumentException("Please select a valid status.");
        }
        return normalized;
    }

    private String formatMedicalProvider(MedicalProviderEntity provider) {
        if (provider == null) {
            return "-";
        }
        String code = safeString(provider.getCode());
        String name = safeString(provider.getName());
        if (code.isBlank()) {
            return name.isBlank() ? "-" : name;
        }
        if (name.isBlank()) {
            return code;
        }
        return code + " - " + name;
    }

    private String formatAuditDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(AUDIT_DATE_TIME_FORMAT);
    }

    private String formatAuditUser(String userId) {
        return userId == null || userId.isBlank() ? "-" : userId;
    }

    private String safeStatus(String status) {
        return status == null || status.isBlank() ? "A" : status;
    }

    private String safeString(String value) {
        return value == null ? "" : value;
    }

    private record GroupKey(Integer medicalProviderId, Integer patientId) {
    }
}
