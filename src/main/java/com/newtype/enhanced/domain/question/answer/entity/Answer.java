package com.newtype.enhanced.domain.question.answer.entity;

import com.newtype.enhanced.domain.question.question.entity.Question;
import com.newtype.enhanced.domain.user.user.entity.SiteUser;
import com.newtype.enhanced.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static jakarta.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
public class Answer extends BaseEntity {

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = LAZY)
    private Question question;

    @ManyToOne(fetch = LAZY)
    private SiteUser author;

    @ManyToMany
    private Set<SiteUser> voter = new HashSet<>();

    public Answer(Question question, SiteUser author, String content) {
        this.question = question;
        this.author = author;
        this.content = content;
    }

    public void modify(String content) {
        this.content = content;
    }

    public void vote(SiteUser user) {
        voter.add(user);
    }
}
