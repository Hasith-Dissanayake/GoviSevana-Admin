package lk.javainstitute.govisevana_admin.ui.userManagement;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.adapter.UserAdapter;
import lk.javainstitute.govisevana_admin.model.UserModel;
import java.util.List;

public class UserManagementFragment extends Fragment {
    private RecyclerView recyclerView;
    private UserAdapter userAdapter;
    private SearchView searchView;
    private ProgressBar progressBar;
    private UserManagementViewModel viewModel;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_management, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewUsers);
        searchView = view.findViewById(R.id.searchUserView);
        progressBar = view.findViewById(R.id.progressBarUsers);
        viewModel = new ViewModelProvider(this).get(UserManagementViewModel.class);
        userAdapter = new UserAdapter();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(userAdapter);
        viewModel.getUserList().observe(getViewLifecycleOwner(), users -> {
            progressBar.setVisibility(View.GONE);
            userAdapter.setUserList(users);
        });
        setupSearchView();
        return view;
    }
    private void setupSearchView() {
        searchView.setIconifiedByDefault(false);
        searchView.setFocusable(true);
        searchView.setFocusableInTouchMode(true);
        searchView.requestFocus();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                userAdapter.filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                userAdapter.filter(newText);
                return false;
            }
        });
        searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchView.setIconified(false);
            }
        });
    }

}
