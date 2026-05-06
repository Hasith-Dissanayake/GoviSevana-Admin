package lk.javainstitute.govisevana_admin.ui.productManagement;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class productManagementViewModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public productManagementViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}