package com.solux.sm137.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScrapRequest {
    @NotNull
    private Long complaintId;
}
