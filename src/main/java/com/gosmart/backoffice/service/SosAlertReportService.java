package com.gosmart.backoffice.service;

import com.gosmart.backoffice.dto.SosAlertReportView;
import com.gosmart.backoffice.dto.SosPushAlertView;
import com.gosmart.backoffice.repo.SosAlertRepository;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class SosAlertReportService {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String LOCATION_IMAGE_BASE_URL =
            "https://dev.beyondzealous.com/file_upload/FileUploadServlet?type=download&name=c_sosalert_";

    private final SosAlertRepository sosAlertRepository;

    public SosAlertReportService(SosAlertRepository sosAlertRepository) {
        this.sosAlertRepository = sosAlertRepository;
    }

    public List<SosAlertReportView> findAlerts(Collection<Integer> allowedMedicalProviderIds) {
        if (allowedMedicalProviderIds == null || allowedMedicalProviderIds.isEmpty()) {
            return List.of();
        }
        return sosAlertRepository.findReportRows(allowedMedicalProviderIds).stream()
                .map(this::toAlertView)
                .toList();
    }

    public List<SosPushAlertView> findCaregiverPushAlerts(
            Integer sosAlertId,
            Collection<Integer> allowedMedicalProviderIds
    ) {
        if (sosAlertId == null || allowedMedicalProviderIds == null || allowedMedicalProviderIds.isEmpty()) {
            return List.of();
        }
        Set<Integer> allowedProviderSet = Set.copyOf(allowedMedicalProviderIds);
        boolean hasAccess = sosAlertRepository.findAlertProvider(sosAlertId)
                .map(row -> row.getMedicalProviderId() != null && allowedProviderSet.contains(row.getMedicalProviderId()))
                .orElse(false);
        if (!hasAccess) {
            return List.of();
        }
        return sosAlertRepository.findCaregiverPushRows(sosAlertId).stream()
                .map(this::toPushView)
                .toList();
    }

    private SosAlertReportView toAlertView(SosAlertRepository.SosAlertReportRow row) {
        String uuid = trimToNull(row.getUuid());
        String locationImageUrl = uuid == null ? null : LOCATION_IMAGE_BASE_URL + uuid + ".png";
        return new SosAlertReportView(
                row.getId(),
                row.getMedicalProviderId(),
                row.getMedicalProviderCode(),
                row.getMedicalProviderName(),
                row.getPatientId(),
                row.getPatientName(),
                row.getCaregiverId(),
                row.getCaregiverName(),
                row.getUuid(),
                row.getLatitude(),
                row.getLongitude(),
                locationImageUrl,
                row.getRemark(),
                row.getStatus(),
                formatDateTime(row.getAlertDateTime()),
                row.getPushAttempts() == null ? 0 : row.getPushAttempts(),
                formatDateTime(row.getAckDateTime())
        );
    }

    private SosPushAlertView toPushView(SosAlertRepository.SosPushAlertRow row) {
        return new SosPushAlertView(
                row.getId(),
                row.getSosAlertId(),
                row.getType(),
                row.getPushReceiverId(),
                row.getPushReceiverName(),
                row.getCount(),
                formatDateTime(row.getCreateDt()),
                row.getCreateBy(),
                formatDateTime(row.getAckDt()),
                formatDuration(row.getCreateDt(), row.getAckDt()),
                row.getRemark(),
                row.getStatus()
        );
    }

    private String formatDateTime(LocalDateTime dateTime) {
        return dateTime == null ? null : DATE_TIME_FORMATTER.format(dateTime);
    }

    private String trimToNull(String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        return value.trim();
    }

    private String formatDuration(LocalDateTime start, LocalDateTime end) {
        if (start == null || end == null) {
            return null;
        }
        long totalSeconds = Math.max(0, Duration.between(start, end).getSeconds());
        long days = totalSeconds / 86_400;
        long hours = (totalSeconds % 86_400) / 3_600;
        long minutes = (totalSeconds % 3_600) / 60;
        long seconds = totalSeconds % 60;

        if (days > 0) {
            return String.format("%dd %02dh %02dm", days, hours, minutes);
        }
        if (hours > 0) {
            return String.format("%dh %02dm %02ds", hours, minutes, seconds);
        }
        if (minutes > 0) {
            return String.format("%dm %02ds", minutes, seconds);
        }
        return String.format("%ds", seconds);
    }
}
