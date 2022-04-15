package com.example.vis.ui.classroomTeacher;

import android.animation.LayoutTransition;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vis.OnFinishListener2;
import com.example.vis.R;
import com.example.vis.TeacherAdapter;
import com.example.vis.TeacherMaterials;
import com.example.vis.databinding.FragmentTeacherclassroomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClassRoomTeacherFragment extends Fragment implements OnFinishListener2 {

    private FragmentTeacherclassroomBinding binding;
    RecyclerView recyclerView;
    List<TeacherMaterials> materialsList = new ArrayList<>();
    TeacherAdapter adapter;
    LinearLayout layout;
    JSONArray array;
    JSONObject user;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentTeacherclassroomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        layout = binding.layout2;
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        recyclerView = binding.recyclerView2;
        Bundle extras = getActivity().getIntent().getExtras();
        String value = extras.getString("key");
        try {
            user = new JSONObject(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Connection2(this).execute();
        adapter = new TeacherAdapter(materialsList);
        recyclerView.setAdapter(adapter);
        binding.addClassroom.setOnClickListener(newClasswoom ->{
            if (binding.nameClassroom.getText().toString().isEmpty() || binding.lectureName.getText().toString().isEmpty()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog55), Toast.LENGTH_LONG).show();
            }
            else{
                new Connection(this).execute();
            }

        });
        binding.cardView.setOnClickListener(expand ->{
            int v = (binding.nameClassroom.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

            TransitionManager.beginDelayedTransition(layout, new AutoTransition());
            binding.nameClassroom.setVisibility(v);
            binding.lectureName.setVisibility(v);
            binding.addClassroom.setVisibility(v);
        });

        binding.addStudent.setOnClickListener(addStudent ->{
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();
        });

        return root;
    }


    private void initData() {
        for(int i = 0; i < array.length(); i++){
            try {
                materialsList.add(new TeacherMaterials(((JSONObject) array.get(i)).getString("name"), user.getString("first_name") + " " + user.getString("last_name"), ((JSONObject) array.get(i)).getString("lecture_name"), "HI"));
                adapter.notifyItemInserted(materialsList.size()-1);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    public class Connection extends AsyncTask<Void, Void, Void> {

        private OnFinishListener2 listener;

        public Connection(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                try {
                    user = new JSONObject(value);
                    RequestBody formBody = new FormBody.Builder()
                            .add("user_name", user.getString("user_name"))
                            .add("lecture_name", binding.lectureName.getText().toString())
                            .add("name", binding.nameClassroom.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/create_classroom/")
                            .post(formBody)
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        listener.onSuccess();
                    }
                    else if (Integer.toString(response.code()).equals("404")){
                        listener.onFailed();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }


    public class Connection2 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener2 listener;

        public Connection2(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();
            Bundle extras = getActivity().getIntent().getExtras();
            if (extras != null) {
                String value = extras.getString("key");
                JSONObject user;
                try {
                    user = new JSONObject(value);

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/return_all_classrooms/")
                            .header("id",user.getString("id"))
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        final String user_info = response.body().string();
                        JSONObject user2 = new JSONObject(user_info);
                        array = user2.getJSONArray("classrooms");
                        listener.onSuccess2();
                    }
                    else if (Integer.toString(response.code()).equals("404")){
                        listener.onFailed2();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

    }


    public class Connection3 extends AsyncTask<Integer, Void, Void> {

        private OnFinishListener2 listener;

        public Connection3(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Integer... help) {
            OkHttpClient client = new OkHttpClient();
                try {
                    int help2 = help[0];
                    String value = ((JSONObject) array.get(help2)).getString("name");
                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/users/"+value)
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        System.out.println(response.body().string());
                    }
                    else if (Integer.toString(response.code()).equals("404")){
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            return null;
        }

    }


    public class Connection4 extends AsyncTask<Integer, Void, Void> {

        private OnFinishListener2 listener;

        public Connection4(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Integer... help) {
            OkHttpClient client = new OkHttpClient();
            try {
                int help2 = help[0];
                String value = ((JSONObject) array.get(help2)).getString("name");
                Request request = new Request.Builder()
                        .url("http://192.168.137.1:8000/vis/add_user/"+value+"/")
                        .build();

                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("200")){
                    System.out.println(response.body().string());
                }
                else if (Integer.toString(response.code()).equals("404")){
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSuccess() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog56), Toast.LENGTH_LONG).show();
            binding.lectureName.setText("");
            binding.nameClassroom.setText("");
            getActivity().recreate();
        });
    }

    @Override
    public void onFailed() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog57), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSuccess2() {
        getActivity().runOnUiThread(() -> {
            initData();
        });
    }

    @Override
    public void onFailed2() {

    }
}