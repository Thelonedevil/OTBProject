package com.github.otbproject.otbproject.filter;

import javax.validation.constraints.NotNull;

public class BasicFilter {
    @NotNull
    private String data;
    @NotNull
    private FilterType type;
    private String group = "default";
    private Boolean enabled = true;

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public FilterType getType() {
        return type;
    }

    public void setType(FilterType type) {
        this.type = type;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
