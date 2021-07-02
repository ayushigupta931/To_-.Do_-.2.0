package com.example.todo20;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BottomSheet extends BottomSheetDialogFragment {

    private EditText newTask;
    private Button saveTask;
    private Context mcontext;
    private TasksResponse task1;
    private Integer mTaskId = null;
    private BottomSheetInterface listener;



    public BottomSheet(Context context) {
        this.mcontext = context;
    }

    public BottomSheet(Context context,int taskId ){
        this.mcontext = context;
        this.mTaskId = taskId;
    }

    @Nullable
    @org.jetbrains.annotations.Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_new_task,container,false);

        newTask = (EditText) view.findViewById(R.id.newTask);
        saveTask = (Button)  view.findViewById(R.id.saveTask);
        saveTask.setEnabled(false);
        saveTask.setTextColor(Color.GRAY);

        newTask.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (newTask.getText().length() == 0) {
                    saveTask.setEnabled(false);
                    saveTask.setTextColor(Color.GRAY);
                }
                else {
                    saveTask.setEnabled(true);
                    saveTask.setTextColor(getResources().getColor(R.color.background));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }


        });

        if(mTaskId != null){
            ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
            Call<TasksResponse> call = apiInterface.getTaskById(Login.tokenn, mTaskId);
            call.enqueue(new Callback<TasksResponse>() {
                @Override
                public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {

                    if (response.isSuccessful()) {
                        task1 = response.body();
                        newTask.setText(task1.getTitle());
                        saveTask.setEnabled(true);
                        saveTask.setTextColor(getResources().getColor(R.color.background));
                    } else {
                        Toast.makeText(mcontext, "Failure", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<TasksResponse> call, Throwable t) {

                    Toast.makeText(mcontext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        }

        saveTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveTask();
            }
        });

        return view;
    }

    public void SaveTask(){

        String s = newTask.getText().toString();

        ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
        TaskRequest task = new TaskRequest();
        task.setTitle(s);

        if (mTaskId== null) {

            Call<ResponseBody> call = apiInterface.createTask(Login.tokenn, task);
            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful()) {

                        listener.OnButtonClicked();
                        dismiss();
                    } else
                        Toast.makeText(mcontext, "Failed to create task!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    Toast.makeText(mcontext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }



        else {
            Call<TasksResponse> call = apiInterface.patchTask(Login.tokenn, task,mTaskId);
            call.enqueue(new Callback<TasksResponse>() {
                @Override
                public void onResponse(Call<TasksResponse> call, Response<TasksResponse> response) {
                    if (response.isSuccessful()) {

                        listener.OnButtonClicked();
                        dismiss();
                    } else
                        Toast.makeText(mcontext, "Failed to update task!", Toast.LENGTH_SHORT).show();

                }

                @Override
                public void onFailure(Call<TasksResponse> call, Throwable t) {
                    Toast.makeText(mcontext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

    }

    public interface BottomSheetInterface{

        void OnButtonClicked();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);

        try {
            listener = (BottomSheetInterface) context;
        }

        catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement BottomSheetInterface");
        }
    }
}
