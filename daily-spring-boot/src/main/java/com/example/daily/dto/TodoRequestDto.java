package com.example.daily.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TodoRequestDto {

    @Schema(description = "할 일 제목", example = "스프링 공부하기")
    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    @Size(min = 2, max = 20, message = "최소 2자 이상 20자 이내로 작성해 주세요.")
    private String title;

    @Schema(description = "완료 여부", example = "false")
    private boolean completed;

    private List<String> tagNames;
}
