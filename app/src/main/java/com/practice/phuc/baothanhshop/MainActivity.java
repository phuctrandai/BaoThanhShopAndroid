package com.practice.phuc.baothanhshop;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BaseTransientBottomBar;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.practice.phuc.baothanhshop.Adapters.ProductAdapter;
import com.practice.phuc.baothanhshop.Models.VProduct;
import com.practice.phuc.baothanhshop.Utils.OkHttpUtil;
import com.squareup.moshi.Json;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static int REQUEST_CODE_SCAN_BARCODE = 0;
    public static String EXTRA_KEY_BARCODE = "extra_key_barcode";

    private LoadProductsTask mLoadProductsTask;
    private ProductAdapter mProductAdapter;
    private List<VProduct> mProducts;
    private String mBarcode;

    private DrawerLayout mDrawerLayoutMain;
    private ProgressBar mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        mProgressBar = findViewById(R.id.pb_loading_product);

        setUpScanBarcodeButton();
        setUpDrawerLayout(toolbar);
        setUpLogoMain();
        setUpRecyclerView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLoadProductsTask = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SCAN_BARCODE) {
            if (resultCode == Activity.RESULT_OK) {
                mBarcode = Objects.requireNonNull(data).getStringExtra(EXTRA_KEY_BARCODE);
                attempLoadProducts(mBarcode);

                //Toast.makeText(MainActivity.this, barcode, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setUpDrawerLayout(Toolbar toolbar) {
        mDrawerLayoutMain = findViewById(R.id.drawer_layout_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayoutMain, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayoutMain.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view_main);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);
    }

    private void setUpScanBarcodeButton() {
        FloatingActionButton mFabScanBarcode = findViewById(R.id.fab_scan_barcode);
        mFabScanBarcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanBarcode();
//                mLoadProductsTask = new LoadProductsTask();
//                mLoadProductsTask.execute((String) null);
            }
        });
    }

    private void setUpLogoMain() {
        NavigationView navigationView = findViewById(R.id.nav_view_main);
        View headerView = navigationView.getHeaderView(0);
        ImageView mIvLogoMain = headerView.findViewById(R.id.iv_logo_main);
        mIvLogoMain.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mDrawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
                    mDrawerLayoutMain.closeDrawer(GravityCompat.START);
                }
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(MainActivity.this, ServerConfigActivity.class);
                        startActivity(intent);
                    }
                }, 500);
                return true;
            }
        });
    }

    private void scanBarcode() {
        Intent scanIntent = new Intent(this, ScanActivity.class);
        startActivityForResult(scanIntent, REQUEST_CODE_SCAN_BARCODE);
    }

    private void setUpRecyclerView() {
        mProducts = new ArrayList<>();
        mProductAdapter = new ProductAdapter(this, mProducts);
        RecyclerView rv = findViewById(R.id.rv_product_main);
        rv.setLayoutManager(new LinearLayoutManager(this));
        rv.setHasFixedSize(true);
        rv.setAdapter(mProductAdapter);
    }

    private void attempLoadProducts(final String barcode) {
        if (OkHttpUtil.getConnectivityStatus(this) == OkHttpUtil.TYPE_NOT_CONNECTED) {
            Snackbar.make(mDrawerLayoutMain, "Không có kết nối Internet", Snackbar.LENGTH_INDEFINITE)
                    .setAction("Thử lại", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            attempLoadProducts(mBarcode);
                        }
                    }).show();

        } else {
            mLoadProductsTask = new LoadProductsTask();
            mLoadProductsTask.execute(barcode);
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class LoadProductsTask extends AsyncTask<String, Void, Boolean> {
        Response mResponse;
        String serverPort, serverAddress, jsonBody, errorMessage;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            SharedPreferences sp = MainActivity.this.getSharedPreferences(OkHttpUtil.SHARED_PRE_SERVER, MODE_PRIVATE);
            serverPort = sp.getString(OkHttpUtil.SHARED_PRE_KEY_SERVER_PORT,/*default:*/ OkHttpUtil.DEFAULT_SERVER_PORT);
            serverAddress = sp.getString(OkHttpUtil.SHARED_PRE_KEY_SERVER_ADDRESS,/*default:*/ OkHttpUtil.DEFAILT_SERVER_ADDRESS);
            mProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected Boolean doInBackground(String... strings) {
            if (mLoadProductsTask == null || strings == null) return false;

            String url = OkHttpUtil.getLoadProductApiUrl(serverAddress, serverPort, strings[0]);
            mResponse = OkHttpUtil.makeRequest(url, false, null);
            Log.d("DEBUG", url);
            Log.d("DEBUG", "Response " + mResponse);

            if (mResponse != null) {
                try {
                    if (mResponse.code() == OkHttpUtil.OK) {
                        jsonBody = mResponse.body() != null ? mResponse.body().string() : "";
                        return true;

                    } else if (mResponse.code() == OkHttpUtil.BAD_REQUEST) {
                        if (mResponse.body() != null) {
                            String responseBody = mResponse.body().string();
                            errorMessage = responseBody.substring(12, responseBody.lastIndexOf("\"}"));

                        } else {
                            errorMessage = "Không tim thấy máy chủ";
                        }
                        return false;

                    } else if (mResponse.code() == OkHttpUtil.NOT_FOUND) {
                        errorMessage = "Không tìm thấy máy chủ";
                        return false;

                    } else {
                        errorMessage = "Có lỗi khi kết nối đến máy chủ";
                        return false;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    errorMessage = "Có lỗi xảy ra bất ngờ";
                    return false;
                }
            } else {
                errorMessage = "Máy chủ không phản hồi";
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);
            mProgressBar.setVisibility(View.GONE);

            if (success) {
                mProgressBar.setVisibility(View.GONE);
                Moshi moshi = new Moshi.Builder().build();
                Type usersType = Types.newParameterizedType(List.class, VProduct.class);
                JsonAdapter<List<VProduct>> jsonAdapter = moshi.adapter(usersType);

                try {
                    List<VProduct> products = jsonAdapter.fromJson(jsonBody);
                    if (products.size() > 0) {
                        mProducts.clear();
                        mProducts.addAll(products);
                        mProductAdapter.notifyDataSetChanged();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Snackbar.make(mDrawerLayoutMain, errorMessage, 5000)
                        .show();
            }
        }

        @Override
        protected void onCancelled() {
            mLoadProductsTask = null;
            super.onCancelled();
        }
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayoutMain.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayoutMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_about) {
            Toast.makeText(this, "About Bao Thanh Shop !!!", Toast.LENGTH_LONG).show();
        }

        mDrawerLayoutMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
