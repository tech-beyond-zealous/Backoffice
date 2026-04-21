package com.gosmart.backoffice.service;

import com.gosmart.backoffice.domain.MedicalPackage;
import com.gosmart.backoffice.domain.MedicalProviderEntity;
import com.gosmart.backoffice.domain.PackageSubscription;
import com.gosmart.backoffice.domain.PatientRegistration;
import com.gosmart.backoffice.dto.SubscriptionSaveRequest;
import com.gosmart.backoffice.dto.SubscriptionView;
import com.gosmart.backoffice.repo.PackageSubscriptionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class PackageSubscriptionService {
    private static final DateTimeFormatter AUDIT_DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    private final PackageSubscriptionRepository packageSubscriptionRepository;
    private final MedicalPackageService medicalPackageService;
    private final PatientRegistrationService patientRegistrationService;
    private final MedicalProviderService medicalProviderService;

    public PackageSubscriptionService(
            PackageSubscriptionRepository packageSubscriptionRepository,
            MedicalPackageService medicalPackageService,
            PatientRegistrationService patientRegistrationService,
            MedicalProviderService medicalProviderService
    ) {
        this.packageSubscriptionRepository = packageSubscriptionRepository;
        this.medicalPackageService = medicalPackageService;
        this.patientRegistrationService = patientRegistrationService;
        this.medicalProviderService = medicalProviderService;
    }

    public List<SubscriptionView> findAllActiveSubscriptions() {
        return packageSubscriptionRepository.findByStatus("A").stream()
                .map(this::toView)
                .toList();
    }

    public SubscriptionView saveSubscription(SubscriptionSaveRequest request, String currentUserId) {
        MedicalPackage medicalPackage = medicalPackageService.findById(request.getMedicalPackageId())
                .orElseThrow(() -> new IllegalArgumentException("Invalid package selected"));
        validateDuplicatePatientSubscription(request);

        PackageSubscription subscription;
        if (request.getId() != null) {
            subscription = packageSubscriptionRepository.findById(request.getId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid subscription selected"));
        } else {
            subscription = new PackageSubscription();
            subscription.setCreateBy(currentUserId);
            subscription.setStatus("A");
        }

        subscription.setMedicalProviderId(request.getMedicalProviderId());
        subscription.setMedicalPackageId(request.getMedicalPackageId());
        subscription.setPatientId(Math.toIntExact(request.getPatientId()));
        subscription.setMode(mapMode(request.getMode()));
        subscription.setAmount(getAmountForMode(medicalPackage, request.getMode()));
        subscription.setRemark(request.getRemark());
        subscription.setModifyBy(currentUserId);

        if (request.getExpirationDate() != null && !request.getExpirationDate().isBlank()) {
            subscription.setExpirationDt(LocalDate.parse(request.getExpirationDate()).atStartOfDay());
        } else {
            subscription.setExpirationDt(null);
        }

        PackageSubscription saved = packageSubscriptionRepository.save(subscription);
        return toView(saved);
    }

    private void validateDuplicatePatientSubscription(SubscriptionSaveRequest request) {
        Integer patientId = Math.toIntExact(request.getPatientId());
        boolean exists = request.getId() != null
                ? packageSubscriptionRepository.existsByPatientIdAndStatusAndIdNot(patientId, "A", request.getId())
                : packageSubscriptionRepository.existsByPatientIdAndStatus(patientId, "A");

        if (exists) {
            throw new IllegalArgumentException("Patient record already exists in the subscription list.");
        }
    }

    public void deleteSubscription(Integer id) {
        packageSubscriptionRepository.findById(id).ifPresent(subscription -> {
            subscription.setStatus("D");
            packageSubscriptionRepository.save(subscription);
        });
    }

    private SubscriptionView toView(PackageSubscription subscription) {
        String providerCode = "-";
        String packageName = "-";
        String patientName = "-";
        Long patientId = null;

        MedicalPackage medicalPackage = medicalPackageService.findById(subscription.getMedicalPackageId()).orElse(null);
        if (medicalPackage != null) {
            packageName = formatPackageName(medicalPackage.getName());
        }
        if (subscription.getMedicalProviderId() != null) {
            providerCode = medicalProviderService.findById(subscription.getMedicalProviderId())
                    .map(this::formatMedicalProvider)
                    .orElse("-");
        }

        PatientRegistration patient = null;
        if (subscription.getPatientId() != null) {
            patientId = subscription.getPatientId().longValue();
            patient = patientRegistrationService.findById(patientId).orElse(null);
        }
        if (patient != null) {
            patientName = patient.getName() != null ? patient.getName() : "-";
        }

        String modeDisplay = "-";
        if ("M".equals(subscription.getMode())) {
            modeDisplay = "Monthly";
        } else if ("Y".equals(subscription.getMode())) {
            modeDisplay = "Yearly";
        }

        String expirationDate = subscription.getExpirationDt() != null
                ? subscription.getExpirationDt().toLocalDate().toString()
                : "-";
        String amount = formatAmount(subscription.getAmount());
        String startDate = subscription.getCreateDt() != null
                ? subscription.getCreateDt().toLocalDate().toString()
                : "-";
        String daysLeft = formatDaysLeft(subscription.getExpirationDt());
        String subscriptionStatus = determineSubscriptionStatus(subscription.getExpirationDt());
        String createdDateTime = formatAuditDateTime(subscription.getCreateDt());
        String createdBy = formatAuditUser(subscription.getCreateBy());
        String modifiedDateTime = formatAuditDateTime(subscription.getModifyDt());
        String modifiedBy = formatAuditUser(subscription.getModifyBy());

        return new SubscriptionView(
                subscription.getId(),
                subscription.getMedicalProviderId(),
                patientId,
                subscription.getMedicalPackageId(),
                providerCode,
                patientName,
                packageName,
                modeDisplay,
                amount,
                startDate,
                expirationDate,
                daysLeft,
                subscriptionStatus,
                createdDateTime,
                createdBy,
                modifiedDateTime,
                modifiedBy,
                subscription.getRemark()
        );
    }

    private String formatMedicalProvider(MedicalProviderEntity provider) {
        if (provider == null) {
            return "-";
        }
        return String.format("%s (%s)", provider.getName(), provider.getCode());
    }

    private String formatPackageName(String packageName) {
        if (packageName == null || packageName.isBlank()) {
            return "-";
        }
        return packageName.trim().toUpperCase(Locale.ENGLISH);
    }

    private String formatAmount(BigDecimal amount) {
        return amount == null ? "-" : "RM " + amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String formatAuditDateTime(LocalDateTime dateTime) {
        return dateTime == null ? "-" : dateTime.format(AUDIT_DATE_TIME_FORMAT);
    }

    private String formatAuditUser(String userId) {
        return userId == null || userId.isBlank() ? "-" : userId;
    }

    private String determineSubscriptionStatus(LocalDateTime expirationDateTime) {
        if (expirationDateTime == null) {
            return "Active";
        }

        LocalDate today = LocalDate.now();
        LocalDate expirationDate = expirationDateTime.toLocalDate();
        if (expirationDate.isBefore(today)) {
            return "Expired";
        }
        if (!expirationDate.isAfter(today.plusDays(30))) {
            return "Expiring Soon";
        }
        return "Active";
    }

    private String formatDaysLeft(LocalDateTime expirationDateTime) {
        if (expirationDateTime == null) {
            return "-";
        }

        long days = ChronoUnit.DAYS.between(LocalDate.now(), expirationDateTime.toLocalDate());
        if (days < 0) {
            return "Expired";
        }
        if (days == 0) {
            return "Expires today";
        }
        if (days == 1) {
            return "1 day left";
        }
        return days + " days left";
    }

    private String mapMode(String mode) {
        if ("YEARLY".equalsIgnoreCase(mode)) {
            return "Y";
        }
        return "M";
    }

    private BigDecimal getAmountForMode(MedicalPackage medicalPackage, String mode) {
        if ("YEARLY".equalsIgnoreCase(mode)) {
            return BigDecimal.valueOf(medicalPackage.getAmountYear());
        }
        return BigDecimal.valueOf(medicalPackage.getAmountMonth());
    }
}
