package lk.javainstitute.govisevana_admin.ui.userManagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;
import java.util.List;
import lk.javainstitute.govisevana_admin.model.UserModel;

public class UserManagementViewModel extends ViewModel {

    private final MutableLiveData<List<UserModel>> userList = new MutableLiveData<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserManagementViewModel() {
        loadUsers();
    }

    public LiveData<List<UserModel>> getUserList() {
        return userList;
    }

    private void loadUsers() {
        db.collection("users")
                .addSnapshotListener((queryDocumentSnapshots, e) -> {
                    if (e != null) return;
                    List<UserModel> users = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        users.add(document.toObject(UserModel.class));
                    }
                    userList.setValue(users);
                });
    }
}
