package com.hiddengrowth.backend.analysis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalysisWorker {

    private final AnalysisJobRepository jobRepository;
    private final AnalysisResultRepository resultRepository;
    private static final long TIMEOUT_SECONDS = 300;

    private void handleTimeouts() {
        Instant cutoff = Instant.now().minusSeconds(TIMEOUT_SECONDS);
        List<AnalysisJob> timedOut = jobRepository.findTimedOutRunningForUpdate(
                cutoff, PageRequest.of(0, 20)
        );

        for (AnalysisJob job : timedOut) {
            if (job.canRetry()) {
                job.markRetry("timeout: exceeded " + TIMEOUT_SECONDS + "s");
            } else {
                job.markFailed("timeout: exceeded " + TIMEOUT_SECONDS + "s");
            }
        }
    }


    @Scheduled(fixedDelayString = "${analysis.worker.fixedDelayMs:3000}")
    @Transactional
    public void pollAndRun() {
        handleTimeouts();
        List<AnalysisJob> jobs = jobRepository.findPendingForUpdate(PageRequest.of(0, 5));
        if (jobs.isEmpty()) return;

        for (AnalysisJob job : jobs) {
            try {
                // 중복 방지
                if (resultRepository.findByJobId(job.getId()).isPresent()) {
                    job.markDone();
                    continue;
                }

                job.markRunning();

                // ✅ FastAPI 대신 더미 결과 생성
                String resultJson = buildDummyResult(job);

                resultRepository.save(new AnalysisResult(job.getId(), resultJson));
                job.markDone();

                log.info("analysis job done. jobId={}", job.getId());
            } catch (Exception e) {
                log.error("analysis job failed. jobId={}", job.getId(), e);
                String msg = e.getMessage() != null ? e.getMessage() : "unknown error";

                if (job.canRetry()) {
                    job.markRetry("retry: " + msg);
                } else {
                    job.markFailed(msg);
                }
            }
        }
    }

    private String buildDummyResult(AnalysisJob job) {
        return """
        {
          "experienceId": %d,
          "summary": "MVP dummy result (backend only)",
          "skills": [
            {"name": "Problem Solving", "score": 78},
            {"name": "Communication", "score": 72},
            {"name": "Teamwork", "score": 70}
          ],
          "recommendedRoles": ["Backend Developer", "Infra Engineer"],
          "dailyChallenge": {
            "title": "로그 남기기 습관 만들기",
            "detail": "오늘 작업한 API 한 개에 대해 요청/응답/예외 케이스를 README에 5줄로 정리"
          }
        }
        """.formatted(job.getExperienceId());
    }
}
