package io.manticore.android;

import android.Manifest;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.manticore.android.fragment.NetworkFragment;
import io.manticore.android.fragment.WifiFragment;
import io.manticore.android.receiver.WifiReceiver;
import io.manticore.android.scanner.NetworkScanner;
import io.manticore.android.util.WifiUtils;
import pub.devrel.easypermissions.EasyPermissions;

public class MainActivity extends AppCompatActivity {

    private final int RC_COARSE_LOCATION = 0xFFFF;

    protected @BindView(R.id.toolbar) Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateFragment();
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

    public void methodPrompt() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this)
                .setTitle("The network state has changed")
                .setMessage("Do you want to switch your scanning mode?")
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User dismissed the dialog
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        updateFragment();
                    }
                });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public void updateFragment() {

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();

        if (WifiUtils.isOnWifi(this)) {
            if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_COARSE_LOCATION)) {
                transaction.replace(R.id.fragment, new NetworkFragment(), NetworkScanner.TAG);
            } else {
                EasyPermissions.requestPermissions(this, getString(R.string.permission_location_rationale), RC_COARSE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION);
            }
        } else {
            transaction.replace(R.id.fragment, new WifiFragment(), WifiReceiver.TAG);
        }

        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }
}
