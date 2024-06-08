package com.platformteam.k8s;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class NamespaceService {

    @Value("${NAMESPACE:default}")
    private String currentNamespace;

    public String getCurrentNamespace() {
        return currentNamespace;
    }
}
