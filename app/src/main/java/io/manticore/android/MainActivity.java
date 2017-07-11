package io.manticore.android;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.manticore.android.fragment.NetworkScanner;
import io.manticore.android.fragment.WifiScanner;
import io.manticore.android.util.NetUtils;

public class MainActivity extends AppCompatActivity {

    protected @BindView(R.id.toolbar) Toolbar mToolbar;

    @OnClick(R.id.fab)
    protected void newPolicy(View view) {
        Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();

        toggleFragment();
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

    public void updateFragment() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("The network state has changed").setMessage("Do you want to switch your scanning mode?");

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User dismissed the dialog
            }
        });

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                toggleFragment();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void toggleFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (NetUtils.isOnWifi(this)) {
            transaction.replace(R.id.fragment, new NetworkScanner(), NetworkScanner.TAG);
        } else {
            transaction.replace(R.id.fragment, new WifiScanner(), WifiScanner.TAG);
        }

        transaction.commit();
    }
}
