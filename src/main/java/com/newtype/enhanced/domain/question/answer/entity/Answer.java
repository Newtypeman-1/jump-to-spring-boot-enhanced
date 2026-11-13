package com.newtype.enhanced.domain.question.answer.entity;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Answer extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    private Question question;

    public Answer(Question question, String content) {
        this.question = question;
        this.content = content;
    }
}
