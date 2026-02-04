package com.hiddengrowth.backend.analysis;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "analysis_jobs")
public class AnalysisJob {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long experienceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private JobStatus status = JobStatus.PENDING;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    private Instant updatedAt;

    @Column(length = 1000)
    private String errorMessage;

    public AnalysisJob(Long experienceId){
        this.experienceId = experienceId;
        this.updatedAt = Instant.now();
    }

    public void markDone(){
        this.status = JobStatus.DONE;
        this.updatedAt = Instant.now();
    }

    public void markFailed(String msg){
        this.status = JobStatus.FAILED;
        this.errorMessage = msg;
        this.updatedAt = Instant.now();
    }
}
