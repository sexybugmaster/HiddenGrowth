package com.hiddengrowth.backend.analysis;

import com.hiddengrowth.backend.analysis.dto.JobResponse;
import com.hiddengrowth.backend.analysis.dto.JobResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class AnalysisService {

    private final AnalysisJobRepository jobRepository;
    private final AnalysisResultRepository resultRepository;

    @Transactional
    public JobResponse finalizeJob(Long experienceId) {
        AnalysisJob job = jobRepository.save(new AnalysisJob(experienceId));
        return toJobResponse(job);
    }

    @Transactional(readOnly = true)
    public JobResponse getJob(Long jobId) {
        AnalysisJob job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found"));
        return toJobResponse(job);
    }

    @Transactional(readOnly = true)
    public JobResultResponse getResult(Long jobId) {
        // job 존재 확인 (원하면 DONE 체크도 여기서 가능)
        if (!jobRepository.existsById(jobId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "job not found");
        }

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
