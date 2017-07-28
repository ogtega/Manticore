package io.manticore.android.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.R;
import io.manticore.android.fragment.NetworkFragment;
import io.manticore.android.fragment.PreferenceFragment;

public class MainActivity extends AppCompatActivity {

    protected @BindView(R.id.toolbar) Toolbar mToolbar;

    private Fragment current;
    private NetworkFragment netFragment = new NetworkFragment();
    private PreferenceFragment prefFragment = new PreferenceFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        setFragment(netFragment);
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
                setFragment(prefFragment);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {

        if (current == netFragment) {
            super.onBackPressed();
        } else {
            setFragment(netFragment);
        }
    }

    public void setFragment(Fragment fragment) {

        if (current != fragment) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment, fragment)
                    .commit();
        }

        current = fragment;
    }
}
