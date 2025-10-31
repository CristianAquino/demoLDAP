package com.exampleLDAP.demoLDAP.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {
    @Value("${spring.ldap.urls}")
    private String ldapUrls;

    @Value("${spring.ldap.base}")
    private String ldapBase;

    @Value("${spring.ldap.username}")
    private String ldapUser;

    @Value("${spring.ldap.password}")
    private String ldapPassword;

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ctx = new LdapContextSource();
        ctx.setUrl(ldapUrls);
        ctx.setBase(ldapBase);
        ctx.setUserDn(ldapUser);
        ctx.setPassword(ldapPassword);
        ctx.afterPropertiesSet();
        return ctx;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
