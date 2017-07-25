package io.manticore.android.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.concurent.ThreadPool;
import io.manticore.android.fragment.NetworkFragment;

public class MainActivity extends AppCompatActivity {

    protected @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setFragment();
    }

    @Override
    protected void onPause() {
        super.onPause();

        ThreadPool.getInstance().pause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void setFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment, new NetworkFragment(), "NetworkFragment");
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        ThreadPool.getInstance().shutdown();
    }
}
