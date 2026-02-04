package com.hiddengrowth.backend.analysis.dto;

import jakarta.validation.constraints.NotNull;

public record FinalizeRequest(
        @NotNull Long experienceId
) {
}
