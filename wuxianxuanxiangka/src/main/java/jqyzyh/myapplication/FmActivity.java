package jqyzyh.myapplication;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class FmActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fm);
        PageFragment f = new PageFragment();
        Bundle bundle = new Bundle();
        bundle.putString("text", "页面" + 1);
        f.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.fralay_content, f).commit();
    }
}
