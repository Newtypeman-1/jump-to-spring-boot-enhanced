package com.newtype.enhanced.domain.question.question.entity;

import com.newtype.enhanced.domain.question.answer.entity.Answer;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Question extends BaseEntity {

    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "question", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private List<Answer> answerList = new ArrayList<>();

    public Question(SiteUser author, String subject, String content) {
        this.author = author;
        this.subject = subject;
        this.content = content;
    }

    public void modify(String subject, String content) {
        this.subject = subject;
        this.content = content;
    }

    public Answer addAnswer(SiteUser author, String content) {
        Answer answer = new Answer(this, author, content);
        answerList.add(answer);
        return answer;
    }
}
