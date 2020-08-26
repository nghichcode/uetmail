package com.nc.uetmail;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.nc.uetmail.adapters.MailRAdapter;
import com.nc.uetmail.models.Product;
import com.nc.uetmail.adapters.ListMailAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ArrayList<Product> listMail;
    ListMailAdapter listMailAdapter;
    // RC - V
    MailRAdapter rvMailAdapter;
    private RecyclerView rvMail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
            this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

//        LVIEW
        ListView listMailView = findViewById(R.id.list_mail);
        listMail = new ArrayList<>();
        listMail.add(new Product(1, "Iphone 6", 500));
        listMail.add(new Product(2, "Iphone 7", 700));
        listMail.add(new Product(3, "Sony Abc", 800));
        listMail.add(new Product(4, "Samsung XYZ", 900));
        listMail.add(new Product(5, "SP 5", 500));
        listMail.add(new Product(6, "SP 6", 700));
        listMail.add(new Product(7, "SP 7", 800));
        listMail.add(new Product(8, "SP 8", 900));
        for (int i=9; i<22; i++) {
            listMail.add(new Product(i, "SP "+i, (i+9)*100));
        }
        listMailAdapter = new ListMailAdapter(listMail);

        listMailView.setAdapter(listMailAdapter);
//        RVIEW
        rvMail = (RecyclerView)findViewById(R.id.rlist_mail);
        rvMail.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView view, @NonNull MotionEvent e) {
//                View childView = view.findChildViewUnder(e.getX(), e.getY());
//                if (childView != null && mListener != null && mGestureDetector.onTouchEvent(e)) {
//                    mListener.onItemClick(childView, view.getChildAdapterPosition(childView));
//                    return true;
//                }
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean b) {

            }
        });
        rvMailAdapter = new MailRAdapter(this, listMail);
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(rvMail);
        rvMail.setAdapter(rvMailAdapter);
        LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        rvMail.setLayoutManager(manager);

        Log.i("NCVN", "ST mail");
        new SimpleMail.RetrieveFeedTask().execute("");
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder viewHolder1) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            listMail.remove(viewHolder.getAdapterPosition());
            rvMailAdapter.notifyDataSetChanged();
        }

        @Override
        public void onChildDraw(@NonNull final Canvas c, @NonNull final RecyclerView recyclerView, @NonNull final ViewHolder viewHolder, final float dX, final float dY, final int actionState, final boolean isCurrentlyActive) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
