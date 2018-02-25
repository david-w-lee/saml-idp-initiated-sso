package com.davidwlee.service;

import com.davidwlee.model.SamlConfig;

public interface SamlService {
    SamlConfig getDefaultSamlConfig();
    String getSamlResponse(SamlConfig config);
}
