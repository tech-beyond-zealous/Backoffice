package com.gosmart.backoffice.repo;

import com.gosmart.backoffice.domain.SosAlertEntity;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SosAlertRepository extends JpaRepository<SosAlertEntity, Integer> {

    interface SosAlertReportRow {
        Integer getId();

        Integer getMedicalProviderId();

        String getMedicalProviderCode();

        String getMedicalProviderName();

        Long getPatientId();

        String getPatientName();

        Long getCaregiverId();

        String getCaregiverName();

        String getUuid();

        String getLatitude();

        String getLongitude();

        String getRemark();

        String getStatus();

        LocalDateTime getAlertDateTime();

        Integer getPushAttempts();

        LocalDateTime getAckDateTime();
    }

    interface SosPushAlertRow {
        Integer getId();

        Integer getSosAlertId();

        Integer getType();

        Long getPushReceiverId();

        String getPushReceiverName();

        Integer getCount();

        LocalDateTime getCreateDt();

        String getCreateBy();

        LocalDateTime getAckDt();

        String getRemark();

        String getStatus();
    }

    interface SosAlertProviderRow {
        Integer getId();

        Integer getMedicalProviderId();
    }

    @Query(value = """
            SELECT
                sa.id AS id,
                pc.medical_provider_id AS medicalProviderId,
                mp.code AS medicalProviderCode,
                mp.name AS medicalProviderName,
                pr.id AS patientId,
                pr.name AS patientName,
                cg.id AS caregiverId,
                cg.name AS caregiverName,
                sa.uuid AS uuid,
                sa.latitude AS latitude,
                sa.longitude AS longitude,
                sa.remark AS remark,
                CASE
                    WHEN MAX(spa.ack_dt) IS NOT NULL THEN 'Acknowledged'
                    ELSE 'Pending'
                END AS status,
                sa.create_dt AS alertDateTime,
                COALESCE(MAX(spa.count), 0) AS pushAttempts,
                MAX(spa.ack_dt) AS ackDateTime
            FROM sos_alert sa
            LEFT JOIN patient_caregiver pc
                ON pc.id = sa.patient_caregiver_id
            LEFT JOIN patient_registration pr
                ON pr.id = pc.patient_id
            LEFT JOIN caregiver cg
                ON cg.id = pc.caregiver_id
            LEFT JOIN medical_provider mp
                ON mp.id = pc.medical_provider_id
            LEFT JOIN sos_push_alert spa
                ON spa.sos_alert_id = sa.id
                AND spa.type = 1
            WHERE pc.medical_provider_id IN (:medicalProviderIds)
            GROUP BY
                sa.id,
                pc.medical_provider_id,
                mp.code,
                mp.name,
                pr.id,
                pr.name,
                cg.id,
                cg.name,
                sa.uuid,
                sa.latitude,
                sa.longitude,
                sa.remark,
                sa.status,
                sa.create_dt
            ORDER BY sa.create_dt DESC, sa.id DESC
            """, nativeQuery = true)
    List<SosAlertReportRow> findReportRows(@Param("medicalProviderIds") Collection<Integer> medicalProviderIds);

    @Query(value = """
            SELECT
                spa.id AS id,
                spa.sos_alert_id AS sosAlertId,
                spa.type AS type,
                spa.push_receiver_id AS pushReceiverId,
                cg.name AS pushReceiverName,
                spa.count AS count,
                spa.create_dt AS createDt,
                spa.create_by AS createBy,
                spa.ack_dt AS ackDt,
                spa.remark AS remark,
                spa.status AS status
            FROM sos_push_alert spa
            LEFT JOIN sos_alert sa
                ON sa.id = spa.sos_alert_id
            LEFT JOIN patient_caregiver pc
                ON pc.id = sa.patient_caregiver_id
                AND pc.status = 'A'
            LEFT JOIN caregiver cg
                ON cg.id = pc.caregiver_id
                AND cg.status = 'A'
            WHERE spa.sos_alert_id = :sosAlertId
                AND spa.type = 1
                AND sa.status = 'A'
            ORDER BY spa.create_dt DESC, spa.id DESC
            """, nativeQuery = true)
    List<SosPushAlertRow> findCaregiverPushRows(@Param("sosAlertId") Integer sosAlertId);

    @Query(value = """
            SELECT
                sa.id AS id,
                pc.medical_provider_id AS medicalProviderId
            FROM sos_alert sa
            LEFT JOIN patient_caregiver pc
                ON pc.id = sa.patient_caregiver_id
            WHERE sa.id = :sosAlertId
            LIMIT 1
            """, nativeQuery = true)
    Optional<SosAlertProviderRow> findAlertProvider(@Param("sosAlertId") Integer sosAlertId);
}
