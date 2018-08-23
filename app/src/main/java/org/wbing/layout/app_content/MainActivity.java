package org.wbing.layout.app_content;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import org.wbing.base.ui.impl.WAct;
import org.wbing.layout.app_content.databinding.ActivityMainBinding;

public class MainActivity extends WAct<ActivityMainBinding> {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int layoutId() {
        return R.layout.activity_main;
    }

    @Override
    public void loadData() {
        getHandler().postDelayed(() -> getBinding().content.showError(), 3000);

        getBinding().content.setRetryListener(v -> getBinding().content.showContent());

        getBinding().text.setOnClickListener(v -> getBinding().content.showEmpty());
    }

    @Override
    public void recycle() {

    }

}
