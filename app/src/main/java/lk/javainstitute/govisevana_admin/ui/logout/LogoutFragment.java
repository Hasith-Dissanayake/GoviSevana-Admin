package lk.javainstitute.govisevana_admin.ui.logout;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.io.IOException;

import lk.javainstitute.govisevana_admin.LoginActivity;
import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.model.SharedPreferenceHelper;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LogoutFragment extends Fragment {

    private SharedPreferenceHelper sharedPreferenceHelper;
    private TextView textView2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_logout, container, false);

        sharedPreferenceHelper = new SharedPreferenceHelper(getContext());

        textView2 = view.findViewById(R.id.textView2);

        fetchLogoutText();

        Button buttonLogout = view.findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferenceHelper.logout();

                Toast.makeText(getContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });

        return view;
    }

    private void fetchLogoutText() {

        OkHttpClient client = new OkHttpClient();


        Request request = new Request.Builder()
                .url("https://quiet-griffon-cheaply.ngrok-free.app/GoviSevanaBackend/AdminLogoutTexts")
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                e.printStackTrace();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "Failed to load text", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (response.isSuccessful()) {
                    final String responseBody = response.body().string();


                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            textView2.setText(responseBody);
                        }
                    });
                } else {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), "Error: " + response.code(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }
}
