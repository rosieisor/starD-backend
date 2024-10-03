package com.web.stard.domain.notification.domain;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

@Getter
@Embeddable
@NoArgsConstructor
public class RelatedURL {

    @NotNull
    private String url;

    public RelatedURL(String url) {
        this.url = url;
    }

}
