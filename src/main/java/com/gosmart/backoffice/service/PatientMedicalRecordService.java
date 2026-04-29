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
import java.util.Objects;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class PatientMedicalRecordService {
    private static final DateTimeFormatter AUDIT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final BigDecimal MAX_4_DIGIT_VALUE = new BigDecimal("9999");
    private static final BigDecimal MAX_TEMPERATURE_VALUE = new BigDecimal("999.9");

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
        boolean newRecord = false;
        if (request.getId() != null) {
            record = patientMedicalRecordRepository.findByIdAndStatus(request.getId(), "A")
                    .orElseThrow(() -> new IllegalArgumentException("Invalid medical record selected."));
        } else {
            record = new PatientMedicalRecord();
            newRecord = true;
            record.setId(patientMedicalRecordRepository.findMaxId() + 1);
            record.setCreateDt(LocalDateTime.now());
            record.setCreateBy(currentUserId);
            record.setStatus("A");
        }

        LocalDateTime measurementTimestamp = LocalDateTime.now();
        record.setPatientId(request.getPatientId());

        boolean bloodPressureChanged = !Objects.equals(record.getSystolic(), request.getSystolic())
                || !Objects.equals(record.getDiastolic(), request.getDiastolic());
        if ((newRecord && request.getSystolic() != null) || bloodPressureChanged) {
            record.setBpRecordedAt(request.getSystolic() == null ? null : measurementTimestamp);
        }
        record.setSystolic(request.getSystolic());
        record.setDiastolic(request.getDiastolic());

        boolean pulseChanged = !Objects.equals(record.getPulse(), request.getPulse());
        if ((newRecord && request.getPulse() != null) || pulseChanged) {
            record.setPulseRecordedAt(request.getPulse() == null ? null : measurementTimestamp);
        }
        record.setPulse(request.getPulse());

        boolean sugarChanged = !equalBigDecimal(record.getSugarLevel(), request.getSugarLevel());
        if ((newRecord && request.getSugarLevel() != null) || sugarChanged) {
            record.setSugarTestDate(request.getSugarLevel() == null ? null : measurementTimestamp);
        }
        record.setSugarLevel(request.getSugarLevel());

        boolean spo2Changed = !Objects.equals(record.getSpo2(), request.getSpo2());
        if ((newRecord && request.getSpo2() != null) || spo2Changed) {
            record.setSpo2RecordedAt(request.getSpo2() == null ? null : measurementTimestamp);
        }
        record.setSpo2(request.getSpo2());

        boolean temperatureChanged = !equalBigDecimal(record.getTemperature(), request.getTemperature());
        if ((newRecord && request.getTemperature() != null) || temperatureChanged) {
            record.setTemperatureRecordedAt(request.getTemperature() == null ? null : measurementTimestamp);
        }
        record.setTemperature(request.getTemperature());

        boolean painScoreChanged = !Objects.equals(record.getPainScore(), request.getPainScore());
        if ((newRecord && request.getPainScore() != null) || painScoreChanged) {
            record.setPainScoreRecordedAt(request.getPainScore() == null ? null : measurementTimestamp);
        }
        record.setPainScore(request.getPainScore());
        record.setRemark(request.getRemark());
        record.setModifyBy(currentUserId);

        PatientMedicalRecord saved = patientMedicalRecordRepository.save(record);
        return toView(saved);
    }

    private void validateVitalSigns(PatientMedicalRecordSaveRequest request) {
        boolean hasSystolic = request.getSystolic() != null;
        boolean hasDiastolic = request.getDiastolic() != null;
        if (hasSystolic != hasDiastolic) {
            throw new IllegalArgumentException("Please enter both systolic and diastolic values for blood pressure.");
        }

        validateOptionalNonNegativeInteger(request.getSystolic(), "Please enter a valid systolic value.");
        validateOptionalNonNegativeInteger(request.getDiastolic(), "Please enter a valid diastolic value.");
        validateNonNegativeInteger(request.getPulse(), "Please enter a valid pulse value.");
        validateSpo2(request.getSpo2());
        validateTemperature(request.getTemperature());
        validatePainScore(request.getPainScore());

        if (hasSystolic && request.getSystolic() <= request.getDiastolic()) {
            throw new IllegalArgumentException("Systolic must be greater than diastolic.");
        }

        BigDecimal sugarLevel = request.getSugarLevel();
        if (sugarLevel != null
                && (sugarLevel.compareTo(BigDecimal.ZERO) < 0
                || sugarLevel.compareTo(MAX_4_DIGIT_VALUE) > 0
                || sugarLevel.stripTrailingZeros().scale() <= 0
                || sugarLevel.scale() > 2)) {
            throw new IllegalArgumentException("Please enter a valid blood sugar value with a decimal point.");
        }
    }

    private void validateOptionalNonNegativeInteger(Integer value, String message) {
        if (value == null || value < 0 || value > 9999) {
            if (value == null) {
                return;
            }
            throw new IllegalArgumentException(message);
        }
    }

    private void validateNonNegativeInteger(Integer value, String message) {
        validateOptionalNonNegativeInteger(value, message);
    }

    private void validateSpo2(Integer spo2) {
        if (spo2 != null && (spo2 < 0 || spo2 > 100)) {
            throw new IllegalArgumentException("Please enter a valid SPO2 value from 0 to 100.");
        }
    }

    private void validateTemperature(BigDecimal temperature) {
        if (temperature != null
                && (temperature.compareTo(BigDecimal.ZERO) < 0
                || temperature.compareTo(MAX_TEMPERATURE_VALUE) > 0
                || temperature.scale() > 1)) {
            throw new IllegalArgumentException("Please enter a valid temperature using up to 1 decimal place.");
        }
    }

    private void validatePainScore(Integer painScore) {
        if (painScore != null && (painScore < 0 || painScore > 10)) {
            throw new IllegalArgumentException("Please enter a valid pain score from 0 to 10.");
        }
    }

    private boolean equalBigDecimal(BigDecimal left, BigDecimal right) {
        if (left == null || right == null) {
            return left == right;
        }
        return left.compareTo(right) == 0;
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
        String patientIcPassportNo = "-";
        Integer medicalProviderId = null;

        if (record.getPatientId() != null) {
            PatientRegistration patient = patientRegistrationService.findById(record.getPatientId()).orElse(null);
            if (patient != null) {
                patientName = patient.getName() == null || patient.getName().isBlank() ? "-" : patient.getName();
                patientIcPassportNo = patient.getIcPassportNo() == null || patient.getIcPassportNo().isBlank()
                        ? "-"
                        : patient.getIcPassportNo();
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
                patientIcPassportNo,
                record.getSystolic(),
                record.getDiastolic(),
                formatAuditDateTime(record.getBpRecordedAt()),
                record.getPulse(),
                formatAuditDateTime(record.getPulseRecordedAt()),
                record.getSugarLevel(),
                formatAuditDateTime(record.getSugarTestDate()),
                record.getSpo2(),
                formatAuditDateTime(record.getSpo2RecordedAt()),
                record.getTemperature(),
                formatAuditDateTime(record.getTemperatureRecordedAt()),
                record.getPainScore(),
                formatAuditDateTime(record.getPainScoreRecordedAt()),
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
