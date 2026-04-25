package com.demoApp.demoApp.repository;

import com.demoApp.demoApp.entity.Appointment;
import com.demoApp.demoApp.entity.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Integer> {

    @Query("""
        SELECT a
        FROM Appointment a
        WHERE a.technician.id = :technicianId
          AND a.status <> :excludedStatus
          AND a.startAt < :endAt
          AND a.endAt > :startAt
        """)
    List<Appointment> findOverlaps(
            @Param("technicianId") int technicianId,
            @Param("startAt") LocalDateTime startAt,
            @Param("endAt") LocalDateTime endAt,
            @Param("excludedStatus") AppointmentStatus excludedStatus
    );

    @Query("""
        SELECT a
        FROM Appointment a
        JOIN FETCH a.client
        JOIN FETCH a.technician
        WHERE a.startAt >= :startInclusive
          AND a.startAt < :endExclusive
        ORDER BY a.startAt ASC
        """)
    List<Appointment> findForCalendarRange(
            @Param("startInclusive") LocalDateTime startInclusive,
            @Param("endExclusive") LocalDateTime endExclusive
    );
}

