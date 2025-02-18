package spa.lyh.cn.permissionutils.demo;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import spa.lyh.cn.permissionutils.AskPermission;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> a = new ArrayList<>();
        a.add("a");
        a.add("b");
        AskPermission
                .with(this);
    }
}
