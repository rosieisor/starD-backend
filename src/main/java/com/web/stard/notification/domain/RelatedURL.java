package com.web.stard.notification.domain;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Embeddable;
import javax.validation.constraints.NotNull;

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
