package com.newtype.enhanced.global.markdown;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.springframework.stereotype.Component;

@Component
public class MarkdownRenderer {

    private final Parser parser = Parser.builder().build();

    // 사용자가 쓴 HTML 태그는 그대로 출력하지 않고 문자로 바꾸고(escapeHtml),
    // javascript: 같은 위험한 링크 주소는 제거(sanitizeUrls)
    private final HtmlRenderer renderer = HtmlRenderer.builder()
            .escapeHtml(true)
            .sanitizeUrls(true)
            .build();

    public String render(String markdown) {
        if (markdown == null) return "";
        Node document = parser.parse(markdown);
        return renderer.render(document);
    }
}
