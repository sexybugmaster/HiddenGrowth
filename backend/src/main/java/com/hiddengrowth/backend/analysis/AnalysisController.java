package com.hiddengrowth.backend.analysis;

import com.hiddengrowth.backend.analysis.dto.FinalizeRequest;
import com.hiddengrowth.backend.analysis.dto.JobResponse;
import com.hiddengrowth.backend.analysis.dto.JobResultResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/analysis")
public class AnalysisController {

    private final AnalysisJobRepository jobRepository;
    private final AnalysisResultRepository resultRepository;

    // MVP: finalize는 "잡만 만든다" (실제 분석은 다음 단계에서 AI 호출/스케줄러로 처리)
    @PostMapping("/finalize")
    @ResponseStatus(HttpStatus.CREATED)
    public JobResponse finalize(@RequestBody @Valid FinalizeRequest req) {
        AnalysisJob job = jobRepository.save(new AnalysisJob(req.experienceId()));
        return toJobResponse(job);
    }

    @GetMapping("/jobs/{jobId}")
    public JobResponse getJob(@PathVariable Long jobId) {
        AnalysisJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        return toJobResponse(job);
    }

    @GetMapping("/jobs/{jobId}/result")
    public JobResultResponse getResult(@PathVariable Long jobId) {
        AnalysisResult result = resultRepository.findByJobId(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "result not found"));
        return new JobResultResponse(jobId, result.getResultJson());
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
