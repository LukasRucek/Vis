package com.example.vis.ui.classroom;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vis.OnFinishListener2;
import com.example.vis.OnFinishListener3;
import com.example.vis.R;
import com.example.vis.TeacherAdapter;
import com.example.vis.TeacherMaterials;
import com.example.vis.databinding.FragmentClassroomBinding;
import com.example.vis.ui.classroomTeacher.ClassRoomTeacherFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ClassRoomFragment extends Fragment implements OnFinishListener2 {

    private FragmentClassroomBinding binding;

    List<TeacherMaterials> materialsList = new ArrayList<>();
    JSONObject user;
    JSONArray array;
    JSONObject student_one_class = new JSONObject();
    RecyclerView recyclerView;
    TeacherAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {


        binding = FragmentClassroomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        Bundle extras = getActivity().getIntent().getExtras();
        String value = extras.getString("key");
        try {
            user = new JSONObject(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Connection(this).execute();
        recyclerView = binding.recyclerViewClassroom;
        adapter = new TeacherAdapter(materialsList);
        recyclerView.setAdapter(adapter);

        return root;
    }


    private void initData() {
        String students = "";
        for(int i = 0; i < array.length(); i++){
            try {
                if(student_one_class.getString(String.valueOf(i)).equals("null")){
                    students += "Nie su pridaný žiadny študenty";
                }
                else{
                    JSONArray student = student_one_class.getJSONArray(String.valueOf(i));
                    for (int j = 0; j < student.length();j++){
                        students += ((JSONObject) student.get(j)).
                                getString("name")+", ";
                    }
                }

                materialsList.add(new TeacherMaterials(((JSONObject) array.get(i)).getString("name"), user.getString("first_name") + " " + user.getString("last_name"), ((JSONObject) array.get(i)).getString("lecture_name"),  students));
                adapter.notifyItemInserted(materialsList.size()-1);
                students = "";

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
                        for(int i = 0; i < array.length(); i++) {
                            try {

                                String value2 = ((JSONObject) array.get(i)).getString("name");
                                Request request2 = new Request.Builder()
                                        .url("http://192.168.137.1:8000/vis/users/" + value2)
                                        .build();

                                Response response2 = client.newCall(request2).execute();
                                Log.d("HTTPCALL", Integer.toString(response2.code()));
                                if (Integer.toString(response2.code()).equals("200")) {
                                    final String help2 = response2.body().string();
                                    JSONObject array2 = new JSONObject(help2);
                                    JSONArray array3 = array2.getJSONArray("users");
                                    if(array3.length() == 0){
                                        student_one_class.put(String.valueOf(i),"null");
                                    }
                                    else{
                                        student_one_class.put(String.valueOf(i),array3);
                                    }
                                }

                            } catch (IOException | JSONException e) {
                                e.printStackTrace();
                            }
                        }
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onSuccess() {
        getActivity().runOnUiThread(() -> {
            initData();
        });
    }

    @Override
    public void onFailed() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog60), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSuccess2() {

    }

    @Override
    public void onFailed2() {

    }
}