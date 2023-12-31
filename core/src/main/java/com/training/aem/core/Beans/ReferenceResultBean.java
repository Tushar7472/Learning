package com.training.aem.core.Beans;

import java.util.HashSet;
import java.util.Set;

public class ReferenceResultBean {

    String assetPath;
    Set<String> cfReference = new HashSet<>();
    Set<String> pageReference = new HashSet<>();

    boolean hasReferences;

    public boolean isHasReferences() {
        return hasReferences;
    }

    public void setHasReferences(boolean hasReferences) {
        this.hasReferences = hasReferences;
    }

    Set<String> otherReference = new HashSet<>();

    public String getAssetPath() {
        return assetPath;
    }

    public void setAssetPath(String assetPath) {
        this.assetPath = assetPath;
    }

    public Set<String> getCfReference() {
        return cfReference;
    }

    public void setCfReference(Set<String> cfReference) {
        this.cfReference = cfReference;
    }

    public Set<String> getPageReference() {
        return pageReference;
    }

    public void setPageReference(Set<String> pageReference) {
        this.pageReference = pageReference;
    }

    public Set<String> getOtherReference() {
        return otherReference;
    }

    public void setOtherReference(Set<String> otherReference) {
        this.otherReference = otherReference;
    }
}
