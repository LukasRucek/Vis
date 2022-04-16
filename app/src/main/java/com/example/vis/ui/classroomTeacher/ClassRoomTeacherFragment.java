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
import java.util.List;
import java.util.Objects;

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
    ProgressBar progresBar4;
    BottomSheetDialog bottomSheetDialog;
    BottomSheetDialog bottomSheetDialog2;
    JSONArray array;
    JSONObject user;
    EditText namestudent;
    private int PICK_IMAGE_FROM_GALLERY_REQUEST = 1;
    File ahoj;
    EditText name_material;
    byte[] pdfInByte;
    JSONObject student_one_class = new JSONObject();
    String spinnerItem;
    private int REQ_PDF = 21;
    private String encodedPDF;
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
            ProgressBar progress =bottomSheetDialog.findViewById(R.id.progressBar4);
            Button submit = bottomSheetDialog.findViewById(R.id.submit);
            progress.setVisibility(View.GONE);
            Spinner help =bottomSheetDialog.findViewById(R.id.classrooms);
            namestudent = bottomSheetDialog.findViewById(R.id.student_name);
            help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list);
            help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            progresBar4 = bottomSheetDialog.findViewById(R.id.progressBar4);
            progresBar4.setVisibility(View.GONE);
            help.setAdapter(help_adapter);
            help.setOnItemSelectedListener(this);
            bottomSheetDialog.setCanceledOnTouchOutside(false);
            bottomSheetDialog.show();
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
            ProgressBar progress =bottomSheetDialog2.findViewById(R.id.progressBar4);
            Button uploadMaterial = bottomSheetDialog2.findViewById(R.id.uploadMaterial);
            progress.setVisibility(View.GONE);
            Spinner help =bottomSheetDialog2.findViewById(R.id.classrooms);
            name_material = bottomSheetDialog2.findViewById(R.id.name_material);
            help_adapter = new ArrayAdapter<>(getActivity(), R.layout.spinner_item, list2);
            help_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            progresBar4 = bottomSheetDialog2.findViewById(R.id.progressBar4);
            progresBar4.setVisibility(View.GONE);
            help.setAdapter(help_adapter);
            help.setOnItemSelectedListener(this);
            bottomSheetDialog2.setCanceledOnTouchOutside(false);
            bottomSheetDialog2.show();
            Button material = bottomSheetDialog2.findViewById(R.id.chooseMaterial_button);
            TextView materialView = bottomSheetDialog2.findViewById(R.id.chooseMaterial);

            material.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
                    chooseFile.setType("*/*");
                    chooseFile = Intent.createChooser(chooseFile, "Choose a file");
                    startActivityForResult(chooseFile, REQ_PDF);
                }
            });

            uploadMaterial.setOnClickListener(addMaterial2 ->{
                spinnerItem = help.getSelectedItem().toString();
                new Connection3(this).execute();
            });

        });

        return root;
    }
    InputStream inputStream;
    Uri path;

    private File selectedFile;

    private String getPath(Context context, Uri uri) throws URISyntaxException {
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = { "_data" };
            Cursor cursor = null;

            try {
                cursor = context.getContentResolver().query(uri, projection, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }

            } catch (Exception e) {

            }
        }
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

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

                materialsList.add(new TeacherMaterials(((JSONObject) array.get(i)).getString("name"), user.getString("first_name") + " " + user.getString("last_name"), ((JSONObject) array.get(i)).getString("lecture_name"),  students));
                adapter.notifyItemInserted(materialsList.size()-1);
                students = "";

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

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

    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("application/pdf");
    public class Connection3 extends AsyncTask<Void, Void, Void> {

        private OnFinishListener3 listener;

        public Connection3(OnFinishListener3 listener) {
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... help) {
            OkHttpClient client = new OkHttpClient();
                try {

                    RequestBody body = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addFormDataPart("file", encodedPDF)
                            .addFormDataPart("classroom_name", spinnerItem)
                            .addFormDataPart("name",name_material.getText().toString())
                            .build();

                    Request request = new Request.Builder()
                            .url("http://192.168.137.1:8000/vis/materials/"+user.getString("id"))
                            .post(body)
                            .build();

                    Response response = client.newCall(request).execute();
                    Log.d("HTTPCALL", Integer.toString(response.code()));
                    if (Integer.toString(response.code()).equals("200")){


                    }
                    else if (Integer.toString(response.code()).equals("404")){

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

                RequestBody formBody = new FormBody.Builder()
                        .build();
                Request request = new Request.Builder()
                        .url("http://192.168.137.1:8000/vis/add_user/"+spinnerItem+"/"+namestudent.getText().toString()+"/"+user.getString("user_name")+"/")
                        .post(formBody)
                        .build();

                Response response = client.newCall(request).execute();
                Log.d("HTTPCALL", Integer.toString(response.code()));
                if (Integer.toString(response.code()).equals("201")){
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
            Toast.makeText(getActivity(), getString(R.string.login_dialog58), Toast.LENGTH_LONG).show();
            getActivity().recreate();
        });
    }

    @Override
    public void onFailed3() {
        getActivity().runOnUiThread(() -> {
            progresBar4.setVisibility(View.GONE);
            Toast.makeText(getActivity(), getString(R.string.login_dialog59), Toast.LENGTH_LONG).show();
        });
    }
}