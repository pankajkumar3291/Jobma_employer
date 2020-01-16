package com.jobma.employer.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jobma.employer.R;
import com.jobma.employer.activities.ActivityFilter;
import com.jobma.employer.adapters.SubAccountAdapter;
import com.jobma.employer.application.ApplicationHelper;
import com.jobma.employer.components.SessionSecuredPreferences;
import com.jobma.employer.dialogs.GlobalProgressDialog;
import com.jobma.employer.model.reportIssue.GetUserList;
import com.jobma.employer.model.subAccounts.EOSubAccounts;
import com.jobma.employer.networking.APIClient;
import com.jobma.employer.util.ObjectUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.jobma.employer.util.Constants.LOGIN_SIGNUP_PREFERENCE;
import static com.jobma.employer.util.Constants.RESPONSE_SUCCESS;
import static com.jobma.employer.util.Constants.SELECTED_API_KEY;

public class FragmentSubAccount extends Fragment implements View.OnClickListener {

    private GlobalProgressDialog progress;
    private APIClient.APIInterface apiInterface;
    private SessionSecuredPreferences loginPreferences;
    private String apiKey;
    private View view;
    private RecyclerView recyclerViewSubAccountt;
    private FloatingActionButton btnFilter;
    private TextView noDataAvailable;

    private int offset = 1;
    private int remainingCounts;
    private static final String PER_PAGE_ITEMS = "10";

    private ArrayList<GetUserList> subAccountsDataArrayList = new ArrayList<>();
    private SubAccountAdapter subAccountAdapter;
    private Map<String, String> params = new HashMap<>();

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_sub_account, container, false);

        this.initView();

        return view;
    }

    private void initView() {
        this.progress = new GlobalProgressDialog(getActivity());
        this.apiInterface = APIClient.getClient();
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_SIGNUP_PREFERENCE);
        this.apiKey = loginPreferences.getString(SELECTED_API_KEY, "");

        this.recyclerViewSubAccountt = view.findViewById(R.id.recyclerViewSubAccountt);
        this.btnFilter = view.findViewById(R.id.floatingActionButton);
        this.noDataAvailable = view.findViewById(R.id.textView204);
        this.btnFilter.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getAllSubAccountList();
    }

    private void getAllSubAccountList() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            progress.showProgressBar();
            apiInterface.subAccountList(apiKey, params).enqueue(new Callback<EOSubAccounts>() {
                @Override
                public void onResponse(Call<EOSubAccounts> call, Response<EOSubAccounts> response) {
                    progress.hideProgressBar();
                    if (!ObjectUtil.isEmpty(response.body())) {

                        EOSubAccounts eoSubAccounts = response.body();

                        if (!ObjectUtil.isEmpty(eoSubAccounts)) {
                            if (eoSubAccounts.getError() == RESPONSE_SUCCESS) {
                                //TODO At first time load by default first page

                                subAccountsDataArrayList.addAll(eoSubAccounts.getData().getGetUserList());
                                if (!ObjectUtil.isEmpty(eoSubAccounts.getData().getRemaining()))
                                    remainingCounts = eoSubAccounts.getData().getRemaining();

                                if (!ObjectUtil.isEmpty(subAccountsDataArrayList)) {
                                    noDataAvailable.setVisibility(View.GONE);
                                    recyclerViewSubAccountt.setVisibility(View.VISIBLE);
                                    recyclerViewSubAccountt.setHasFixedSize(true);
                                    subAccountAdapter = new SubAccountAdapter(getContext(), subAccountsDataArrayList);
                                    recyclerViewSubAccountt.setItemAnimator(new DefaultItemAnimator());
                                    recyclerViewSubAccountt.setAdapter(subAccountAdapter);
                                } else {
                                    noDataAvailable.setVisibility(View.VISIBLE);
                                    recyclerViewSubAccountt.setVisibility(View.GONE);
                                }

                                //TODO when user scroll then this api will call again & again
                                recyclerViewSubAccountt.addOnScrollListener(new RecyclerView.OnScrollListener() {
                                    @Override
                                    public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                                        super.onScrollStateChanged(recyclerView, newState);
                                    }

                                    @Override
                                    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                        super.onScrolled(recyclerView, dx, dy);
                                        LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                                        if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == subAccountsDataArrayList.size() - 1) {
                                            offset += 10;
                                            loadNextPage();
                                        }
                                    }
                                });

                            } else {
                                progress.hideProgressBar();
                                noDataAvailable.setVisibility(View.VISIBLE);
                                recyclerViewSubAccountt.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), "" + eoSubAccounts.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Call<EOSubAccounts> call, Throwable t) {
                    if (t.getMessage() != null) {
                        progress.hideProgressBar();
                        Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void loadNextPage() {
        if (!ObjectUtil.isEmpty(this.apiKey)) {

            params.put("offset", String.valueOf(offset));
            params.put("limit", PER_PAGE_ITEMS);

            if (remainingCounts > 0) {
                progress.showProgressBar();
                apiInterface.subAccountList(apiKey, params).enqueue(new Callback<EOSubAccounts>() {
                    @Override
                    public void onResponse(Call<EOSubAccounts> call, Response<EOSubAccounts> response) {
                        progress.hideProgressBar();
                        if (!ObjectUtil.isEmpty(response.body())) {
                            EOSubAccounts eoSubAccounts = response.body();
                            if (!ObjectUtil.isEmpty(eoSubAccounts)) {
                                if (eoSubAccounts.getError() == RESPONSE_SUCCESS) {

                                    subAccountsDataArrayList.addAll(eoSubAccounts.getData().getGetUserList());
                                    if (!ObjectUtil.isEmpty(eoSubAccounts.getData().getRemaining()))
                                        remainingCounts = eoSubAccounts.getData().getRemaining();

                                    if (!ObjectUtil.isEmpty(subAccountsDataArrayList)) {
                                        noDataAvailable.setVisibility(View.GONE);
                                        recyclerViewSubAccountt.setVisibility(View.VISIBLE);
                                        subAccountAdapter.notifyDataSetChanged();
                                    }
                                } else {
                                    progress.hideProgressBar();
                                    noDataAvailable.setVisibility(View.VISIBLE);
                                    recyclerViewSubAccountt.setVisibility(View.GONE);
                                    Toast.makeText(getActivity(), "" + eoSubAccounts.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<EOSubAccounts> call, Throwable t) {
                        if (t.getMessage() != null) {
                            progress.hideProgressBar();
                            Toast.makeText(getActivity(), "Failed Error" + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == 111) {
            params = (Map<String, String>) data.getSerializableExtra("mapData");
            subAccountsDataArrayList.clear();
            offset = 1;
            remainingCounts = 0;
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.floatingActionButton) {
            Intent intent = new Intent(getActivity(), ActivityFilter.class);
            intent.putExtra("subAccount", "subaccount");
            startActivityForResult(intent, 111);
        }
    }

}
