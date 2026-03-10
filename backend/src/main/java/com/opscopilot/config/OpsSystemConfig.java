package com.opscopilot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * 运维系统API配置
 */
@Configuration
@ConfigurationProperties(prefix = "ops-system")
public class OpsSystemConfig {

    // 监控系统配置
    private MonitorConfig monitor = new MonitorConfig();
    
    // CMDB配置
    private CmdbConfig cmdb = new CmdbConfig();
    
    // 工单系统配置
    private TicketSystemConfig ticket = new TicketSystemConfig();
    
    // 知识库配置
    private KnowledgeConfig knowledge = new KnowledgeConfig();

    public MonitorConfig getMonitor() { return monitor; }
    public void setMonitor(MonitorConfig monitor) { this.monitor = monitor; }
    public CmdbConfig getCmdb() { return cmdb; }
    public void setCmdb(CmdbConfig cmdb) { this.cmdb = cmdb; }
    public TicketSystemConfig getTicket() { return ticket; }
    public void setTicket(TicketSystemConfig ticket) { this.ticket = ticket; }
    public KnowledgeConfig getKnowledge() { return knowledge; }
    public void setKnowledge(KnowledgeConfig knowledge) { this.knowledge = knowledge; }

    public static class MonitorConfig {
        private String apiUrl;
        private String apiKey;
        private String enabled = "false";

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getEnabled() { return enabled; }
        public void setEnabled(String enabled) { this.enabled = enabled; }
    }

    public static class CmdbConfig {
        private String apiUrl;
        private String apiKey;
        private String enabled = "false";

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getEnabled() { return enabled; }
        public void setEnabled(String enabled) { this.enabled = enabled; }
    }

    public static class TicketSystemConfig {
        private String apiUrl;
        private String apiKey;
        private String enabled = "false";

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getEnabled() { return enabled; }
        public void setEnabled(String enabled) { this.enabled = enabled; }
    }

    public static class KnowledgeConfig {
        private String apiUrl;
        private String apiKey;
        private String vectorEnabled = "false";

        public String getApiUrl() { return apiUrl; }
        public void setApiUrl(String apiUrl) { this.apiUrl = apiUrl; }
        public String getApiKey() { return apiKey; }
        public void setApiKey(String apiKey) { this.apiKey = apiKey; }
        public String getVectorEnabled() { return vectorEnabled; }
        public void setVectorEnabled(String vectorEnabled) { this.vectorEnabled = vectorEnabled; }
    }
}
