package com.dwikyhutomo.e_bioskop.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public HomeViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("DWIKY SATRIA HUTOMO-201118001");
    }

    public LiveData<String> getText() {
        return mText;
    }
}