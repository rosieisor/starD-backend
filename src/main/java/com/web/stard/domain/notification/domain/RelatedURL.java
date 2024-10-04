package com.web.stard.domain.notification.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;

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
