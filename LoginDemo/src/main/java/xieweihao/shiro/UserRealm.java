package shiro;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import entity.ActiveUser;
import entity.User;
import service.UserService;
import service.impl.UserServiceImpl;


/**
 * 自定义Realm，Shiro过滤器会从这里获取数据对用户进行认证
 * Created by ozc on 2017/12/8.
 *
 * @author ozc
 * @version 1.0
 */
public class UserRealm extends AuthorizingRealm {


    @Autowired
    private UserService userService;

    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return null;
    }

    /**
     * Shiro认证就是从这里获取数据的
     *
     * @param token
     * @return
     * @throws AuthenticationException
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {

        //得到用户名
        String userCode = (String) token.getPrincipal();


        //得到密码
        String password = new String((char[])token.getCredentials());


        User user = userService.validateUserExist(userCode);

        if (user == null) {
            return null;
        }

        //将数据存到用户的身份信息实体中
        ActiveUser activeUser = new ActiveUser();
        activeUser.setUserId(user.getUserId());
        activeUser.setUserEmail(user.getUserEmail());
        activeUser.setUserNickname(user.getUserNickname());
        activeUser.setPassword(password);


        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(
                activeUser, user.getUserPassword(), ByteSource.Util.bytes(user.getSalt()), this.getName());
        return simpleAuthenticationInfo;
    }

    //清除缓存
    public void clearCached() {
        PrincipalCollection principals = SecurityUtils.getSubject().getPrincipals();
        super.clearCache(principals);
    }
}
