package com.hiddengrowth.backend.analysis;

import com.hiddengrowth.backend.analysis.dto.FinalizeRequest;
import com.hiddengrowth.backend.analysis.dto.JobResponse;
import com.hiddengrowth.backend.analysis.dto.JobResultPendingResponse;
import com.hiddengrowth.backend.analysis.dto.JobResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisJobRepository jobRepository;
    private final AnalysisResultRepository resultRepository;

    @PostMapping("/finalize")
    public ResponseEntity<JobResponse> finalize(@RequestBody @Valid FinalizeRequest req) {

        // ✅ 최근 job 재사용 (DONE/FAILED 포함)
        var latestOpt = jobRepository.findTopByExperienceIdOrderByIdDesc(req.experienceId());

        if (latestOpt.isPresent()) {
            AnalysisJob latest = latestOpt.get();

            // TTL: 10분 (원하면 조정)
            var cutoff = java.time.Instant.now().minusSeconds(600);

            if (latest.getCreatedAt() != null && latest.getCreatedAt().isAfter(cutoff)) {
                return ResponseEntity.ok(toJobResponse(latest)); // 200
            }
        }

        AnalysisJob job = jobRepository.save(new AnalysisJob(req.experienceId()));
        return ResponseEntity.status(HttpStatus.CREATED).body(toJobResponse(job)); // 201
    }


    @GetMapping("/jobs/{jobId}")
    public JobResponse getJob(@PathVariable Long jobId) {
        AnalysisJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        return toJobResponse(job);
    }

    /**
     * 결과 조회 정책 (202 Accepted)
     * - DONE: 200 + resultJson
     * - PENDING/RUNNING: 202 + status payload
     * - FAILED: 422 + status payload(에러메시지 포함)
     */
    @GetMapping("/jobs/{jobId}/result")
    public ResponseEntity<?> getResult(@PathVariable Long jobId) {
        AnalysisJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));

        if (job.getStatus() == JobStatus.DONE) {
            AnalysisResult result = resultRepository.findByJobId(jobId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "result not found"));
            return ResponseEntity.ok(new JobResultResponse(jobId, result.getResultJson()));
        }

        JobResultPendingResponse payload = new JobResultPendingResponse(
                job.getId(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getErrorMessage()
        );

        if (job.getStatus() == JobStatus.FAILED) {
            return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(payload);
        }
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(payload);
    }

    private static JobResponse toJobResponse(AnalysisJob job) {
        return new JobResponse(
                job.getId(),
                job.getExperienceId(),
                job.getStatus(),
                job.getCreatedAt(),
                job.getUpdatedAt(),
                job.getErrorMessage()
        );
    }
}
