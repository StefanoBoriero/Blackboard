package dima.it.polimi.blackboard.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import dima.it.polimi.blackboard.R;
import dima.it.polimi.blackboard.adapters.DayResumeAdapter;
import dima.it.polimi.blackboard.adapters.FirestoreAdapter;
import dima.it.polimi.blackboard.model.DayResume;
import dima.it.polimi.blackboard.model.User;
import dima.it.polimi.blackboard.utils.DataGeneratorUtil;
import dima.it.polimi.blackboard.utils.GlideApp;
import dima.it.polimi.blackboard.utils.HouseDecoder;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FirestoreAdapter.OnCompleteListener {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private NavigationView navigationView;
    private View navHeaderView;
    private ImageView ivProfile;
    private FirebaseFirestore db;
    private DayResumeAdapter adapter;
    private boolean graphInstantiated;
    private String[] datesCreated;
    private String[] datesCompleted;

    @Override
    @SuppressWarnings("unchecked")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navHeaderView = navigationView.getHeaderView(0);
        navigationView.setNavigationItemSelectedListener(this);

        recyclerView = findViewById(R.id.dashboard);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        ivProfile = navHeaderView.findViewById(R.id.user_icon);

        Menu menuNav= navigationView.getMenu();
        MenuItem nav_balance = menuNav.findItem(R.id.nav_balance);
        MenuItem nav_group_list = menuNav.findItem(R.id.nav_group_list);
        MenuItem nav_my_list = menuNav.findItem(R.id.nav_my_list);

        nav_balance.setEnabled(false);
        nav_group_list.setEnabled(false);
        nav_my_list.setEnabled(false);



        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null) {
            db.collection("users").document(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        List<String> houses = (List<String>) task.getResult().getData().get("houses");
                        if (houses != null && houses.size() > 0) {
                            nav_balance.setEnabled(true);
                            nav_group_list.setEnabled(true);
                            nav_my_list.setEnabled(true);
                            addHouseListener();
                        }
                    }
                }

            });
        }



        initializeUser();
        loadProfilePicture();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            moveTaskToBack(true);
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
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_my_list) {
            Intent intent = new Intent(MainActivity.this, MyListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_group_list) {
            Intent intent = new Intent(MainActivity.this, HouseListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_balance) {
            Intent intent = new Intent(MainActivity.this, BalanceActivity.class);
            startActivity(intent);
        } else if(id == R.id.nav_profile){
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivityForResult(intent,2);
        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        } else if (id == R.id.nav_settings){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }
        else if (id == R.id.nav_logout){
            finish();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            firebaseAuth.signOut();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void initializeUser(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if(user != null) {
            final String mail = user.getEmail();
            final TextView mailView = navHeaderView.findViewById(R.id.email);
            final TextView nameView = navHeaderView.findViewById(R.id.name);
            DocumentReference userRef = db.collection("users").document(user.getUid());
            userRef.get().addOnCompleteListener(task -> {
                User u = task.getResult().toObject(User.class);
                User.setInstance(u);
                String name = (String)User.getInstance().getPersonal_info().get("name");
                String surname =  (String)User.getInstance().getPersonal_info().get("surname");
                String completeName = name + " " + surname;
                mailView.setText(mail);
                nameView.setText(completeName);

                HouseDecoder.getInstance().populateFromUser(User.getInstance());
                //TODO get also the profile picture
                initializeDays();
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);


        if(requestCode == 2 && resultCode == 3) {

            loadProfilePicture();

        }
    }

    private void initializeDays(){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            String id = user.getUid();
            CollectionReference days = db.collection("users").document(id).collection("days");
            Query query = days.orderBy("day", Query.Direction.DESCENDING).limit(7);

            adapter = new DayResumeAdapter(query, this);
            recyclerView.setAdapter(adapter);
            adapter.startListening();
        }
    }

    @Override
    public void onComplete(boolean emptyResult) {
        if(!emptyResult){
            recyclerView.setVisibility(View.VISIBLE);
            if(!graphInstantiated) {
                graphInstantiated = true;
                LineChart completeChart = findViewById(R.id.chart_completed);
                LineChart createdChart = findViewById(R.id.chart_created);
                if (createdChart == null) {
                    //Small device
                    populateCompletedChart(completeChart);
                } else {
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                    }
                    populateCompletedChart(completeChart);
                    populateCreatedChart(createdChart);
                }
            }
            else{
                LineChart completeChart = findViewById(R.id.chart_completed);
                LineChart createdChart = findViewById(R.id.chart_created);
                if (createdChart == null) {
                    //Small device
                    //populateCompletedChart(completeChart);
                    completeChart.setVisibility(View.INVISIBLE);
                    LineDataSet dataSet = generateCompletedDataSet();
                    styleDataSet(dataSet, getDrawable(R.drawable.fade_green), getResources().getColor(R.color.greenAccept));
                    LineData lineData = new LineData(dataSet);
                    setXLabels(completeChart, datesCompleted);
                    completeChart.setData(lineData);
                    completeChart.notifyDataSetChanged();
                    completeChart.invalidate();
                    completeChart.setVisibility(View.VISIBLE);
                } else {
                    if (recyclerView != null) {
                        recyclerView.scrollToPosition(0);
                    }
                    //populateCompletedChart(completeChart);
                    findViewById(R.id.charts).setVisibility(View.INVISIBLE);
                    completeChart.getXAxis().setDrawLabels(false);
                    LineDataSet dataSet = generateCompletedDataSet();
                    styleDataSet(dataSet, getDrawable(R.drawable.fade_green), getResources().getColor(R.color.greenAccept));
                    LineData lineData = new LineData(dataSet);
                    setXLabels(completeChart, datesCompleted);
                    completeChart.setData(lineData);
                    completeChart.notifyDataSetChanged();
                    completeChart.invalidate();
                    completeChart.getXAxis().setDrawLabels(true);


                    //populateCreatedChart(createdChart);
                    createdChart.getXAxis().setDrawLabels(false);
                    LineDataSet dataSetCr = generateCreatedDataSet();
                    styleDataSet(dataSetCr, getDrawable(R.drawable.fade_accent), getResources().getColor(R.color.colorAccent));
                    LineData lineDataCr = new LineData(dataSetCr);
                    setXLabels(createdChart, datesCreated);
                    createdChart.setData(lineDataCr);
                    createdChart.notifyDataSetChanged();
                    createdChart.invalidate();
                    createdChart.getXAxis().setDrawLabels(true);
                    findViewById(R.id.charts).setVisibility(View.VISIBLE);
                }
            }
        }
        else{
            adapter.goBackOneWeek();
            Toast.makeText(this, "There's no more data to load!", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    private List<Entry> createNewEntries(){
        DayResume[] mData = adapter.getDayResumeArray();
        List<Entry> entries = new ArrayList<>();
        int i = mData.length;
        int j;
        datesCompleted = new String[i];
        for(i--, j=0;i>=0; i--, j++){
            DayResume day = mData[i];
            DateFormat df = new SimpleDateFormat("MMM dd", Locale.US);
            String date = df.format(day.getDay());
            datesCompleted[j] = date;
            entries.add(new Entry(j, day.getCompletedItems()));
        }
        return entries;
    }

    private LineDataSet generateCompletedDataSet(){
        DayResume[] mData = adapter.getDayResumeArray();
        List<Entry> entries = new ArrayList<>();
        int i = mData.length;
        int j;
        datesCompleted = new String[i];
        for(i--, j=0;i>=0; i--, j++){
            DayResume day = mData[i];
            DateFormat df = new SimpleDateFormat("MMM dd", Locale.US);
            String date = df.format(day.getDay());
            datesCompleted[j] = date;
            entries.add(new Entry(j, day.getCompletedItems()));
        }
        return new LineDataSet(entries, "Completed Items");
    }

    private LineDataSet generateCreatedDataSet(){
        DayResume[] mData = adapter.getDayResumeArray();

        List<Entry> entries = new ArrayList<>();
        datesCreated = new String[mData.length];
        int i = mData.length;
        int j;
        for(i--, j=0;i>=0; i--, j++){
            DayResume day = mData[i];
            DateFormat df = new SimpleDateFormat("MMM dd", Locale.US);
            String date = df.format(day.getDay());
            datesCreated[j] = date;
            entries.add(new Entry(j, day.getCreatedItems()));
        }
        return new LineDataSet(entries, "Completed Items");
    }

    private void styleChart(LineChart chart){
        chart.getXAxis().setDrawGridLines(false);
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getLegend().setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        chart.getLegend().setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        chart.setDrawGridBackground(false);

        Description mDesc = new Description();
        mDesc.setText("");
        chart.setDescription(mDesc);    // Hide the description
        chart.getAxisLeft().setDrawLabels(false);
        chart.getAxisRight().setDrawLabels(false);
        chart.getXAxis().setDrawLabels(true);
        chart.getXAxis().setLabelRotationAngle(-75);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        chart.getLegend().setEnabled(false);
    }

    private void setXLabels(LineChart chart, String[] dates){
        chart.getXAxis().setValueFormatter( (f,a) ->
            dates[(int)f]
        );
    }

    private void styleDataSet(LineDataSet dataSet, Drawable background, int lineColor){
        //dataSet = new LineDataSet(entries, "Completed Items");
        dataSet.setColor(lineColor);
        dataSet.setDrawFilled(true);
        //Drawable background = getDrawable(R.drawable.fade_green);
        dataSet.setFillDrawable(background);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        dataSet.setCubicIntensity(0.2f);
    }

    private void populateCompletedChart(LineChart chart){
        /*
        DayResume[] mData = adapter.getDayResumeArray();

        List<Entry> entries = new ArrayList<>();
        int i = mData.length;
        int j;
        String[] dates = new String[i];
        for(i--, j=0;i>=0; i--, j++){
            DayResume day = mData[i];
            DateFormat df = new SimpleDateFormat("MMM dd", Locale.US);
            String date = df.format(day.getDay());
            dates[j] = date;
            entries.add(new Entry(j, day.getCompletedItems()));
        }*/
        LineDataSet dataSet = generateCompletedDataSet();
        //LineDataSet dataSet = new LineDataSet(entries, "Completed Items");
        styleDataSet(dataSet, getDrawable(R.drawable.fade_green), getResources().getColor(R.color.greenAccept));
        LineData lineData = new LineData(dataSet);
        styleChart(chart);
        setXLabels(chart, datesCompleted);
        chart.setData(lineData);
        chart.invalidate();
    }


    private void populateCreatedChart(LineChart chart){
        LineDataSet dataSet = generateCreatedDataSet();
        styleDataSet(dataSet, getDrawable(R.drawable.fade_accent), getResources().getColor(R.color.colorAccent));
        LineData lineData = new LineData(dataSet);
        styleChart(chart);
        setXLabels(chart, datesCreated);
        chart.setData(lineData);
        chart.invalidate();
    }

    @Override
    public void deleteByOther(int position) {

    }

    @Override
    public void addedByOther(int position) {

    }

    @Override
    public void resetOnFirst(){

    }

    private void loadProfilePicture()
    {
        readSharedPreferenceForCache();
    }

    //retrieve the last update to the photo profile, so we can get the URL
    private void readSharedPreferenceForCache()
    {
        SharedPreferences sharedPref = getSharedPreferences("sharedPrefs", Context.MODE_PRIVATE);

        db.collection("users").whereEqualTo("auth_id",firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED: {
                            String imageCaching = (String) dc.getDocument().getData().get("lastEdit");
                            if (imageCaching != null) {
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("imageCaching", imageCaching);
                                editor.commit();

                                FirebaseStorage storage = FirebaseStorage.getInstance();
                                StorageReference reference = storage.getReference().child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString() + "/profile" + imageCaching);
                                GlideApp.with(getBaseContext())
                                        .load(reference)
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                ivProfile.setVisibility(View.VISIBLE);
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                ivProfile.setVisibility(View.VISIBLE);
                                                return false;
                                            }
                                        })
                                        .error(R.drawable.empty_profile_blue_circle)
                                        .apply(RequestOptions.circleCropTransform())
                                        .into(ivProfile);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }


    private void addHouseListener()
    {
        db.collection("users").whereEqualTo("auth_id",firebaseAuth.getCurrentUser().getUid()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots,
                                @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case MODIFIED: {
                            List<String> houses = (List<String>) dc.getDocument().getData().get("houses");
                            if (houses != null && houses.size() > 0) {
                                Menu menuNav= navigationView.getMenu();
                                MenuItem nav_balance = menuNav.findItem(R.id.nav_balance);
                                MenuItem nav_group_list = menuNav.findItem(R.id.nav_group_list);
                                MenuItem nav_my_list = menuNav.findItem(R.id.nav_my_list);

                                nav_balance.setEnabled(true);
                                nav_group_list.setEnabled(true);
                                nav_my_list.setEnabled(true);
                                break;
                            }
                        }
                    }
                }
            }
        });
    }

}
