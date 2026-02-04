package com.hiddengrowth.backend.analysis.dto;

import com.hiddengrowth.backend.analysis.JobStatus;

import java.time.Instant;

public record JobResponse(
        Long jobId,
        Long experienceId,
        JobStatus status,
        Instant createdAt,
        Instant updatedAt,
        String errorMessage
) {}
