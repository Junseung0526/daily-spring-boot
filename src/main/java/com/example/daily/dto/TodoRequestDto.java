package com.example.daily.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoRequestDto {

    @NotBlank(message = "제목은 필수로 입력해야 합니다.")
    @Size(min = 2, max = 20, message = "최소 2자 이상 20자 이내로 작성해 주세요.")
    private String title;
    private boolean completed;
}
