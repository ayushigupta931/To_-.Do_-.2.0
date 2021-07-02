package com.example.todo20;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.todo20.db.AppDatabase;
import com.example.todo20.db.TokenDb;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Text;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements BottomSheet.BottomSheetInterface,TaskAdapter.adapterInterface {


    private FloatingActionButton add;
    private RecyclerView recyclerView;
    private TaskAdapter myAdapter;
    private List<TasksResponse> tasklist;
    private BottomSheet bottomSheet;
    private DrawerLayout drawerLayout;
    private AppDatabase db;
    private LinearLayout home;
    private LinearLayout logout;
    private TextView username;
    private TextView name;
    private TextView email;
    private TokenDb tokenToBeInserted;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = AppDatabase.getDatabase(this.getApplicationContext());


            tokenToBeInserted = new TokenDb();
            tokenToBeInserted.token = Login.tokenn;
            db.tokenDao().insertToken(tokenToBeInserted);



        if(Login.tokenn == null){
            List<TokenDb> tokenInDb = db.tokenDao().getAllTokens();
            Login.tokenn = (tokenInDb.get(0)).token;
        }


        drawerLayout = findViewById(R.id.drawerLayout);

        home = (LinearLayout) findViewById(R.id.HOME);
        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //click home
                closeDrawer();
            }
        });


        logout = (LinearLayout) findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logout(MainActivity.this);
            }
        });

        add = (FloatingActionButton) findViewById(R.id.addTask);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               bottomSheet = new BottomSheet(MainActivity.this);
               bottomSheet.show(getSupportFragmentManager(),"TAG");

            }
        });

        username = (TextView) findViewById(R.id.username_profile);
        name = (TextView) findViewById(R.id.name_profile);
        email = (TextView) findViewById(R.id.email_profile);

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<UserProfile> call = apiInterface.getUserProfile(Login.tokenn);
        call.enqueue(new Callback<UserProfile>() {
            @Override
            public void onResponse(Call<UserProfile> call, Response<UserProfile> response) {
                if(response.isSuccessful()){

                    UserProfile userProfile = response.body();
                    username.setText(userProfile.getUsername());
                    name.setText(userProfile.getName());
                    email.setText(userProfile.getEmail());


                }
                else{
                    Toast.makeText( MainActivity.this,"Failed to load profile!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UserProfile> call, Throwable t) {
                Toast.makeText(MainActivity.this,"Throwable" + t.getLocalizedMessage() ,Toast.LENGTH_SHORT).show();

            }
        });



        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull @NotNull View drawerView, float slideOffset) {

            }

            @Override
            public void onDrawerOpened(@NonNull @NotNull View drawerView) {

                getSupportActionBar().hide();
            }

            @Override
            public void onDrawerClosed(@NonNull @NotNull View drawerView) {
                getSupportActionBar().show();
            }

            @Override
            public void onDrawerStateChanged(int newState) {

            }
        });


        initRecyclerView();
        loadTaskList();

    }




    public boolean isDrawerOpen(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START))
            return true;
        else 
            return false;
    }
    
    
    public void Logout(Activity activity){

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        builder.setTitle(R.string.logout);

        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                List<TokenDb> tokenInDb = db.tokenDao().getAllTokens();

                for(TokenDb t : tokenInDb) {
                    db.tokenDao().deleteToken(t);
                }

                tokenInDb.clear();

                activity.finishAffinity();

                Intent i = new Intent(activity,Login.class);
                startActivity(i);

            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();

    }



    public void initRecyclerView(){

        recyclerView = (RecyclerView) findViewById(R.id.recycleView);
        myAdapter = new com.example.todo20.TaskAdapter(this);
        recyclerView.setAdapter(myAdapter);

        LinearLayoutManager linearLayout = new LinearLayoutManager(this);
        linearLayout.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(linearLayout);
    }

    public void loadTaskList() {

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        Call<List<TasksResponse>> call = apiInterface.getAllTasks(Login.tokenn);
        call.enqueue(new Callback<List<TasksResponse>>() {
            @Override
            public void onResponse(Call<List<TasksResponse>> call, Response<List<TasksResponse>> response) {

                if(response.isSuccessful()){

                    tasklist = response.body();
                    Collections.reverse(tasklist);
                    myAdapter.setTasksList(tasklist);

                }
                else{
                    Toast.makeText(MainActivity.this,"Failed to load tasks!",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TasksResponse>> call, Throwable t) {

                Toast.makeText(MainActivity.this,"Throwable" + t.getLocalizedMessage() ,Toast.LENGTH_SHORT).show();

            }
        });

    }


//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        closeDrawer();
//    }

    public void closeDrawer(){

        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        Menu optionsMenu = menu;

        MenuItem item2 = optionsMenu.findItem(R.id.profile);
        item2.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                drawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        });


        MenuItem item1 = optionsMenu.findItem(R.id.search);
        item1.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SearchView searchView = (SearchView) item1.getActionView();
                searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String query) {
                        return false;
                    }

                    @Override
                    public boolean onQueryTextChange(String newText) {
                        myAdapter.getFilter().filter(newText);
                        return false;
                    }
                });
                return true;
            }
        });




        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void OnButtonClicked() {

        loadTaskList();
    }
}