package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.MedicalProviderEntity;
import com.gosmart.backoffice.domain.PatientMedicalRecord;
import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.dto.PatientMedicalRecordSaveRequest;
import com.gosmart.backoffice.dto.PatientMedicalRecordView;
import com.gosmart.backoffice.repo.PatientMedicalRecordRepository;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientMedicalRecordService {
    private static final DateTimeFormatter AUDIT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final BigDecimal MAX_4_DIGIT_VALUE = new BigDecimal("9999");

    private final PatientMedicalRecordRepository patientMedicalRecordRepository;
    private final MedicalProviderService medicalProviderService;
    private final PatientRegistrationService patientRegistrationService;

    public PatientMedicalRecordService(
            PatientMedicalRecordRepository patientMedicalRecordRepository,
            MedicalProviderService medicalProviderService,
            PatientRegistrationService patientRegistrationService
    ) {
        this.patientMedicalRecordRepository = patientMedicalRecordRepository;
        this.medicalProviderService = medicalProviderService;
        this.patientRegistrationService = patientRegistrationService;
    }

    public List<PatientMedicalRecordView> findAllActiveRecords() {
        return patientMedicalRecordRepository.findByStatus("A").stream()
                .map(this::toView)
                .toList();
    }

    public PatientMedicalRecordView saveRecord(PatientMedicalRecordSaveRequest request, String currentUserId) {
        if (request.getPatientId() == null) {
            throw new IllegalArgumentException("Please select a registered patient.");
        }
        validateVitalSigns(request);
        PatientRegistration patient = patientRegistrationService.findById(request.getPatientId())
                .filter(item -> "A".equalsIgnoreCase(item.getStatus()))
                .orElseThrow(() -> new IllegalArgumentException("Selected patient is not registered or active."));

        if (patient.getMedicalProviderId() == null
                || !String.valueOf(patient.getMedicalProviderId()).equals(String.valueOf(request.getMedicalProviderId()))) {
            throw new IllegalArgumentException("Selected patient does not belong to the chosen medical provider.");
        }

        PatientMedicalRecord record;
        if (request.getId() != null) {
            record = patientMedicalRecordRepository.findByIdAndStatus(request.getId(), "A")
                    .orElseThrow(() -> new IllegalArgumentException("Invalid medical record selected."));
        } else {
            record = new PatientMedicalRecord();
            record.setId(patientMedicalRecordRepository.findMaxId() + 1);
            record.setCreateDt(LocalDateTime.now());
            record.setCreateBy(currentUserId);
            record.setStatus("A");
        }

        record.setPatientId(request.getPatientId());
        record.setSystolic(request.getSystolic());
        record.setDiastolic(request.getDiastolic());
        record.setPulse(request.getPulse());
        record.setSugarLevel(request.getSugarLevel());
        record.setRemark(request.getRemark());
        record.setModifyBy(currentUserId);

        PatientMedicalRecord saved = patientMedicalRecordRepository.save(record);
        return toView(saved);
    }

    private void validateVitalSigns(PatientMedicalRecordSaveRequest request) {
        validateNonNegativeInteger(request.getSystolic(), "Please enter a valid systolic value.");
        validateNonNegativeInteger(request.getDiastolic(), "Please enter a valid diastolic value.");
        validateNonNegativeInteger(request.getPulse(), "Please enter a valid pulse value.");

        if (request.getSystolic() <= request.getDiastolic()) {
            throw new IllegalArgumentException("Systolic must be greater than diastolic.");
        }

        BigDecimal sugarLevel = request.getSugarLevel();
        if (sugarLevel == null
                || sugarLevel.compareTo(BigDecimal.ZERO) < 0
                || sugarLevel.compareTo(MAX_4_DIGIT_VALUE) > 0
                || sugarLevel.stripTrailingZeros().scale() <= 0
                || sugarLevel.scale() > 2) {
            throw new IllegalArgumentException("Please enter a valid sugar level with a decimal point.");
        }
    }

    private void validateNonNegativeInteger(Integer value, String message) {
        if (value == null || value < 0 || value > 9999) {
            throw new IllegalArgumentException(message);
        }
    }

    public void deleteRecord(Integer id, String currentUserId) {
        patientMedicalRecordRepository.findByIdAndStatus(id, "A").ifPresent(record -> {
            record.setStatus("D");
            record.setModifyBy(currentUserId);
            record.setModifyDt(LocalDateTime.now());
            patientMedicalRecordRepository.save(record);
        });
    }

    private PatientMedicalRecordView toView(PatientMedicalRecord record) {
        String providerCode = "-";
        String patientName = "-";
        Integer medicalProviderId = null;

        if (record.getPatientId() != null) {
            PatientRegistration patient = patientRegistrationService.findById(record.getPatientId()).orElse(null);
            if (patient != null) {
                patientName = patient.getName() == null || patient.getName().isBlank() ? "-" : patient.getName();
                if (patient.getMedicalProviderId() != null) {
                    medicalProviderId = patient.getMedicalProviderId().intValue();
                    providerCode = medicalProviderService.findById(medicalProviderId)
                            .map(this::formatMedicalProvider)
                            .orElse("-");
                }
            }
        }

        return new PatientMedicalRecordView(
                record.getId(),
                medicalProviderId,
                record.getPatientId(),
                providerCode,
                patientName,
                record.getSystolic(),
                record.getDiastolic(),
                record.getPulse(),
                record.getSugarLevel(),
                record.getRemark(),
                formatAuditDateTime(record.getCreateDt()),
                formatAuditUser(record.getCreateBy()),
                formatAuditDateTime(record.getModifyDt()),
                formatAuditUser(record.getModifyBy())
        );
    }

    private String formatMedicalProvider(MedicalProviderEntity provider) {
        if (provider == null) {
            return "-";
        }
        return String.format(
                Locale.ENGLISH,
                "%s (%s)",
                provider.getName() == null ? "-" : provider.getName(),
                provider.getCode() == null ? "-" : provider.getCode()
        );
    }

    private String formatAuditDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(AUDIT_DATE_TIME_FORMAT);
    }

    private String formatAuditUser(String userId) {
        return userId == null || userId.isBlank() ? "-" : userId;
    }
}
