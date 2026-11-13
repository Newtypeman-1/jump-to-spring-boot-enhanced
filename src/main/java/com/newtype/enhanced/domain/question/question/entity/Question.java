package com.newtype.enhanced.domain.question.question.entity;

import com.newtype.enhanced.global.jpa.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Question extends BaseEntity {

    @Column(length = 200)
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String content;
}
