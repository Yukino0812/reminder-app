package me.yukino.reminderapp.user;

import java.io.IOException;

import me.yukino.reminderapp.user.api.RegisterApi;
import me.yukino.reminderapp.user.vo.RegisterVO;
import me.yukino.reminderapp.util.RetrofitProvider;
import retrofit2.Response;

/**
 * @author Yukino Yukinoshita
 */

public class RegisterService {

    public Response<String> register(RegisterVO vo) throws IOException {
        return RetrofitProvider.getContent().create(RegisterApi.class)
                .register(vo.getUsername(), vo.getPassword(), vo.getMail(), vo.getCode())
                .execute();
    }

}
