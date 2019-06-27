package mvp.model;

/**
 * 数据模型层接口定义
 */
public interface IUser {

    String getUserName();

    String getPassword();

    int checkUserValidity(String name, String pwd);
}
