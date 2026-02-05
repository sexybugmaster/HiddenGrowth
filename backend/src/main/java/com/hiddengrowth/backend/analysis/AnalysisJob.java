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

    @Column(nullable = false)
    private int attempts = 0;

    @Column(nullable = false)
    private int maxAttempts = 3;

    private Instant startedAt;
    private Instant finishedAt;

    public AnalysisJob(Long experienceId){
        this.experienceId = experienceId;
        this.updatedAt = Instant.now();
    }

    public void markDone(){
        this.status = JobStatus.DONE;
        this.finishedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markFailed(String msg){
        this.status = JobStatus.FAILED;
        this.errorMessage = msg;
        this.finishedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public void markRunning(){
        this.status = JobStatus.RUNNING;
        this.startedAt = Instant.now();
        this.updatedAt = Instant.now();
    }

    public boolean canRetry(){
        return this.attempts < this.maxAttempts;
    }

    public void markRetry(String msg){
        this.attempts++;
        this.status = JobStatus.PENDING;
        this.errorMessage = msg;
        this.startedAt = null;
        this.finishedAt = null;
        this.updatedAt = Instant.now();
    }

    public boolean isTimeOut(Instant now, long timeoutSeconds){
        return this.status == JobStatus.RUNNING
                && this.startedAt != null
                && this.startedAt.plusSeconds(timeoutSeconds).isBefore(now);
    }
}
