package com.example.vis.ui.classroomTeacher;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;


import com.example.vis.OnFinishListener2;
import com.example.vis.OnFinishListener3;
import com.example.vis.R;
import com.example.vis.TeacherAdapter;
import com.example.vis.TeacherMainActivity;
import com.example.vis.TeacherMaterials;
import com.example.vis.databinding.FragmentTeacherclassroomBinding;
import com.google.android.material.bottomsheet.BottomSheetDialog;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class ClassRoomTeacherFragment extends Fragment implements OnFinishListener3, AdapterView.OnItemSelectedListener {

    private FragmentTeacherclassroomBinding binding;
    RecyclerView recyclerView;
    List<TeacherMaterials> materialsList = new ArrayList<>();
    TeacherAdapter adapter;
    LinearLayout layout;
    private ArrayAdapter help_adapter;
    private ArrayAdapter help_adapter2;
    ProgressBar progresBar4;
    ProgressBar progresBar5;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDialog bottomSheetDialog2;
    CheckBox add_removeStudent;
    JSONArray array;
    JSONObject user;
    EditText namestudent;
    EditText name_material;
    JSONObject student_one_class = new JSONObject();
    String spinnerItem;
    String spinnerItem2;
    Intent chooseFile;
    private int REQ_PDF = 21;
    private String encodedPDF;
    Button submit;
    CheckBox remove_material;

    String[] allow_types = { "pdf", "txt","png", "jpeg"};
    Map<String, String> combination = new HashMap<String, String>()
    {
        {
            put("pdf", "application/pdf");
            put("txt", "text/plain");
            put("jpeg", "image/jpeg");
            put("png", "image/png");
        }
    };


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
            ArrayList<String> list = new ArrayList<>();
            for(int i = 0 ; i < array.length();i++){
                try {
                    list.add(((JSONObject) array.get(i)).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            bottomSheetDialog = new BottomSheetDialog(getActivity());
            bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog);
            submit = bottomSheetDialog.findViewById(R.id.submit);
            namestudent = bottomSheetDialog.findViewById(R.id.student_name);
            add_removeStudent = bottomSheetDialog.findViewById(R.id.add_delete);
            Spinner help =bottomSheetDialog.findViewById(R.id.classrooms);
            help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list);
            help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            progresBar4 = bottomSheetDialog.findViewById(R.id.progressBar4);
            progresBar4.setVisibility(View.GONE);
            help.setAdapter(help_adapter);
            help.setOnItemSelectedListener(this);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();
            add_removeStudent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    if (add_removeStudent.isChecked()){
                        submit.setText(getString(R.string.remove_student2));
                    }
                    else{
                        submit.setText(getString(R.string.add_student2));
                    }
                }
            });

            submit.setOnClickListener(addStudent1->{
                if (namestudent.getText().toString().isEmpty()){
                    Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                }
                else{
                    spinnerItem = help.getSelectedItem().toString();
                    progresBar4.setVisibility(View.VISIBLE);
                    new Connection4(this).execute();
                }
            });
        });

        binding.addMaterial.setOnClickListener(addmaterial ->{
            ArrayList<String> list2 = new ArrayList<>();
            for(int i = 0 ; i < array.length();i++){
                try {
                    list2.add(((JSONObject) array.get(i)).getString("name"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            bottomSheetDialog2 = new BottomSheetDialog(getActivity());
            bottomSheetDialog2.setContentView(R.layout.bottom_sheet_dialog2);
            Button uploadMaterial = bottomSheetDialog2.findViewById(R.id.uploadMaterial);
            Spinner help =bottomSheetDialog2.findViewById(R.id.classrooms);
            name_material = bottomSheetDialog2.findViewById(R.id.name_material);
            help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list2);
            help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            Spinner help2 =bottomSheetDialog2.findViewById(R.id.type_material);
            help_adapter2 = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, allow_types);
            help_adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            help2.setAdapter(help_adapter2);
            help2.setOnItemSelectedListener(this);

            progresBar5 = bottomSheetDialog2.findViewById(R.id.progressBar5);
            progresBar5.setVisibility(View.GONE);
            help.setAdapter(help_adapter);
            help.setOnItemSelectedListener(this);
            bottomSheetDialog2.setCanceledOnTouchOutside(false);
            bottomSheetDialog2.show();
            Button material = bottomSheetDialog2.findViewById(R.id.chooseMaterial_button);
            TextView materialView = bottomSheetDialog2.findViewById(R.id.chooseMaterial);
            TextView chooseMaterial = bottomSheetDialog2.findViewById(R.id.chooseMaterial);
            remove_material = bottomSheetDialog2.findViewById(R.id.add_remove_material);
            chooseMaterial.setText(getString(R.string.login_dialog64));
            TextView textview1 = bottomSheetDialog2.findViewById(R.id.textView8);
            TextView textview2 = bottomSheetDialog2.findViewById(R.id.textView7);

            remove_material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (remove_material.isChecked()){
                        uploadMaterial.setText(getString(R.string.remove_material));
                        materialView.setVisibility(View.GONE);
                        chooseMaterial.setVisibility(View.GONE);
                        material.setVisibility(View.GONE);
                        help2.setVisibility(View.GONE);
                        help.setVisibility(View.GONE);
                        textview1.setVisibility(View.GONE);
                        textview2.setVisibility(View.GONE);
                    }
                    else{
                        uploadMaterial.setText(getString(R.string.login_dialog62));
                        materialView.setVisibility(View.VISIBLE);
                        chooseMaterial.setVisibility(View.VISIBLE);
                        material.setVisibility(View.VISIBLE);
                        help.setVisibility(View.VISIBLE);
                        help2.setVisibility(View.VISIBLE);
                        textview1.setVisibility(View.VISIBLE);
                        textview2.setVisibility(View.VISIBLE);
                    }
                }
            });

            material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    spinnerItem2 = help2.getSelectedItem().toString();
                    chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    String allowtype = combination.get(spinnerItem2);
                    chooseFile.setType(allowtype);
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                    if (chooseFile != null){
                        chooseMaterial.setText(getString(R.string.login_dialog65));
                        startActivityForResult(chooseFile, REQ_PDF);
                    }

                }
            });

            uploadMaterial.setOnClickListener(addMaterial2 ->{

                if ((name_material.getText().toString().isEmpty() || chooseFile == null) && !remove_material.isChecked()){
                    Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                }
                else if (name_material.getText().toString().isEmpty() && remove_material.isChecked()){
                    Toast.makeText(getActivity(), getString(R.string.login_dialog52), Toast.LENGTH_LONG).show();
                }
                else{
                    spinnerItem = help.getSelectedItem().toString();
                    progresBar5.setVisibility(View.VISIBLE);
                    new Connection3(this).execute();
                }

            });

        });

        return root;
    }

    Uri path;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQ_PDF && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            path = data.getData();
            try {
                InputStream input = getActivity().getContentResolver().openInputStream(path);
                byte[] pdfInbytes = new byte[input.available()];
                input.read(pdfInbytes);
                encodedPDF = Base64.encodeToString(pdfInbytes, Base64.DEFAULT);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

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

                materialsList.add(new TeacherMaterials(getString(R.string.maretial_info1)+" "+((JSONObject) array.get(i)).getString("name"), getString(R.string.maretial_info2)+"\n"+user.getString("first_name") + " " + user.getString("last_name"), getString(R.string.maretial_info3)+"\n"+((JSONObject) array.get(i)).getString("lecture_name"),  getString(R.string.maretial_info4)+"\n"+students));
                adapter.notifyItemInserted(materialsList.size()-1);
                students = "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {}

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {}

    public class Connection extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection(OnFinishListener3 listener) {
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

        private OnFinishListener3 listener;

        public Connection2(OnFinishListener3 listener) {
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


    public class Connection3 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection3(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... help) {
            OkHttpClient client = new OkHttpClient();
                try {
                    Request request;
                    if (remove_material.isChecked()){
                        request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/delete_material/" + name_material.getText().toString())
                                .delete()
                                .build();
                    }
                    else {
                        RequestBody body = new MultipartBody.Builder()
                                .setType(MultipartBody.FORM)
                                .addFormDataPart("file", encodedPDF)
                                .addFormDataPart("classroom_name", spinnerItem)
                                .addFormDataPart("name", name_material.getText().toString())
                                .addFormDataPart("file_type", spinnerItem2)
                                .build();

                        request = new Request.Builder()
                                .url("http://192.168.137.1:8000/vis/materials/" + user.getString("id"))
                                .post(body)
                                .build();
                    }
                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){
                        listener.onSuccess4();
                    }
                    else {
                        listener.onFailed4();
                    }

                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                }

            return null;
        }

    }


    public class Connection4 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection4(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... help) {
            OkHttpClient client = new OkHttpClient();
            try {
                Request request;
                if (add_removeStudent.isChecked()){
                    request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/add_user/" + spinnerItem + "/" + namestudent.getText().toString() + "/" + user.getString("user_name") + "/")
                            .delete()
                            .build();
                }
                else {
                    RequestBody formBody = new FormBody.Builder()
                            .build();
                    request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/add_user/" + spinnerItem + "/" + namestudent.getText().toString() + "/" + user.getString("user_name") + "/")
                            .post(formBody)
                            .build();
                }
                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("201") || Integer.toString(response.code()).equals("200")){
                    listener.onSuccess3();
                }
                else {
                    listener.onFailed3();
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
        getActivity().runOnUiThread(() -> {
        Toast.makeText(getActivity(), getString(R.string.login_dialog60), Toast.LENGTH_LONG).show();
        });
    }

    @Override
    public void onSuccess3() {
        getActivity().runOnUiThread(() -> {
            progresBar4.setVisibility(View.GONE);
            bottomSheetDialog.dismiss();
            if (add_removeStudent.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog69), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog58), Toast.LENGTH_LONG).show();
            }
            getActivity().recreate();
        });
    }

    @Override
    public void onFailed3() {
        getActivity().runOnUiThread(() -> {
            progresBar4.setVisibility(View.GONE);
            if (add_removeStudent.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog70), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.login_dialog59), Toast.LENGTH_LONG).show();
            }
            });
    }

    @Override
    public void onSuccess4() {
        getActivity().runOnUiThread(() -> {
            if (remove_material.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog71), Toast.LENGTH_LONG).show();
            }
            else {
                Toast.makeText(getActivity(), getString(R.string.login_dialog66), Toast.LENGTH_LONG).show();
            }
            progresBar5.setVisibility(View.GONE);
            bottomSheetDialog2.dismiss();

        });
    }

    @Override
    public void onFailed4() {
        getActivity().runOnUiThread(() -> {
            if (remove_material.isChecked()){
                Toast.makeText(getActivity(), getString(R.string.login_dialog72), Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getActivity(), getString(R.string.login_dialog67), Toast.LENGTH_LONG).show();
            }
            progresBar5.setVisibility(View.GONE);

        });
    }
}