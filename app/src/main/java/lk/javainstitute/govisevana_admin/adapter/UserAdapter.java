package lk.javainstitute.govisevana_admin.adapter;

import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import java.util.ArrayList;
import java.util.List;
import lk.javainstitute.govisevana_admin.R;
import lk.javainstitute.govisevana_admin.model.UserModel;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<UserModel> userList = new ArrayList<>();
    private List<UserModel> filteredList = new ArrayList<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    public UserAdapter() {
        listenForUserUpdates();
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserModel user = filteredList.get(position);
        updateUI(holder, user);

        holder.toggleStatusButton.setOnClickListener(v -> toggleUserStatus(user, holder, position));
        holder.deleteUserButton.setOnClickListener(v -> deleteUser(user, position, holder));
    }
    @Override
    public int getItemCount() {
        return filteredList.size();
    }
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView userName, userPhone, userType, userStatus;
        Button toggleStatusButton, deleteUserButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            userName = itemView.findViewById(R.id.userName);
            userPhone = itemView.findViewById(R.id.userPhone);
            userType = itemView.findViewById(R.id.userType);
            userStatus = itemView.findViewById(R.id.userStatus);
            toggleStatusButton = itemView.findViewById(R.id.toggleStatusButton);
            deleteUserButton = itemView.findViewById(R.id.deleteUserButton);
        }
    }
    private void listenForUserUpdates() {
        db.collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }
                        List<UserModel> updatedUsers = new ArrayList<>();
                        for (QueryDocumentSnapshot document : value) {
                            UserModel user = document.toObject(UserModel.class);
                            updatedUsers.add(user);
                        }
                        setUserList(updatedUsers);
                    }
                });
    }
    public void setUserList(List<UserModel> users) {
        this.userList = users;
        this.filteredList = new ArrayList<>(users);
        notifyDataSetChanged();
    }
    public void filter(String query) {
        filteredList.clear();
        if (query.isEmpty()) {
            filteredList.addAll(userList);
        } else {
            for (UserModel user : userList) {
                if (user.getFullname().toLowerCase().contains(query.toLowerCase()) ||
                        user.getPhone().contains(query)) {
                    filteredList.add(user);
                }
            }
        }
        notifyDataSetChanged();
    }

    private void toggleUserStatus(UserModel user, ViewHolder holder, int position) {
        boolean newStatus = !user.isActive();
        user.setActive(newStatus);
        updateUI(holder, user);

        db.collection("users").document(user.getPhone())
                .update("active", newStatus)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        notifyItemChanged(position);
                        Toast.makeText(holder.itemView.getContext(), "User status updated!", Toast.LENGTH_SHORT).show();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        user.setActive(!newStatus);
                        updateUI(holder, user);
                        Toast.makeText(holder.itemView.getContext(), "Update failed!", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void deleteUser(UserModel user, int position, ViewHolder holder) {
        new AlertDialog.Builder(holder.itemView.getContext())
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getFullname() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("users").document(user.getPhone())
                            .delete()
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    userList.remove(user);
                                    filteredList.remove(user);
                                    notifyDataSetChanged();
                                    Toast.makeText(holder.itemView.getContext(), "User deleted!", Toast.LENGTH_SHORT).show();

                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(holder.itemView.getContext(), "Delete failed!", Toast.LENGTH_SHORT).show();

                                }
                            });
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }
    private void updateUI(ViewHolder holder, UserModel user) {
        holder.userName.setText(user.getFullname());
        holder.userPhone.setText(user.getPhone());
        holder.userType.setText(user.getUsertype());

        if (user.isActive()) {
            holder.userStatus.setText("Active");
            holder.userStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.toggleStatusButton.setText("Deactivate");
        } else {
            holder.userStatus.setText("Inactive");
            holder.userStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            holder.toggleStatusButton.setText("Activate");
        }
    }

}
