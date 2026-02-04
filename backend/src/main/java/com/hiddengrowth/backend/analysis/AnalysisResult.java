package com.hiddengrowth.backend.analysis;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "analysis_results", uniqueConstraints = {
        @UniqueConstraint(name = "uk_analysis_results_job", columnNames = {"jobId"})
})
public class AnalysisResult {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable =false)
    private Long jobId;

    @Lob
    @Column(nullable =false, columnDefinition = "LONGTEXT")
    private String resultJson;

    @Column(nullable = false)
    private Instant createdAt =  Instant.now();

    public AnalysisResult(Long jobId, String resultJson){
        this.jobId = jobId;
        this.resultJson = resultJson;
    }
}
