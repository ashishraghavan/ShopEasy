package com.shopping.shopeasy.authorization;

/**
 * "azp": "675482710608-6sbo35eqrc3cg7hsusr9di5gtekd4ii4.apps.googleusercontent.com",
 "aud": "675482710608-6sbo35eqrc3cg7hsusr9di5gtekd4ii4.apps.googleusercontent.com",
 "sub": "111676653733526286145",
 "scope": "https://www.googleapis.com/auth/userinfo.email https://www.googleapis.com/auth/userinfo.profile",
 "exp": "1449597045",
 "expires_in": "2000",
 "email": "ashishraghavan13687@gmail.com",
 "email_verified": "true",
 "access_type": "offline"
 */
public class ValidatedToken {
    private String azp;
    private String aud;
    private String sub;
    private String scope;
    private String exp;
    private String expires_in;
    private String email;
    private String email_verified;
    private String access_type;

    public ValidatedToken(){}

    public String getAzp() {
        return azp;
    }

    public String getAud() {
        return aud;
    }

    public String getSub() {
        return sub;
    }

    public String getScope() {
        return scope;
    }

    public String getExp() {
        return exp;
    }

    public String getExpires_in() {
        return expires_in;
    }

    public String getEmail() {
        return email;
    }

    public String getEmail_verified() {
        return email_verified;
    }

    public String getAccess_type() {
        return access_type;
    }

    public void setAzp(String azp) {
        this.azp = azp;
    }

    public void setAud(String aud) {
        this.aud = aud;
    }

    public void setSub(String sub) {
        this.sub = sub;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public void setExp(String exp) {
        this.exp = exp;
    }

    public void setExpires_in(String expires_in) {
        this.expires_in = expires_in;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setEmail_verified(String email_verified) {
        this.email_verified = email_verified;
    }

    public void setAccess_type(String access_type) {
        this.access_type = access_type;
    }
}
