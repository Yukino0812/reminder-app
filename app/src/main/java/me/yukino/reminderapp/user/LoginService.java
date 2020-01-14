package me.yukino.reminderapp.user;

import java.io.IOException;

import me.yukino.reminderapp.user.api.LoginApi;
import me.yukino.reminderapp.user.vo.LoginVO;
import me.yukino.reminderapp.util.RetrofitProvider;
import retrofit2.Response;

/**
 * @author Yukino Yukinoshita
 */

public class LoginService {

    public Response<String> login(LoginVO vo) throws IOException {
        return RetrofitProvider.getContent().create(LoginApi.class)
                .login(vo.getUsername(), vo.getPassword())
                .execute();
    }

}
