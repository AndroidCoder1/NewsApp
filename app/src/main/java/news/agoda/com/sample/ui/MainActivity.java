package news.agoda.com.sample.ui;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import com.facebook.drawee.backends.pipeline.Fresco;

import java.util.ArrayList;

import javax.inject.Inject;

import news.agoda.com.sample.R;
import news.agoda.com.sample.api.NewsViewModelFactory;
import news.agoda.com.sample.base.BaseActivity;
import news.agoda.com.sample.databinding.ActivityMainBinding;
import news.agoda.com.sample.ui.viewmodel.NewsViewModel;
import news.agoda.com.sample.util.Utilities;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private NewsAdapter newsAdapter;
    private NewsViewModel newsViewModel;

    @Inject
    NewsViewModelFactory newsViewModelFactory;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        newsViewModel = ViewModelProviders.of(this, newsViewModelFactory).get(NewsViewModel.class);

        initNewsDataAdapter();
        loadNewsData();
        observeDataChange();
    }

    private void loadNewsData() {
        if (newsViewModel.getNewsData().getValue() != null) {
            newsAdapter.addNewsList(newsViewModel.getNewsData().getValue().getResults());

        } else {
            if (Utilities.isNetworkConnected(this)) {
                newsViewModel.fetchNews();
            } else {
                showToast(getString(R.string.internet_error));
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    private void initNewsDataAdapter() {
        newsAdapter = new NewsAdapter(this, new ArrayList<>());
        binding.rvNewsList.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNewsList.setAdapter(newsAdapter);
    }

    private void observeDataChange() {

        newsViewModel.getApiError().observe(this, error -> {
            showToast(getString(R.string.some_error));
        });


        newsViewModel.getLoading().observe(this, isLoading -> {
            if (isLoading) {
                binding.setShowLoading(true);
            } else {
                binding.setShowLoading(false);
            }
        });

        newsViewModel.getNewsData().observe(this, newsData -> {
            newsAdapter.addNewsList(newsData.getResults());

        });

    }

}