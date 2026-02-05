package com.hiddengrowth.backend.analysis;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface AnalysisJobRepository extends JpaRepository<AnalysisJob, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select j
        from AnalysisJob j
        where j.status = com.hiddengrowth.backend.analysis.JobStatus.PENDING
        order by j.id asc
    """)
    List<AnalysisJob> findPendingForUpdate(Pageable pageable);

    Optional<AnalysisJob> findTopByExperienceIdAndStatusInOrderByIdDesc(
            Long experienceId, List<JobStatus> statuses
    );

    Optional<AnalysisJob> findTopByExperienceIdOrderByIdDesc(Long experienceId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        select j
        from AnalysisJob j
        where j.status = com.hiddengrowth.backend.analysis.JobStatus.RUNNING
          and j.startedAt is not null
          and j.startedAt < :cutoff
        order by j.id asc
    """)
    List<AnalysisJob> findTimedOutRunningForUpdate(Instant cutoff, Pageable pageable);
}
