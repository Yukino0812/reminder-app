package me.yukino.reminderapp.user.vo;

/**
 * @author Yukino Yukinoshita
 */

public class RegisterVO {

    private String username;
    private String password;
    private String mail;
    private String code;

    public RegisterVO() {
    }

    public RegisterVO(String username, String password, String mail, String code) {
        this.username = username;
        this.password = password;
        this.mail = mail;
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "RegisterVO{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", mail='" + mail + '\'' +
                ", code='" + code + '\'' +
                '}';
    }
}
