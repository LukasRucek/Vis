package com.example.vis.ui.classroom;

import android.Manifest;
import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vis.OnFinishListener2;
import com.example.vis.R;
import com.example.vis.TeacherAdapter;
import com.example.vis.TeacherMaterials;
import com.example.vis.databinding.FragmentClassroomBinding;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;


public class ClassRoomFragment extends Fragment implements OnFinishListener2, AdapterView.OnItemSelectedListener {

    private FragmentClassroomBinding binding;

    List<TeacherMaterials> materialsList = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    Map<String, JSONArray> list2 = new HashMap<String, JSONArray>();
    JSONObject user;
    JSONArray array;
    JSONArray array2;
    JSONObject student_one_class = new JSONObject();
    RecyclerView recyclerView;
    Spinner classroomSpinner;
    Spinner materialSpinner;
    TeacherAdapter adapter;
    private ArrayAdapter help_adapter;
    private ArrayAdapter help_adapter2;
    String spinnerItem2;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        binding = FragmentClassroomBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        binding.loading.setVisibility(View.VISIBLE);
        Bundle extras = getActivity().getIntent().getExtras();
        String value = extras.getString("key");
        binding.layout2.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        try {
            user = new JSONObject(value);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        new Connection(this).execute();
        recyclerView = binding.recyclerViewClassroom;
        adapter = new TeacherAdapter(materialsList);
        recyclerView.setAdapter(adapter);



        binding.cardView.setOnClickListener(expand ->{
            classroomSpinner = binding.classroomname;
            materialSpinner = binding.materialName;
            help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list);
            help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            binding.classroomname.setAdapter(help_adapter);
            binding.classroomname.setOnItemSelectedListener(this);
            spinnerItem2 = classroomSpinner.getSelectedItem().toString();
            int v = (binding.classroomname.getVisibility() == View.GONE)? View.VISIBLE: View.GONE;

            TransitionManager.beginDelayedTransition(binding.layout2, new AutoTransition());
            binding.classroomname.setVisibility(v);
            binding.materialName.setVisibility(v);
            binding.downloadMaterial.setVisibility(v);
        });

        binding.downloadMaterial.setOnClickListener(download->{
            new Connection2(this).execute();
        });


        return root;
    }



    private void initData() {
        String students = "";
        String materials = "";
        for(int i = 0; i < array.length(); i++){
            try {
                if(student_one_class.getString(String.valueOf(i)).equals("null")){
                    students += getString(R.string.login_dialog76);
                }
                else{
                    JSONArray student = student_one_class.getJSONArray(String.valueOf(i));
                    for (int j = 0; j < student.length();j++){
                        if(j == student.length() -1){
                            students += ((JSONObject) student.get(j)).
                                    getString("name")+".";
                        }
                        else {
                            students += ((JSONObject) student.get(j)).
                                    getString("name") + ", ";
                        }
                    }
                }


                    String nameClassroom = ((JSONObject) array.get(i)).getString("name");
                if(list2.get(nameClassroom).length() == 0){
                    materials += getString(R.string.login_dialog77);
                }
                else {
                    for (int x = 0; x < list2.get(nameClassroom).length(); x++) {

                        try {
                            if (x == list2.get(nameClassroom).length() - 1) {
                                materials += (list2.get(nameClassroom).getJSONObject(x).getString("name")) + ".";
                            } else {
                                materials += (list2.get(nameClassroom).getJSONObject(x).getString("name")) + ", ";
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }

                materialsList.add(new TeacherMaterials(getString(R.string.maretial_info1)+" "+((JSONObject) array.get(i)).getString("name"), getString(R.string.maretial_info2)+"\n"+user.getString("first_name") + " " + user.getString("last_name"), getString(R.string.maretial_info3)+"\n"+((JSONObject) array.get(i)).getString("lecture_name"),  getString(R.string.maretial_info4)+"\n"+students, getString(R.string.maretial_info5)+"\n"+materials));
                list.add(((JSONObject) array.get(i)).getString("name"));
                adapter.notifyItemInserted(materialsList.size()-1);
                students = "";
                materials = "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    String spinnerItem = "";
    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String spinnerItem2 = classroomSpinner.getSelectedItem().toString();
        if(!spinnerItem.equals(spinnerItem2) || spinnerItem.equals("")) {
            spinnerItem = spinnerItem2;
            ArrayList<String> classroom_materials = new ArrayList<>();
            for (int x = 0; x < list2.get(spinnerItem).length(); x++) {
                try {
                    classroom_materials.add(list2.get(spinnerItem).getJSONObject(x).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            help_adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, classroom_materials);
            help_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            binding.materialName.setAdapter(help_adapter2);
            binding.materialName.setOnItemSelectedListener(this);
        }
    }


    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };


    public static int verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
        return permission;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
                        for(int j = 0; j < array.length(); j++){
                            try {
                                String value3 = ((JSONObject) array.get(j)).getString("name");
                                Request request3 = new Request.Builder()
                                        .url("http://192.168.137.1:8000/vis/return_classroom_materials/")
                                        .header("name",value3)
                                        .build();

                                Response response3 = client.newCall(request3).execute();
                                Log.d("HTTPCALL", Integer.toString(response3.code()));
                                if (Integer.toString(response3.code()).equals("200")){
                                    final String user_info1 = response3.body().string();
                                    JSONObject user3= new JSONObject(user_info1);
                                    array2 = user3.getJSONArray("materials");
                                    list2.put(value3,array2);
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


    public class Connection2 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener2 listener;

        public Connection2(OnFinishListener2 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            OkHttpClient client = new OkHttpClient();

                try {

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/return_material")
                            .header("classname",classroomSpinner.getSelectedItem().toString())
                            .header("filename",materialSpinner.getSelectedItem().toString())
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        final String user_info = response.body().string();
                        JSONObject user2 = new JSONObject(user_info);

                        try {
                            File path = Environment.getExternalStoragePublicDirectory(
                                    Environment.DIRECTORY_DOWNLOADS + File.separator + "Vis");
                            File dir = new File(path + File.separator);
                            if (!dir.exists()) dir.mkdir();
                            File file = new File(dir + File.separator + user2.getString("name"));
                            int permision =verifyStoragePermissions(getActivity());
                            if (permision == PackageManager.PERMISSION_GRANTED){
                                byte[] data = Base64.decode(user2.getString("file").getBytes(), Base64.DEFAULT);
                                file.createNewFile();
                                FileOutputStream out = new FileOutputStream(file);
                                out.write(data);
                                out.close();
                            }

                        }
                        catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }

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
            initData();
            binding.loading.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    @Override
    public void onFailed() {
        getActivity().runOnUiThread(() -> {
            Toast.makeText(getActivity(), getString(R.string.login_dialog60), Toast.LENGTH_LONG).show();
            binding.loading.setVisibility(View.GONE);
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        });
    }

    @Override
    public void onSuccess2() {

    }

    @Override
    public void onFailed2() {

    }
}