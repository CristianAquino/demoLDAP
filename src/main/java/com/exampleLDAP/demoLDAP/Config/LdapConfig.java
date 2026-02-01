package com.exampleLDAP.demoLDAP.Config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;

@Configuration
public class LdapConfig {

    @Bean
    public LdapContextSource contextSource() {
        LdapContextSource ctx = new LdapContextSource();
        ctx.setUrl("ldap://192.168.1.6:389");
        ctx.setBase("dc=suna,dc=lab");
        ctx.setUserDn("cn=Administrador,cn=Users");
        ctx.setPassword("123qweASD+");
        ctx.afterPropertiesSet();
        return ctx;
    }

    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }
}
