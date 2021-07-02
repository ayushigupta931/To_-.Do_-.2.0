package com.example.todo20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> implements Filterable {

    private Context mContext;
    private List<TasksResponse> tasksList;
    private Boolean isEnable = false;
    private ActionMode Mode;
    private TasksResponse selectedTask;
    private List<TasksResponse> taskListAll;
    private adapterInterface aInterface;

    public TaskAdapter(Context context) {
        this.mContext = context;

    }

    public void setTasksList(List<TasksResponse> tasksList) {
        this.tasksList = new ArrayList<>(tasksList);
        this.taskListAll = new ArrayList<>(tasksList);

        notifyDataSetChanged();
    }


    @Override
    public Filter getFilter() {
        return filter;
    }

    Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {


            List<TasksResponse> filteredTasks = new ArrayList<>();


            if (constraint.toString().isEmpty()) {
                filteredTasks.addAll(taskListAll);
            } else {

                for (TasksResponse t : taskListAll) {
                    if ((t.getTitle()).toLowerCase().contains(constraint.toString().toLowerCase())) {
                        filteredTasks.add(t);
                    }
                }
            }

            FilterResults Results = new FilterResults();
            Results.values = filteredTasks;
            return Results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            tasksList.clear();
            tasksList.addAll((Collection<? extends TasksResponse>) results.values);
            notifyDataSetChanged();
        }
    };

    public void ClickItem(TaskAdapter.TaskViewHolder holder, ActionMode mode) {

        selectedTask = tasksList.get(holder.getAdapterPosition());

        if (holder.selected.getVisibility() == View.GONE) {
            holder.selected.setVisibility(View.VISIBLE);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.background));
            holder.cardView.setContentPadding(5, 5, 5, 5);

        } else {
            holder.selected.setVisibility(View.GONE);
            holder.cardView.setContentPadding(0, 0, 0, 0);
            holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.fields));
            Mode.finish();


        }


    }


    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item, parent, false);
        TaskViewHolder ViewHolder = new TaskViewHolder(view);
        return ViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull TaskAdapter.TaskViewHolder holder, int position) {

        if (tasksList != null) {

            TasksResponse task3 = this.tasksList.get(position);
            holder.taskField.setText(task3.getTitle());


            holder.taskField.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!(((adapterInterface)mContext).isDrawerOpen())){

                        if (!isEnable) {
                            ActionMode.Callback callback = new ActionMode.Callback() {
                                @Override
                                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                    MenuInflater menuInflater = mode.getMenuInflater();
                                    menuInflater.inflate(R.menu.menu, menu);
                                    return true;
                                }

                                @Override
                                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                    isEnable = true;
                                    ClickItem(holder, mode);
                                    Mode = mode;

                                    return true;
                                }

                                @Override
                                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                    int id = item.getItemId();


                                    AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                    alertBox.setTitle("Delete Task");
                                    alertBox.setMessage("Are you sure you want to delete this task?");
                                    alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            int taskId = selectedTask.getId();
                                            ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                                            Call<ResponseBody> call = apiInterface.deleteTask(Login.tokenn, taskId);
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        tasksList.remove(selectedTask);
                                                        taskListAll.remove(selectedTask);
                                                        notifyItemRemoved(position);
                                                        mode.finish();
                                                    } else
                                                        Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(mContext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });

                                        }
                                    });
                                    alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = alertBox.create();
                                    dialog.show();

                                    return true;
                                }

                                @Override
                                public void onDestroyActionMode(ActionMode mode) {
                                    isEnable = false;
                                    selectedTask = null;
                                    holder.selected.setVisibility(View.GONE);
                                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.fields));
                                    holder.cardView.setContentPadding(0, 0, 0, 0);
                                    notifyDataSetChanged();
                                }
                            };
                            ((AppCompatActivity) v.getContext()).startActionMode(callback);
                        }
                }
                    return true;
                }
            });


            holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!(((adapterInterface)mContext).isDrawerOpen())){
                        if (!isEnable) {
                            ActionMode.Callback callback = new ActionMode.Callback() {
                                @Override
                                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                    MenuInflater menuInflater = mode.getMenuInflater();
                                    menuInflater.inflate(R.menu.menu, menu);
                                    return true;
                                }

                                @Override
                                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                    isEnable = true;
                                    ClickItem(holder, mode);
                                    Mode = mode;

                                    return true;
                                }

                                @Override
                                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                    int id = item.getItemId();


                                    AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                    alertBox.setTitle("Delete Task");
                                    alertBox.setMessage("Are you sure you want to delete this task?");
                                    alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int taskId = selectedTask.getId();
                                            ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                                            Call<ResponseBody> call = apiInterface.deleteTask(Login.tokenn, taskId);
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        tasksList.remove(selectedTask);
                                                        taskListAll.remove(selectedTask);
                                                        notifyItemRemoved(position);
                                                        mode.finish();
                                                    } else
                                                        Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(mContext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });
                                        }
                                    });
                                    alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = alertBox.create();
                                    dialog.show();


                                    return true;
                                }

                                @Override
                                public void onDestroyActionMode(ActionMode mode) {
                                    isEnable = false;
                                    selectedTask = null;
                                    holder.selected.setVisibility(View.GONE);
                                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.fields));
                                    holder.cardView.setContentPadding(0, 0, 0, 0);
                                    notifyDataSetChanged();
                                }
                            };
                            ((AppCompatActivity) v.getContext()).startActionMode(callback);
                        }
                }
                    return true;
                }
            });


            holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    if (!(((adapterInterface)mContext).isDrawerOpen())){
                        if (!isEnable) {
                            ActionMode.Callback callback = new ActionMode.Callback() {
                                @Override
                                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                    MenuInflater menuInflater = mode.getMenuInflater();
                                    menuInflater.inflate(R.menu.menu, menu);
                                    return true;
                                }

                                @Override
                                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {

                                    isEnable = true;
                                    ClickItem(holder, mode);
                                    Mode = mode;

                                    return true;
                                }

                                @Override
                                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                    int id = item.getItemId();


                                    AlertDialog.Builder alertBox = new AlertDialog.Builder(mContext);
                                    alertBox.setTitle("Delete Task");
                                    alertBox.setMessage("Are you sure you want to delete this task?");
                                    alertBox.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                            int taskId = selectedTask.getId();
                                            ApiInterface apiInterface = ApiClient.getRetrofit().create(ApiInterface.class);
                                            Call<ResponseBody> call = apiInterface.deleteTask(Login.tokenn, taskId);
                                            call.enqueue(new Callback<ResponseBody>() {
                                                @Override
                                                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                                    if (response.isSuccessful()) {
                                                        tasksList.remove(selectedTask);
                                                        taskListAll.remove(selectedTask);
                                                        notifyItemRemoved(position);
                                                        mode.finish();
                                                    } else
                                                        Toast.makeText(mContext, "Failure", Toast.LENGTH_SHORT).show();
                                                }

                                                @Override
                                                public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                    Toast.makeText(mContext, "Throwable" + t.getLocalizedMessage(), Toast.LENGTH_SHORT).show();

                                                }
                                            });


                                        }
                                    });
                                    alertBox.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    AlertDialog dialog = alertBox.create();
                                    dialog.show();


                                    return true;
                                }

                                @Override
                                public void onDestroyActionMode(ActionMode mode) {
                                    isEnable = false;
                                    selectedTask = null;
                                    holder.selected.setVisibility(View.GONE);
                                    holder.cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.fields));
                                    holder.cardView.setContentPadding(0, 0, 0, 0);
                                    notifyDataSetChanged();
                                }
                            };
                            ((AppCompatActivity) v.getContext()).startActionMode(callback);
                        }
                }
                    return true;
                }
            });


            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!(((adapterInterface)mContext).isDrawerOpen())){


                        if (isEnable) {

                            if (task3 == selectedTask)
                                ClickItem(holder, Mode);
                            else
                                Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                        } else {
                            BottomSheet bottomSheet = new BottomSheet(mContext, task3.getId());
                            bottomSheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "TAG");
                        }

                }
                }
            });

            holder.taskField.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(((adapterInterface)mContext).isDrawerOpen())){
                    if (isEnable) {

                        if (task3 == selectedTask)
                            ClickItem(holder, Mode);
                        else
                            Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                    } else {
                        BottomSheet bottomSheet = new BottomSheet(mContext,task3.getId());
                        bottomSheet.show(((AppCompatActivity)mContext).getSupportFragmentManager(),"TAG");
                    }}
                }
            });

            holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!(((adapterInterface)mContext).isDrawerOpen())) {
                        if (isEnable) {

                            if (task3 == selectedTask)
                                ClickItem(holder, Mode);
                            else
                                Toast.makeText(mContext, "Only one task can be selected at a time", Toast.LENGTH_SHORT).show();
                        } else {
                            BottomSheet bottomSheet = new BottomSheet(mContext, task3.getId());
                            bottomSheet.show(((AppCompatActivity) mContext).getSupportFragmentManager(), "TAG");
                        }
                    }
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (tasksList != null)
            return this.tasksList.size();
        else
            return 0;
    }

    public class TaskViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout relativeLayout;
        TextView taskField;
        CheckBox checkBox;
        //        TextView date;
        CardView cardView;
        ImageView selected;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            cardView = itemView.findViewById(R.id.cardView);
            selected = itemView.findViewById(R.id.selected);
//            date = itemView.findViewById(R.id.date);
            taskField = itemView.findViewById(R.id.task);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);

        }
    }

    public interface adapterInterface{

        boolean isDrawerOpen();
    }


}


