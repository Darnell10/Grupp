package com.example.c4q.capstone.network;

import android.util.Log;

import com.example.c4q.capstone.network.barzz.BarzzService;
import com.example.c4q.capstone.network.barzz.barzzmodel.BarzzModel;
import com.example.c4q.capstone.network.barzz.barzzmodel.Results;
import com.example.c4q.capstone.userinterface.events.VenueNetworkListener;
import com.example.c4q.capstone.userinterface.user.onboarding.BarPreferencesFragment;
import com.example.c4q.capstone.utils.Constants;

import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by amirahoxendine on 3/27/18.
 */

public class NetworkUtility {
    private static NetworkUtility utility;

    public static NetworkUtility getUtility() {
        if (utility == null) {
            utility = new NetworkUtility();
        }
        return utility;
    }

    public void getBarzzList(String zipCode, List<String> preferences, final VenueNetworkListener venueNetworkListener) {


        Call<BarzzModel> call = RetrofitInstance.getInstance()
                .getBarzzApi()
                .getBarzz(zipCode, preferences.get(0));

        call.enqueue(new Callback<BarzzModel>() {
            @Override
            public void onResponse(Call<BarzzModel> call, Response<BarzzModel> response) {

                if(response != null){
                    Log.d("SUCESSSS!", response.body().getSuccess().getResults().get(0).getName());}
                if(response != null) {
                    List<Results> venueResults = response.body().getSuccess().getResults();
                    venueNetworkListener.getBarzList(venueResults);
                }
            }

            @Override
            public void onFailure(Call<BarzzModel> call, Throwable t) {

                t.printStackTrace();

            }
        });

    }

}