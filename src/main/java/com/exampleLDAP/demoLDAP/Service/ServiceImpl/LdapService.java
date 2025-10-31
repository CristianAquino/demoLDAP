package com.exampleLDAP.demoLDAP.Service.ServiceImpl;

import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.stereotype.Service;

import javax.naming.directory.Attribute;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.util.List;

@Service
public class LdapService {
    private final LdapTemplate ldapTemplate;
    private static final int UF_ACCOUNTDISABLE = 0x2;

    public LdapService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    // Busca DN por sAMAccountName
    public String findDnBySamAccountName(String samAccountName) {
        EqualsFilter filter = new EqualsFilter("sAMAccountName", samAccountName);
        List<String> result = ldapTemplate.search(
                "", filter.encode(),
                (AttributesMapper<String>) attrs -> {
                    Attribute dnAttr = attrs.get("distinguishedName");
                    return (dnAttr != null) ? (String) dnAttr.get() : null;
                });
        return (result.isEmpty()) ? null : result.get(0);
    }

    // Lee userAccountControl del usuario
    private Integer getUserAccountControl(String userDn) {
        List<Integer> vals = ldapTemplate.lookup(userDn, new String[]{"userAccountControl"},
                (AttributesMapper<Integer>) attrs -> {
                    Attribute a = attrs.get("userAccountControl");
                    return (a != null) ? Integer.parseInt((String) a.get()) : 0;
                });
        return vals.isEmpty() ? 0 : vals.get(0);
    }

    // Modifica userAccountControl aplicando máscara bitwise
    private void modifyUserAccountControl(String userDn, int newVal) {
        Attribute attr = new BasicAttribute("userAccountControl", String.valueOf(newVal));
        ModificationItem item = new ModificationItem(DirContext.REPLACE_ATTRIBUTE, attr);
        ldapTemplate.modifyAttributes(userDn, new ModificationItem[]{item});
    }

    public void blockAccountByDn(String userDn) {
        Integer cur = getUserAccountControl(userDn);
        int newVal = cur | UF_ACCOUNTDISABLE; // set bit
        modifyUserAccountControl(userDn, newVal);
    }

    public void unblockAccountByDn(String userDn) {
        Integer cur = getUserAccountControl(userDn);
        int newVal = cur & ~UF_ACCOUNTDISABLE; // clear bit
        modifyUserAccountControl(userDn, newVal);
    }

    // Métodos públicos que reciben username o DN
    public void blockByUsername(String username) {
        String dn = findDnBySamAccountName(username);
        if (dn == null) throw new RuntimeException("Usuario no encontrado: " + username);
        blockAccountByDn(dn);
    }

    public void unblockByUsername(String username) {
        String dn = findDnBySamAccountName(username);
        if (dn == null) throw new RuntimeException("Usuario no encontrado: " + username);
        unblockAccountByDn(dn);
    }
}
