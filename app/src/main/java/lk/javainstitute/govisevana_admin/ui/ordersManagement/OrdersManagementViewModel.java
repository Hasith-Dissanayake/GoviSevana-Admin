package lk.javainstitute.govisevana_admin.ui.ordersManagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.List;

import lk.javainstitute.govisevana_admin.model.OrderModel;

public class OrdersManagementViewModel extends ViewModel {
    private final MutableLiveData<List<OrderModel>> ordersList;

    public OrdersManagementViewModel() {
        ordersList = new MutableLiveData<>();
    }

    public LiveData<List<OrderModel>> getOrders() {
        return ordersList;
    }

    public void setOrders(List<OrderModel> orders) {
        ordersList.setValue(orders);
    }
}
