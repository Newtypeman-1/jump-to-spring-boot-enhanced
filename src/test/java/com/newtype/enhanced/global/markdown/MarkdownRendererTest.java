package com.newtype.enhanced.global.markdown;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MarkdownRendererTest {

    private final MarkdownRenderer markdown = new MarkdownRenderer();

    @Test
    @DisplayName("마크다운 문법을 HTML로 바꾼다")
    void t1() {
        assertThat(markdown.render("**굵게** 그리고 `코드`"))
                .isEqualTo("<p><strong>굵게</strong> 그리고 <code>코드</code></p>\n");
    }

    @Test
    @DisplayName("사용자가 직접 쓴 HTML 태그는 실행되지 않도록 문자로 바꾼다")
    void t2() {
        assertThat(markdown.render("<script>alert(1)</script>"))
                .doesNotContain("<script>")
                .contains("&lt;script&gt;");
    }

    @Test
    @DisplayName("javascript: 링크는 제거한다")
    void t3() {
        assertThat(markdown.render("[클릭](javascript:alert(1))"))
                .doesNotContain("javascript:");
    }
}
