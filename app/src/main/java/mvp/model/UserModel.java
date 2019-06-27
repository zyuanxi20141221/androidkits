package mvp.model;

public class UserModel implements IUser {

    private String username;
    private String password;

    public UserModel(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getUserName() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int checkUserValidity(String name, String pwd) {
        if (name == null || pwd == null || !name.equals(getUserName()) || !pwd.equals(getPassword())) {
            return -1;
        }
        return 0;
    }
}
