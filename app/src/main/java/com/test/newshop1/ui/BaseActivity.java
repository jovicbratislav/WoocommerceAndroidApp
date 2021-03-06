package com.test.newshop1.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.test.newshop1.R;
import com.test.newshop1.data.database.customer.Customer;
import com.test.newshop1.ui.categoryActivity.CategoryActivity;
import com.test.newshop1.ui.checkoutActivity.CheckoutActivity;
import com.test.newshop1.ui.homeActivity.HomeActivity;
import com.test.newshop1.ui.loginActivity.LoginActivity;
import com.test.newshop1.ui.ordersActivity.OrdersActivity;
import com.test.newshop1.ui.profileActivity.ProfileActivity;
import com.test.newshop1.utilities.InjectorUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProviders;

public abstract class BaseActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "BaseActivity";
    private BaseActivityViewModel viewModel;
    private int lastLoggedInCustomerId = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: called");
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.drawer);

        if (viewModel == null){
            ViewModelFactory factory = InjectorUtil.provideViewModelFactory(this);
            viewModel = ViewModelProviders.of(this, factory).get(BaseActivityViewModel.class);
        }



    }

    @Override
    public void setContentView(int layoutResID) {
        Log.d(TAG, "setContentView: called");
        DrawerLayout fullView = (DrawerLayout) getLayoutInflater().inflate(R.layout.drawer, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.container);
        getLayoutInflater().inflate(layoutResID, activityContainer, true);
        super.setContentView(fullView);

        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        setupNavigationView(navigationView);
        setupObservers(navigationView.getHeaderView(0));

    }

    public void setupNavigationView(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setupObservers(View navigationHeader) {
        viewModel.getLoggedInCustomer().observe(this, customer -> updateUI(navigationHeader, customer));
    }

    private void updateUI(View navigationHeader, Customer customer) {

        if (navigationHeader != null ) {
            TextView fullNameTV = navigationHeader.findViewById(R.id.nav_head_tv);
            TextView usernameTV = navigationHeader.findViewById(R.id.username_TV);

            if (customer != null) {

                String name = customer.getFirstName() + " " + customer.getLastName();
                fullNameTV.setText(name);
                usernameTV.setText(customer.getEmail());

                lastLoggedInCustomerId = customer.getId();
                navigationHeader.setOnClickListener(v -> onNavigationHeaderClicked(true));
            } else {
                navigationHeader.setOnClickListener(v -> onNavigationHeaderClicked(false));

                fullNameTV.setText("ورود و ثبت نام");
                usernameTV.setText(" ");
            }
        }

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_home:
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                break;
            case R.id.nav_category:
                startActivity(new Intent(getApplicationContext(), CategoryActivity.class));
                break;
            case R.id.nav_cart:
                startActivity(new Intent(getApplicationContext(), CheckoutActivity.class));
                break;
            case R.id.nav_orders:
                startActivity(new Intent(getApplicationContext(), OrdersActivity.class));

                break;
            case R.id.nav_social:
                Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show();
                //TODO
                break;
            case R.id.nav_contact:
                Toast.makeText(this, "Not Implemented", Toast.LENGTH_SHORT).show();
                //TODO
                break;

            case R.id.nav_logout:
                logout();
                return true;

        }
        overridePendingTransition(R.anim.enter_from_left, R.anim.fade_out);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    private void logout() {
        if (lastLoggedInCustomerId != 0) {
            viewModel.logout(lastLoggedInCustomerId);
            lastLoggedInCustomerId = 0;
        }

    }

    public void onNavigationHeaderClicked(Boolean isLoggedIn){
        if (!isLoggedIn) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        } else {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
        }
    }

}
