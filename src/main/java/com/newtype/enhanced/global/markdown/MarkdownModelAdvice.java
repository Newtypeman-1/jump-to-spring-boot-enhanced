package com.newtype.enhanced.global.markdown;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

// 모든 화면에서 ${markdown.render(...)}로 마크다운을 HTML로 바꿀 수 있게 모델에 등록
@ControllerAdvice
@RequiredArgsConstructor
public class MarkdownModelAdvice {

    private final MarkdownRenderer markdownRenderer;

    @ModelAttribute("markdown")
    public MarkdownRenderer markdown() {
        return markdownRenderer;
    }
}
